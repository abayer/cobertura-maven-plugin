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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.Collections;
import org.codehaus.plexus.util.StringUtils;

public class InheritProject {

    private ArtifactMetadataSource artifactMetadataSource;

    private ArtifactRepository artifactRepository;

    private ArtifactResolver artifactResolver;

    /**
     * Group ID of project
     *
     * @parameter
     * @required
     */
    private String groupId;
    
    /**
     * Artifact ID of project
     *
     * @parameter
     * @required
     */
    private String artifactId;
    
    /**
     * Version of project
     *
     * @parameter
     * @required
     */
    private String version;
    
    /**
     * Type of project
     *
     * @parameter default-value="jar"
     * @required
     */
    private String type;

    /**
     * Relative path to the source directory for the project
     *
     * @parameter
     * @required
     */
    private String relativeSourcePath;

    private ArtifactFactory artifactFactory;

    public InheritProject() {}
    
    public InheritProject(String groupId, String artifactId, String version, String type,
                          String relativeSourcePath) {
        this.groupId = groupId;
        this.version = version;
        this.artifactId = artifactId;
        this.type = type;
        this.relativeSourcePath = relativeSourcePath;
    }

    public void setArtifactMetadataSource(ArtifactMetadataSource ams) {
        this.artifactMetadataSource = ams;
    }

    public void setArtifactRepository(ArtifactRepository ar) {
        this.artifactRepository = ar;
    }

    public void setArtifactResolver(ArtifactResolver ar) {
        this.artifactResolver = ar;
    }

    public void setArtifactFactory(ArtifactFactory af) {
        this.artifactFactory = af;
    }

    private String filterEmptyString(String in) {
        if (in == null || in.equals("")) {
            return null;
        }
        else {
            return in;
        }
    }
    
    /**
     * @return Returns the artifactId.
     */
    public String getArtifactId() {
        return artifactId;
    }
    
    /**
     * @param artifactId
     *            The artifactId to set.
     */
    public void setArtifactId(String artifact) {
        this.artifactId = filterEmptyString(artifact);
    }
    
    /**
     * @return Returns the relativeSourcePath
     */
    public String getRelativeSourcePath() {
        return relativeSourcePath;
    }
    
    /**
     * @param relativeSourcePath
     *            The relativeSourcePath to set.
     */
    public void setRelativeSourcePath(String relativeSourcePath) {
        this.relativeSourcePath = filterEmptyString(relativeSourcePath);
    }
    
    /**
     * @return Returns the groupId.
     */
    public String getGroupId() {
        return groupId;
    }
    
