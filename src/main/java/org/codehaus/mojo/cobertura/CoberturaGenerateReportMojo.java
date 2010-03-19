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

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.cobertura.tasks.GenerateReportTask;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.reporting.MavenReportException;

import java.util.ArrayList;
import java.util.List;

/**
 * Cobertura report generation.
 * 
 * @author Witek Wołejszo ww@touk.pl
 * @goal generate-report
 */
public class CoberturaGenerateReportMojo extends AbstractCoberturaMojo {

    /**
     * Maven ProjectHelper
     * 
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    /**
     * The format of the report. (can be 'html' and/or 'xml'. defaults to 'html')
     * 
     * @parameter
     */
    private String[] formats = new String[] { "html" };

    /**
     * The output directory for the report.
     * 
     * @parameter default-value="${project.build.directory}/site/cobertura"
     * @required
     */
    private File outputDirectory;
    
    private void executeGenerateReportTask(GenerateReportTask task, String format) {
        task.setOutputFormat(format);

        // execute task
        try {
            task.execute();
        }
        catch (MojoExecutionException e) {
            // throw new MavenReportException("Error in Cobertura Report generation: " + e.getMessage(), e);
            // better don't break the build if report is not generated, also due to the sporadic MCOBERTURA-56
            getLog().error("Error in Cobertura Report generation: " + e.getMessage(), e);
        }
    }


    public void execute() throws MojoExecutionException {
        
        ArtifactHandler artifactHandler = project.getArtifact().getArtifactHandler();
        
        if (!"java".equals(artifactHandler.getLanguage())) {
            getLog().info("Not executing cobertura:generate-report as the project is not a Java classpath-capable package");
        } else {

            if (!dataFile.exists()) {
                getLog().info("Cannot perform generate-report, instrumentation not performed - skipping.");
            } else {
                GenerateReportTask task = new GenerateReportTask();
                setTaskDefaults(task);
                task.setDataFile(dataFile);
                task.setOutputDirectory(outputDirectory);
                List<String> allSourceRoots = new ArrayList<String>();
                for (InheritProject ip : inheritProjects) {
                    allSourceRoots.add(ip.getRelativeSourcePath());
                }
                allSourceRoots.addAll(project.getCompileSourceRoots());
                task.setCompileSourceRoots(allSourceRoots);
                for (int i = 0; i < formats.length; i++) {
                    executeGenerateReportTask(task, formats[i]);
                }
                
                projectHelper.attachArtifact(project, "ser", "cobertura", dataFile); 
                
            }
            
            String originalOutputDirectory = System.getProperty("original.project.build.outputDirectory");
            getLog().info("Current output directory: " + System.getProperty("project.build.outputDirectory"));
            getLog().info("Original output directory: " + originalOutputDirectory);
            getLog().info("Changing back to original directory.");
            
            if (originalOutputDirectory != null) {
                project.getBuild().setOutputDirectory(originalOutputDirectory);
                System.setProperty("project.build.outputDirectory", originalOutputDirectory);
                System.setProperty("original.project.build.outputDirectory" , "");
            }
            
        }
        
    }
    
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }
}
