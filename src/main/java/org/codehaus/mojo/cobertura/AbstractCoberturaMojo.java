package org.codehaus.mojo.cobertura;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.cobertura.configuration.ConfigCheck;
import org.codehaus.mojo.cobertura.configuration.ConfigInstrumentation;
import org.codehaus.mojo.cobertura.tasks.AbstractTask;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.factory.ArtifactFactory;

/**
 * Abstract Base for Cobertura Mojos.
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public abstract class AbstractCoberturaMojo
    extends AbstractMojo {
    /**
     * <i>Maven Internal</i>: Project to interact with.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @component
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    protected ArtifactFactory artifactFactory;

    /**
     * @component
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    protected ArtifactRepository artifactRepository;

    /**
     * @component
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    protected ArtifactResolver artifactResolver;

    /**
     * The directory where we put the instrumented files.
     *
     * @parameter default-value="${project.build.directory}/generated-classes/cobertura"
     * @required
     * @readonly
     */
    protected String instrumentedDir;


    /**
     * The inherited projects info.
     *
     * @parameter expression="${coberutra.inheritedProjects}"
     */
    protected ArrayList<InheritProject> inheritProjects;
    
    /**
     * Maximum memory to pass JVM of Cobertura processes.
     * 
     * @parameter expression="${cobertura.maxmem}"
     */
    private String maxmem = "64m";

    /**
     * <p>
     * The Datafile Location.
     * </p>
     * 
     * @parameter expression="${cobertura.datafile}" default-value="${project.build.directory}/cobertura/cobertura.ser"
     * @required
     * @readonly
     */
    protected File dataFile;

    /**
     * The <a href="usage.html#Check">Check Configuration</a>.
     * 
     * @parameter expression="${check}"
     */
    protected ConfigCheck check;

    /**
     * The <a href="usage.html#Instrumentation">Instrumentation Configuration</a>.
     * 
     * @parameter expression="${instrumentation}"
     */
    protected ConfigInstrumentation instrumentation;

    /**
     * Only output coberura errors, avoid info messages.
     * 
     * @parameter expression="${quiet}" default-value="false"
     */
    private boolean quiet;

    /**
     * <i>Maven Internal</i>: List of artifacts for the plugin.
     * 
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    protected List pluginClasspathList;

    /**
     * @return Returns the inherited projects.
     */
    protected ArrayList<InheritProject> getInheritProjects() {
        return this.inheritProjects;
    }

    /**
     * @param inheritProjects the inheritProjects to set.
     */
    public void setInheritProjects(ArrayList<InheritProject> inheritProjects) {
        this.inheritProjects = inheritProjects;
    }
    
    /**
     * Setup the Task defaults.
     * 
     * @param task the task to setup.
     */
    public void setTaskDefaults(AbstractTask task) {
        task.setLog(getLog());
        task.setPluginClasspathList(pluginClasspathList);
        task.setMaxmem(maxmem);
        task.setQuiet(quiet);
    }
}