    /**
     * @param groupId
     *            The groupId to set.
     */
    public void setGroupId(String groupId) {
        this.groupId = filterEmptyString(groupId);
    }
    
    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
        this.version = filterEmptyString(version);
    }

    /**
     * @return Returns the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        this.type = filterEmptyString(type);
    }

    /**
     * Returns the Artifact for the main artifact of the project.
     *
     * @param project       This actual project, not the inherited one.
     * @return Artifact for main artifact
     * @throws MojoExecutionException  Failed to resolve artifact
     */
    public Artifact getMainArtifact(final MavenProject project) throws MojoExecutionException {
        return resolveArtifact(project, createArtifact(null,null), false);
    }

    /**
     * Returns the Artifact for the datafile of the project.
     *
     * @param project       This actual project, not the inherited one.
     * @return Artifact for datafile
     * @throws MojoExecutionException  Failed to resolve artifact
     */
    public Artifact getSerArtifact(final MavenProject project) throws MojoExecutionException {
        return resolveArtifact(project, createArtifact("cobertura","ser"), false);
    }

    /**
     * Resolves the main artifact for the inherited project, and copies it to a given directory.
     *
     * @param project       This actual project, not the inherited one.
     * @param outputDir     The directory to copy to.
     * @return
     *
     * @throws MojoExecutionException  Failed to resolve artifact, or could not copy artifact.
     */
    public void copyMainArtifact(final MavenProject project, final File outputDir) throws MojoExecutionException {
        Artifact mainArtifact = getMainArtifact(project);

        copyArtifact(mainArtifact, outputDir, false);
    }

    /**
     * Resolves the cobertura.ser artifact for the inherited project, and copies it to a given directory.
     *
     * @param project       This actual project, not the inherited one.
     * @param outputDir     The directory to copy to.
     * @return
     *
     * @throws MojoExecutionException  Failed to resolve artifact, or could not copy artifact.
     */
    public void copySerArtifact(final MavenProject project, final File outputDir) throws MojoExecutionException {
        Artifact serArtifact = getSerArtifact(project);

        copyArtifact(serArtifact, outputDir, false);
    }


    /**
     * Creates an Artifact object using the already-defined values, along with an optional
     * classifier and an optional override for type.
     *
     * @param classifier    The artifact classfier
     * @param overrideType  If given, overrides the type value we already have.
     * @return              An unresolved artifact
     *
     * @throws MojoExecutionException   Failed to create artifact
     */
    private Artifact createArtifact(final String classifier, final String overrideType) throws MojoExecutionException {
        String argType;
        if (overrideType!=null && !overrideType.equals("")) {
            argType = overrideType;
        }
        else {
            argType = type;
        }

                // Convert the string version to a range
        VersionRange range;
        try {
            range = VersionRange.createFromVersionSpec(version);
        }
        catch (InvalidVersionSpecificationException e) {
            throw new MojoExecutionException("Could not create range for version: " + version, e);
        }
        
        return artifactFactory.createDependencyArtifact(groupId,
                                                        artifactId,
                                                        range,
                                                        argType,
                                                        classifier,
                                                        Artifact.SCOPE_COMPILE);
    }
    
    /**
     * Resolves the Artifact from the remote repository if nessessary. If no version is specified, it will
     * be retrieved from the dependency list or from the DependencyManagement section of the pom.
     *
     * @param project       This actual project, not the inherited one.
     * @param artifact      The artifact to be resolved; must not be null
     * @param transitive    True to resolve the artifact transitivly
     * @return              The resolved artifact; never null
     *
     * @throws MojoExecutionException   Failed to resolve artifact
     */
    private Artifact resolveArtifact(final MavenProject project, final Artifact artifact, final boolean transitive) throws MojoExecutionException {
        assert artifact != null;

        try {
            if (transitive) {
                artifactResolver.resolveTransitively(
                        Collections.singleton(artifact),
                        project.getArtifact(),
                        project.getRemoteArtifactRepositories(),
                        artifactRepository,
                        artifactMetadataSource);
            }
            else {
                artifactResolver.resolve(
                        artifact,
                        project.getRemoteArtifactRepositories(),
                        artifactRepository);
            }
        }
        catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Unable to resolve artifact", e);
        }
        catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Unable to find artifact", e);
        }

        return artifact;
    }

    /**
     * Copies the Artifact after building the destination file name if
     * overridden. This method also checks if the classifier is set and adds it
     * to the destination file name if needed.
     * 
     * @param artifact
     *            representing the object to be copied.
     * @param destDir
     *            The directory to copy the artifact to.
     * @param removeVersion
     *            specifies if the version should be removed from the file name
     *            when copying.
     * 
     * @throws MojoExecutionException
     *             with a message if an error occurs.
     * 
     */
    private void copyArtifact(Artifact artifact, File destDir, boolean removeVersion) throws MojoExecutionException {
        assert artifact != null;

        String destFileName = getFormattedFileName(artifact, false);
        
        File destFile = new File(destDir, destFileName);

        copyFile(artifact.getFile(), destFile);
    }

    /**
     * Does the actual copy of the file and logging.
     *
     * @param artifact represents the file to copy.
     * @param destFile file name of destination file.
     *
     * @throws MojoExecutionException with a message if an
     *             error occurs.
     */
    private void copyFile(File artifact, File destFile) throws MojoExecutionException {
        try {
            FileUtils.copyFile(artifact, destFile);
            
        }
        catch (Exception e) {
            throw new MojoExecutionException("Error copying artifact from " + artifact + " to " + destFile, e);
        }
    }

    
    /**
     * Builds the file name. If removeVersion is set, then the file name must be
     * reconstructed from the artifactId, Classifier (if used) and Type.
     * Otherwise, this method returns the artifact file name.
     * 
     * @param artifact
     *            File to be formatted.
     * @param removeVersion
     *            Specifies if the version should be removed from the file name.
     * @return Formatted file name in the format
     *         artifactId-[version]-[classifier].[type]
     */
    public static String getFormattedFileName(Artifact artifact, boolean removeVersion) {
        String destFileName = null;

        // if there is a file and we aren't stripping the version, just get the
        // name directly
        if (artifact.getFile() != null && !removeVersion) {
            destFileName = artifact.getFile().getName();
        }
        else {
            String versionString = null;
            if (!removeVersion) {
                versionString = "-" + artifact.getVersion();
            }
            else {
                versionString = "";
            }

            String classifierString = "";

            if (StringUtils.isNotEmpty(artifact.getClassifier())) {
                classifierString = "-" + artifact.getClassifier();
            }

            destFileName = artifact.getArtifactId() + versionString + classifierString + "."
                + artifact.getArtifactHandler().getExtension();
        }
        return destFileName;
    }

}
