/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut für Graphische Datenverarbeitung
	
	See the NOTICE file distributed with this work for additional 
	information regarding copyright ownership
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	  http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package org.universaal.uaalpax.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.examples.util.Booter;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.universaal.uaalpax.model.ArtifactURL;
import org.universaal.uaalpax.model.BundleEntry;


public class MavenDependencyResolver {
	private static MavenDependencyResolver theResolver;
	
	private RepositorySystem system;
	private RepositorySystemSession session;
	private List<RemoteRepository> repos;
	
	private Map<Object, DependencyNode> dependencyCache;
	private Map<Artifact, Artifact> artifactCache;
	private Map<String, Boolean> wrapCache;
	
	private Composite guiParent;
	
	private MavenDependencyResolver() {
		system = Booter.newRepositorySystem();
		session = Booter.newRepositorySystemSession(system);
		repos = Booter.newRepositories();
		
		dependencyCache = new HashMap<Object, DependencyNode>();
		artifactCache = new HashMap<Artifact, Artifact>();
		wrapCache = new HashMap<String, Boolean>();
	}
	
	public static MavenDependencyResolver getResolver() {
		if (theResolver == null) {
			synchronized (MavenDependencyResolver.class) {
				if (theResolver == null)
					theResolver = new MavenDependencyResolver();
			}
		}
		return theResolver;
	}
	
	public void setGUIParent(Composite guiParent) {
		this.guiParent = guiParent;
		
	}
	
	public void clearCache() {
		dependencyCache.clear();
		artifactCache.clear();
		wrapCache.clear();
	}
	
	public List<Dependency> getDirectDependencies(Artifact artifact) throws ArtifactDescriptorException {
		ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
		descriptorRequest.setArtifact(artifact);
		descriptorRequest.setRepositories(repos);
		
		ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
		
		return descriptorResult.getDependencies();
	}
	
	public Artifact resolveArtifact(Artifact artifact) {
		Artifact resolved = artifactCache.get(artifact);
		
		if (resolved == null) {
			ArtifactRequest artifactRequest = new ArtifactRequest();
			artifactRequest.setArtifact(artifact);
			artifactRequest.setRepositories(Booter.newRepositories());
			
			try {
				ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);
				resolved = artifactResult.getArtifact();
				artifactCache.put(artifact, resolved);
				
			} catch (ArtifactResolutionException e) {
			}
			
		}
		return resolved;
	}
	
	public DependencyNode resolveDependencies(Artifact artifact) throws DependencyCollectionException, TimeoutException {
		ArtifactURL url = BundleEntry.artifactUrlFromArtifact(artifact);
		
		DependencyNode artifactResults = dependencyCache.get(url);
		if (artifactResults == null) {
			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
			collectRequest.setRepositories(repos);
			
			artifactResults = doCollectDependencies(collectRequest);
		}
		
		return artifactResults;
	}
	
	public DependencyNode resolveDependenciesBlocking(Artifact artifact) throws DependencyCollectionException {
		System.out.println("resolving " + artifact);
		ArtifactURL url = BundleEntry.artifactUrlFromArtifact(artifact);
		
		DependencyNode artifactResults = dependencyCache.get(url);
		if (artifactResults == null) {
			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
			collectRequest.setRepositories(repos);
			
			artifactResults = system.collectDependencies(session, collectRequest).getRoot();
			cacheDependencies(artifactResults);
		}
		
		System.out.println("resolved " + artifact);
		return artifactResults;
	}
	
	public boolean isWrapArtifact(Artifact artifact) {
		Boolean wrap = wrapCache.get(BundleEntry.artifactUrlFromArtifact(artifact).url);
		if (wrap == null) {
			artifact = resolveArtifact(artifact);
			if (artifact == null)
				return true;
			
			File jarPath = artifact.getFile();
			JarInputStream jio;
			
			try {
				jio = new JarInputStream(new FileInputStream(jarPath));
			} catch (FileNotFoundException e) {
				return true;
			} catch (IOException e) {
				return true;
			}
			
			Manifest manifest = jio.getManifest();
			Object bundleManifestVersion = null;
			if (manifest != null) {
				Attributes attribs = manifest.getMainAttributes();
				bundleManifestVersion = attribs.getValue("Bundle-ManifestVersion");
			}
			
			wrap = Boolean.valueOf(manifest == null || bundleManifestVersion == null);
			wrapCache.put(BundleEntry.artifactUrlFromArtifact(artifact).url, wrap);
			try {
				jio.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return wrap.booleanValue();
	}
	
	private void cacheDependencies(DependencyNode node) {
		Dependency d = node.getDependency();
		if (d != null && d.getArtifact() != null)
			dependencyCache.put(BundleEntry.artifactUrlFromArtifact(d.getArtifact()), node);
		
		for (DependencyNode child : node.getChildren())
			cacheDependencies(child);
	}
	
	public DependencyNode resolve(Set<Artifact> artifacts) throws DependencyCollectionException, TimeoutException {
		DependencyNode artifactResults = dependencyCache.get(artifacts);
		if (artifactResults == null) {
			CollectRequest collectRequest = new CollectRequest();
			for (Artifact a : artifacts)
				collectRequest.addDependency(new Dependency(a, JavaScopes.COMPILE));
			collectRequest.setRepositories(repos);
			
			artifactResults = doCollectDependencies(collectRequest);
		}
		
		return artifactResults;
	}
	
	private DependencyNode doCollectDependencies(final CollectRequest collectRequest) throws DependencyCollectionException,
			TimeoutException {
		final DependencyNode[] artifactResults = new DependencyNode[1];
		final DependencyCollectionException[] exception = new DependencyCollectionException[1];
		
		final Thread[] thread = new Thread[1];
		
		ProgressMonitorDialog d = new ProgressMonitorDialog(guiParent.getShell()) {
			@Override
			protected void cancelPressed() {
				super.cancelPressed();
				synchronized (thread) {
					while (thread[0] == null)
						try {
							thread.wait();
						} catch (InterruptedException e) {
						}
				}
				
				thread[0].interrupt();
			}
		};
		
		try {
			d.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor mon) throws InvocationTargetException, InterruptedException {
					try {
						synchronized (thread) {
							thread[0] = Thread.currentThread();
							thread.notify();
						}
						
						mon.beginTask("dependency resolution", IProgressMonitor.UNKNOWN);
						mon.subTask("Retrieving metadata for bundles");
						
						try {
							artifactResults[0] = system.collectDependencies(session, collectRequest).getRoot();
							mon.worked(100);
							
						} catch (DependencyCollectionException e) {
							exception[0] = e;
							throw new InvocationTargetException(e);
						}
					} finally {
						mon.done();
					}
				}
			});
		} catch (InvocationTargetException e1) {
			if (exception[0] != null)
				throw exception[0];
		} catch (InterruptedException e1) {
			throw new TimeoutException();
		}
		
		if (artifactResults[0] == null)
			throw new TimeoutException();
		
		cacheDependencies(artifactResults[0]);
		return artifactResults[0];
	}
}
