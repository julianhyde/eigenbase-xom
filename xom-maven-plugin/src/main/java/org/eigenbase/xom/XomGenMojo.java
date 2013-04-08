package org.eigenbase.xom;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 * XOM generation maven plugin.
 *
 * @goal xom
 * @phase generate-sources
 * @requiresDependencyResolution runtime
 */
public class XomGenMojo extends AbstractMojo {

    /**
     * @parameter expression="${modelFileName}"
     * @required
     */
    File modelFile;

    /**
     * @parameter expression="${outputSourcesDirectory}" default-value="${project.build.directory}/generated-sources/xom"
     * @required
     */
    File outputSourcesDirectory;

    /**
     * @parameter expression="${outputResourcesDirectory}" default-value="${project.build.directory}/generated-resources/xom"
     * @required
     */
    File outputResourcesDirectory;


    /**
     * @parameter expression="${dtdFileName}"
     */
    String dtdFileName;

    /**
     * @parameter expression="${className}"
     */
    String className;

    /**
     * The current project representation.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            if (!outputSourcesDirectory.exists()) {
                outputSourcesDirectory.mkdirs();
            }
            if (!outputResourcesDirectory.exists()) {
                outputResourcesDirectory.mkdirs();
            }

            File classFile = classNameToFile(outputSourcesDirectory, className);
            File outputDir = classFile.getParentFile();
            outputDir.mkdirs();

            File dtdFile = new File(outputResourcesDirectory, dtdFileName);
            File dtdDir = dtdFile.getParentFile();
            dtdDir.mkdirs();

            if (modelFile.exists() &&
                    classFile.exists() &&
                    dtdFile.exists()) {
                long modelStamp = modelFile.lastModified(),
                        classStamp = classFile.lastModified(),
                        dtdStamp = dtdFile.lastModified();
                if (classStamp > modelStamp &&
                        dtdStamp > modelStamp) {
                    // files are up to date
                    return;
                }
            }

            final boolean testMode = false;
            MetaGenerator generator = new MetaGenerator(
                    modelFile.toString(), testMode, className);
            generator.writeFiles(classFile, dtdFile);
            generator.writeOutputs();

            project.addCompileSourceRoot(outputSourcesDirectory.getAbsolutePath());

            Resource res = new Resource();
            res.setDirectory(outputResourcesDirectory.getAbsolutePath());
            project.addResource(res);

        } catch (XOMException e) {
            throw new MojoExecutionException("Generation of model failed: " + e);
        } catch (IOException e) {
            throw new MojoExecutionException("Generation of model failed: " + e);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Creates the File that a java class will live in. For example,
     * <code>makeJavaFileName("com.myproj", "MyClass")</code> returns
     * "com/myproj/MyClass.java".
     */
    static File classNameToFile(File dir, String className) {
        char fileSep = System.getProperty("file.separator").charAt(0); // e.g. '/'

        String relativePath = className.replace('.', fileSep) + ".java";

        if (dir == null) {
            return new File(relativePath);
        } else {
            return new File(dir, relativePath);
        }
    }

}
