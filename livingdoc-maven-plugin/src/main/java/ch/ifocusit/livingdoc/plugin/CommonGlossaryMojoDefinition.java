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
package ch.ifocusit.livingdoc.plugin;

import ch.ifocusit.livingdoc.plugin.common.ClassLoaderUtil;
import ch.ifocusit.livingdoc.plugin.mapping.MappingDefinition;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.library.OrderedClassLibraryBuilder;
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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Julien Boz
 */
public abstract class CommonGlossaryMojoDefinition extends CommonMojoDefinition {

    /**
     * List of source directories to browse
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}")
    private List<String> sources;

    /**
     * Annotation used to comment classes that will be included in the glossary (default: UbiquitousLanguage)
     */
    @Parameter(defaultValue = "UbiquitousLanguage")
    private String annotation;

    /**
     * File to use for UbiquitousLanguage mapping.
     */
    @Parameter
    private File glossaryMapping;

    @Parameter
    private String packageRoot;

    protected AsciiDocBuilder asciiDocBuilder = new AsciiDocBuilder();
    protected JavaProjectBuilder javaDocBuilder;
    protected List<MappingDefinition> mappings;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        javaDocBuilder = buildJavaProjectBuilder();
        if (Format.html.equals(format)) {
            asciiDocBuilder.sectionTitleLevel1(getTitle());
        }

        if (glossaryMapping != null) {
            try {
                mappings = CsvParser.mapTo(MappingDefinition.class)
                        .stream(new FileReader(glossaryMapping))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new MojoExecutionException("error reading mappings file", e);
            }
        }

        executeMojo();
        write(asciiDocBuilder);
    }

    protected abstract String getTitle();

    protected abstract void executeMojo();

    protected Stream<JavaClass> getClasses() {
        return javaDocBuilder.getClasses().stream()
                .filter(javaClass -> packageRoot == null || javaClass.getPackageName().startsWith(packageRoot))
                .filter(this::hasAnnotation) // if annotated
                ;
    }

    protected boolean hasAnnotation(JavaAnnotatedElement annotatedElement) {
        return getGlossary(annotatedElement).isPresent();
    }

    protected Optional<JavaAnnotation> getGlossary(JavaAnnotatedElement annotatedElement) {
        return annotatedElement.getAnnotations().stream()
                .filter(a -> a.getType().getFullyQualifiedName().endsWith(annotation))
                .findFirst();
    }

    protected Optional<Integer> getGlossaryId(JavaAnnotatedElement annotatedElement) {
        Optional<JavaAnnotation> annotation = getGlossary(annotatedElement);
        return annotation.map(annot -> annot.getProperty("id") == null ? null :
                    Optional.ofNullable(Integer.valueOf(String.valueOf(annot.getNamedParameter("id")))))
                .orElse(Optional.empty());
    }

    protected Optional<MappingDefinition> getDefinition(Optional<Integer> id) {
        return mappings == null || !id.isPresent() ? Optional.empty() : mappings.stream().filter(def -> id.get().equals(def.getId())).findFirst();
    }

    protected String getName(JavaAnnotatedElement annotatedElement, String defaultValue) {
        return getDefinition(getGlossaryId(annotatedElement)).map(def -> def.getName()).orElse(defaultValue);
    }

    protected String getDescription(JavaAnnotatedElement annotatedElemen, String defaultValue) {
        return getDefinition(getGlossaryId(annotatedElemen)).map(def -> def.getDescription()).orElse(defaultValue);
    }

    protected MappingDefinition map(JavaAnnotatedElement annotatedElement, String name, String comment) {
        MappingDefinition def = new MappingDefinition();
        def.setId(getGlossaryId(annotatedElement).orElse(null));
        def.setName(getName(annotatedElement, name));
        def.setDescription(getDescription(annotatedElement, comment));
        return def;
    }

    private JavaProjectBuilder buildJavaProjectBuilder() throws MojoExecutionException {
        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.setEncoding(Charset.defaultCharset().toString());
        javaDocBuilder.setErrorHandler(e -> getLog().warn(e.getMessage()));
        sources.stream().map(File::new).forEach(javaDocBuilder::addSourceTree);
        javaDocBuilder.addClassLoader(ClassLoaderUtil.getRuntimeClassLoader(project));

        loadSourcesDependencies(javaDocBuilder);

        return javaDocBuilder;
    }

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
                getLog().warn("Unable to load jar source " + artifact, e);
            }
        });
    }

    private static Function<MappingDefinition, ?> key() {
        return def -> def.getId() == null ? def.getName().toLowerCase() : def.getId();
    }

    protected static Predicate<MappingDefinition> distinctByKey() {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(key().apply(t), Boolean.TRUE) == null;
    }

    protected String defaultString(Integer id, String defaultString) {
        return id == null ? defaultString : String.valueOf(id);
    }

    protected String idFromName(MappingDefinition def) {
        return def.getName().replaceAll(" ", "");
    }
}
