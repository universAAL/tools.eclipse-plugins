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

package org.universaal.uaalpax.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ArtifactNode {
	private ArtifactURL artifact;
	
	private Set<ArtifactNode> children;
	private Set<ArtifactNode> parents;
	
	private List<BundleEntry> bundleEntries;
	
	public ArtifactNode(ArtifactURL artifact) {
		this.artifact = artifact;
		this.children = new HashSet<ArtifactNode>();
		this.parents = new HashSet<ArtifactNode>();
		
		this.bundleEntries = new LinkedList<BundleEntry>();
	}
	
	public List<BundleEntry> getBundleEntries() {
		return bundleEntries;
	}
	
	public ArtifactURL getArtifact() {
		return artifact;
	}
	
	public Set<ArtifactNode> getChildren() {
		return children;
	}
	
	public Set<ArtifactNode> getParents() {
		return parents;
	}
	
	public void addBundleEntry(BundleEntry be) {
		bundleEntries.add(be);
	}
	
	public void addChild(ArtifactNode child) {
		if (children.add(child))
			child.addParent(this);
	}
	
	private void addParent(ArtifactNode parent) {
		parents.add(parent);
	}
	
	private void removeChild(ArtifactNode c) {
		children.remove(c);
	}
	
	private void removeParent(ArtifactNode p) {
		parents.remove(p);
	}
	
	public void removeSelf() {
		for(ArtifactNode parent: getParents())
			parent.removeChild(this);
		
		for(ArtifactNode child: getChildren())
			child.removeParent(this);
		
		parents.clear();
		children.clear();
		bundleEntries.clear();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getArtifact()).append("\n");
		
		sb.append("parents\n");
		for (ArtifactNode p : getParents())
			sb.append("  ").append(p.getArtifact()).append("\n");
		
		sb.append("children\n");
		for (ArtifactNode c : getChildren())
			sb.append("  ").append(c.getArtifact()).append("\n");
		
		sb.append("bundles\n");
		for (BundleEntry b : getBundleEntries())
			sb.append("  ").append(b).append("\n");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return artifact.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
