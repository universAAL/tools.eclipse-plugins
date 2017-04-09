package org.universaal.uaalpax.maven;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.examples.util.ConsoleDependencyGraphDumper;
import org.eclipse.aether.graph.DependencyNode;

public class Test {
	public static void main(String[] args) {
		Artifact a = new DefaultArtifact("org.universAAL.ontology",
				"ont.device", "jar", "3.4.1-SNAPSHOT");

		try {
			long l1 = System.currentTimeMillis();
			DependencyNode dNode = MavenDependencyResolver.getResolver()
					.resolveDependenciesBlocking(a);
			long l2 = System.currentTimeMillis();

			System.out.println("Tree of Artifact " + a);
			ConsoleDependencyGraphDumper dumper = new ConsoleDependencyGraphDumper();
			dNode.accept(dumper);
			System.out.println("Time: " + (l2 - l1));
		} catch (DependencyCollectionException e) {
			e.printStackTrace();
		}
	}
}
