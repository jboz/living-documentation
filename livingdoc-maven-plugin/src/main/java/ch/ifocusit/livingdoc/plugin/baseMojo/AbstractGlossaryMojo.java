/*
 * Living Documentation
 *
 * Copyright (C) 2017 Focus IT
 *
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
package ch.ifocusit.livingdoc.plugin.baseMojo;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.plugin.mapping.DomainObject;
import ch.ifocusit.livingdoc.plugin.mapping.MappingRespository;
import ch.ifocusit.livingdoc.plugin.utils.ClassLoaderUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.simpleflatmapper.csv.CsvParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

/**
 * @author Julien Boz
 */
public abstract class AbstractGlossaryMojo extends AbstractDocsGeneratorMojo implements MappingRespository {

    protected static final String JAVAX_VALIDATION_CONSTRAINTS = "javax.validation.constraints.";
    protected static final String HIBERNATE_VALIDATION_CONSTRAINTS = "org.hibernate.validator.constraints.";

    /**
     * List of source directories to browse
     */
    @Parameter(property = "livingdoc.glossary.sources", defaultValue = "${project.build.sourceDirectory}")
    private List<String> sources = new ArrayList<>();

    @Parameter(property = "livingdoc.glossary.packageRoot", defaultValue = "${project.groupId}.${project.artifactId}.domain")
    private String packageRoot;

    @Parameter
    private String[] excludes = new String[0];

    protected AsciiDocBuilder asciiDocBuilder = this.createAsciiDocBuilder();
    protected JavaProjectBuilder javaDocBuilder;
    protected List<DomainObject> mappings;

    protected boolean somethingWasGenerated = false;

    /**
     * Main method.
     */
    @Override
    public void executeMojo() throws MojoExecutionException, MojoFailureException {
        javaDocBuilder = buildJavaProjectBuilder();
        appendTitle(asciiDocBuilder);

        if (glossaryMapping != null) {
            try {
                mappings = CsvParser.mapTo(DomainObject.class)
                        .stream(new FileReader(glossaryMapping))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new MojoExecutionException("error reading mappings file", e);
            }
        }
        try {
            executeGlossaryMojo();
        } catch (Exception e) {
            throw new MojoExecutionException("error executing glossary template", e);
        }
        if (!somethingWasGenerated) {
            // nothing generated
            return;
        }

        write(asciiDocBuilder);
    }

    // *******************************************************
    // TOOLS
    // *******************************************************

    protected abstract String getTitle();

    /**
     * Implementation main method.
     */
    protected abstract void executeGlossaryMojo() throws Exception;

    protected Stream<JavaClass> getClasses() {
        return javaDocBuilder.getClasses().stream()
                .filter(javaClass -> packageRoot == null || javaClass.getPackageName().startsWith(packageRoot))
                .filter(this::hasAnnotation) // if annotated
                .filter(defaultFilter())
                ;
    }

    private static final String TEST = "Test";
    private static final String IT = "IT";
    private static final String PACKAGE_INFO = "package-info";

    protected Predicate<JavaClass> defaultFilter() {
        return ci -> !ci.getSimpleName().equalsIgnoreCase(PACKAGE_INFO)
                && !ci.getSimpleName().endsWith(TEST)
                && !ci.getSimpleName().endsWith(IT)
                // do not load class if must be filtered
                && stream(excludes).noneMatch(excl -> ci.getCanonicalName().matches(excl));
    }

    protected boolean hasAnnotation(JavaAnnotatedElement annotatedElement) {
        return !onlyAnnotated || getGlossary(annotatedElement).isPresent();
    }

    private Optional<JavaAnnotation> getGlossary(JavaAnnotatedElement annotatedElement) {
        return annotatedElement.getAnnotations().stream()
                .filter(a -> a.getType().getFullyQualifiedName().endsWith(UbiquitousLanguage.class.getSimpleName()))
                .findFirst();
    }

    private Optional<Integer> getGlossaryId(JavaAnnotatedElement annotatedElement) {
        Optional<JavaAnnotation> annotation = getGlossary(annotatedElement);
        return annotation.map(annot -> annot.getProperty("id") == null ? null :
                Optional.ofNullable(Integer.valueOf(String.valueOf(annot.getNamedParameter("id")))))
                .orElse(Optional.empty());
    }

    public Optional<DomainObject> getMapping(JavaAnnotatedElement annotatedElement) {
        Optional<Integer> id = getGlossaryId(annotatedElement);
        return mappings == null || !id.isPresent() ? Optional.empty() : mappings.stream().filter(def -> id.get().equals(def.getId())).findFirst();
    }

    // *******************************************************
    // CLASSLOADING
    // *******************************************************

    private JavaProjectBuilder buildJavaProjectBuilder() throws MojoExecutionException {
        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.setEncoding(Charset.defaultCharset().toString());
        javaDocBuilder.setErrorHandler(e -> getLog().warn(e.getMessage()));
        sources.stream().map(File::new).forEach(javaDocBuilder::addSourceTree);
        javaDocBuilder.addClassLoader(ClassLoaderUtil.getRuntimeClassLoader(project));

        loadSourcesDependencies(javaDocBuilder);

        return javaDocBuilder;
    }

    // *******************************************************
    // FILTERING
    // *******************************************************

    private void loadSourcesDependencies(JavaProjectBuilder javaDocBuilder) {

        PluginDescriptor pluginDescriptor = ((PluginDescriptor) getPluginContext().get("pluginDescriptor"));

        Stream.concat(project.getDependencies().stream(),
                project.getPlugin(pluginDescriptor.getPluginLookupKey()).getDependencies().stream())

                .forEach(dependency -> {
                    Artifact sourcesArtifact = repositorySystem.createArtifactWithClassifier(dependency.getGroupId(),
                            dependency.getArtifactId(), dependency.getVersion(), dependency.getType(), "sources");

                    loadSourcesDependency(javaDocBuilder, sourcesArtifact);
                });

    }

    private void loadSourcesDependency(JavaProjectBuilder javaDocBuilder, Artifact sourcesArtifact) {
        // create request
        ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(sourcesArtifact);

        // resolve deps
        ArtifactResolutionResult result = repositorySystem.resolve(request);

        // load source file into javadoc builder
        result.getArtifacts().forEach(artifact -> {
            try {
                JarFile jarFile = new JarFile(artifact.getFile());
                for (Enumeration entries = jarFile.entries(); entries.hasMoreElements(); ) {
                    JarEntry entry = (JarEntry) entries.nextElement();
                    String name = entry.getName();
                    if (name.endsWith(".java") && !name.endsWith("/package-info.java")) {
                        javaDocBuilder.addSource(new URL("jar:" + artifact.getFile().toURI().toURL().toString() + "!/" + name));
                    }
                }
            } catch (Exception e) {
                getLog().warn("Unable to load jar source " + artifact + " : " + e.getMessage());
            }
        });
    }
}
