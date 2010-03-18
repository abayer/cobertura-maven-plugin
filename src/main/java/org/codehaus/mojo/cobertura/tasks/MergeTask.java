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
 * The Merge Task.
 * 
 * @author Andrew Bayer
 */
public class MergeTask extends AbstractTask {

    private List<File> sourceDataFiles;
    private File dataFile;

    
    public MergeTask() {
        super("net.sourceforge.cobertura.merge.Main");
    }

    public void execute() throws MojoExecutionException {
        cmdLineArgs.addArg("--datafile", dataFile.getAbsolutePath());
        
        for (File sdf : sourceDataFiles) {
            cmdLineArgs.addArg(sdf.getAbsolutePath());
        }
        
        int returnCode = executeJava();

        // Check the return code and print a message
        if (returnCode == 0) {
            getLog().info("Cobertura datafile merge was successful.");
        }
        else {
            throw new MojoExecutionException("Unable to merge Cobertura datafiles for project.");
        }

    }

    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
    }

    public File getDataFile() {
        return dataFile;
    }
    
    public void setSourceDataFiles(List<File> sourceDataFiles) {
        this.sourceDataFiles = Collections.unmodifiableList(sourceDataFiles);
    }

    public List<File> getSourceDataFiles() {
        return sourceDataFiles;
    }

}
