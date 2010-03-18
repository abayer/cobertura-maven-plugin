package org.codehaus.mojo.cobertura.tasks;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * The Generate Report Task.
 * 
 * @author Witek Wołejszo ww@touk.pl
 */
public class GenerateReportTask extends AbstractTask {

    private File dataFile;
    
    private File outputDirectory;

    private String outputFormat;

    private List compileSourceRoots;


    public GenerateReportTask() {
        super("net.sourceforge.cobertura.reporting.Main");
    }

    public void execute() throws MojoExecutionException {
        getOutputDirectory().mkdirs();

        for (Iterator i = getCompileSourceRoots().iterator(); i.hasNext();) {
            String directory = (String) i.next();
            if (!directory.equals("--source")) 
                cmdLineArgs.addArg("--source", directory);
        }

        if (getOutputDirectory() != null) {
            cmdLineArgs.addArg("--destination", getOutputDirectory().getAbsolutePath());
        }

        if (dataFile != null) {
            cmdLineArgs.addArg("--datafile", dataFile.getAbsolutePath());
        }

        if (StringUtils.isNotEmpty(getOutputFormat())) {
            cmdLineArgs.addArg("--format", getOutputFormat());
        }

        int returnCode = executeJava();

        // Check the return code and print a message
        if (returnCode == 0) {
            getLog().info("Cobertura Report generation was successful.");
        }
        else {
            throw new MojoExecutionException("Unable to generate Cobertura Report for project.");
        }

    }

    /**
     * @return Returns the dataFile.
     */
    public File getDataFile() {
        return dataFile;
    }

    /**
     * @param dataFile The dataFile to set.
     */
    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setCompileSourceRoots(List compileSourceRoots) {
        this.compileSourceRoots = Collections.unmodifiableList(compileSourceRoots);
    }

    public List getCompileSourceRoots() {
        return compileSourceRoots;
    }

}
