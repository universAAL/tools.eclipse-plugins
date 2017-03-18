/*******************************************************************************
 * Copyright (c) 2010, 2014 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.examples.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

/**
 * A helper to boot the repository system and a repository system session.
 */
public class Booter
{

    public static RepositorySystem newRepositorySystem()
    {
        return org.eclipse.aether.examples.manual.ManualRepositorySystemFactory.newRepositorySystem();
        // return org.eclipse.aether.examples.guice.GuiceRepositorySystemFactory.newRepositorySystem();
        // return org.eclipse.aether.examples.sisu.SisuRepositorySystemFactory.newRepositorySystem();
        // return org.eclipse.aether.examples.plexus.PlexusRepositorySystemFactory.newRepositorySystem();
    }

	public static String getLocalRepositoryLocation() {
		System.out.println(" -- getLocalRepositoryLocation: " + MavenCli.userMavenConfigurationHome + File.separator + "repository");
		return MavenCli.userMavenConfigurationHome + File.separator + "repository";
	}

	public static DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system )
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

//        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
        LocalRepository localRepo = new LocalRepository( getLocalRepositoryLocation() );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );
//		LocalRepository localRepo = new LocalRepository(getLocalRepositoryLocation());
//		// LocalRepository localRepo = new LocalRepository("C:\\Users\\jgdo\\.m2\\repository");
//		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        session.setTransferListener( new ConsoleTransferListener() );
        session.setRepositoryListener( new ConsoleRepositoryListener() );

        // uncomment to generate dirty trees
		// dirty tree: every child node contains all dependencies; not dirty: every dependency is contained at most once
        session.setDependencyGraphTransformer( null );

        return session;
    }

    public static List<RemoteRepository> newRepositories( RepositorySystem system, RepositorySystemSession session )
    {
        return new ArrayList<RemoteRepository>( Arrays.asList( newCentralRepository() ) );
    }

    private static RemoteRepository newCentralRepository()
    {
        return new RemoteRepository.Builder( "central", "default", "http://central.maven.org/maven2/" ).build();
    }
    
    
	private static RemoteRepository getRepo(String id, String url) {
		return new RemoteRepository.Builder(id, "default", url)
				.setReleasePolicy(new RepositoryPolicy(true, RepositoryPolicy.UPDATE_POLICY_DAILY,
						RepositoryPolicy.CHECKSUM_POLICY_WARN))
				.setSnapshotPolicy(new RepositoryPolicy(true, RepositoryPolicy.UPDATE_POLICY_ALWAYS,
						RepositoryPolicy.CHECKSUM_POLICY_WARN))
				.build();
	}

	public static List<RemoteRepository> newRepositories() {
		List<RemoteRepository> repos = new ArrayList<RemoteRepository>();
		repos.add(getRepo("central", "http://repo1.maven.org/maven2/"));
		repos.add(getRepo("uaal", "http://depot.universaal.org/maven-repo/releases/"));
		repos.add(getRepo("uaal-snapshots", "http://depot.universaal.org/maven-repo/snapshots/"));
		repos.add(getRepo("uaal-thirdparty", "http://depot.universaal.org/maven-repo/thirdparty/"));
		repos.add(getRepo(">maven2-repository.java.net", "http://download.java.net/maven/2"));
		repos.add(getRepo("apache-snapshots", "http://people.apache.org/repo/m2-snapshot-repository"));
		repos.add(getRepo("ops4j", "http://repository.ops4j.org/maven2"));
		//repos.add(getRepo("xxxx", "xxxx"));
				
//		r = new RemoteRepository("ima-thirdparty", "default", "http://v2me.igd.fraunhofer.de/nexus/content/repositories/thirdparty/");
//		r.setPolicy(true, r.getPolicy(true).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
//		r.setPolicy(false, r.getPolicy(false).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
//		repos.add(r);
//		
//		r = new RemoteRepository("ima-release", "default", "http://v2me.igd.fraunhofer.de/nexus/content/repositories/releases/");
//		r.setPolicy(true, r.getPolicy(true).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
//		r.setPolicy(false, r.getPolicy(false).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
//		repos.add(r);
//		
//		r = new RemoteRepository("ima-snapshot", "default", "http://v2me.igd.fraunhofer.de/nexus/content/repositories/snapshots/");
//		r.setPolicy(true, r.getPolicy(true).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
//		r.setPolicy(false, r.getPolicy(false).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
//		repos.add(r);
		
		// r = new RemoteRepository("igd_releases", "default", "http://a1gforge.igd.fraunhofer.de/nexus/content/repositories/releases/");
		// r.setPolicy(true, r.getPolicy(true).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
		// r.setPolicy(false, r.getPolicy(false).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
		// repos.add(r);
		//
		// r = new RemoteRepository("igd_snapshots", "default", "http://a1gforge.igd.fraunhofer.de/nexus/content/repositories/snapshots/");
		// r.setPolicy(true, r.getPolicy(true).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
		// r.setPolicy(false, r.getPolicy(false).setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER));
		// repos.add(r);
		
		return repos;
	}
}
