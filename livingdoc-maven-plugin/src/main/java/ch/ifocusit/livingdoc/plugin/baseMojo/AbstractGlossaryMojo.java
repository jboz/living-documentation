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
import ch.ifocusit.livingdoc.plugin.utils.AnchorUtil;
import ch.ifocusit.livingdoc.plugin.utils.ClassLoaderUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.NEWLINE;
import static ch.ifocusit.livingdoc.plugin.utils.StringUtil.defaultString;
import static ch.ifocusit.livingdoc.plugin.utils.StringUtil.interpretNewLine;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author Julien Boz
 */
public abstract class AbstractGlossaryMojo extends AbstractDocsGeneratorMojo {

    // first parameter is the 'id', the second, the 'name', the third the anchor link
    protected static final String GLOSSARY_LINK_TITLE = "[[{2}]]" + NEWLINE + "=== [small]#{0}# - {1}";
    protected static final String GLOSSARY_LINK_TITLE_LITE = "[[{2}]]" + NEWLINE + "=== {1}";
    protected static final String GLOSSARY_LINK_INLINE_ID = "<<{2},{0}>>";
    protected static final String GLOSSARY_LINK_INLINE_NAME = "<<{2},{1}>>";
    protected static final String JAVAX_VALIDATION_CONSTRAINTS = "javax.validation.constraints.";
    protected static final String HIBERNATE_VALIDATION_CONSTRAINTS = "org.hibernate.validator.constraints.";

    /**
     * Temple for glossary title.
     */
    @Parameter
    protected String glossaryTitleTemplate;

    /**
     * List of source directories to browse
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}")
    private List<String> sources = new ArrayList<>();

    @Parameter
    private String packageRoot = EMPTY;

    protected AsciiDocBuilder asciiDocBuilder = this.createAsciiDocBuilder();
    protected JavaProjectBuilder javaDocBuilder;
    protected List<DomainObject> mappings;

    protected boolean somethingWasGenerated = false;

    private static Function<DomainObject, ?> key() {
        return def -> def.getId() == null ? def.getName() : def.getId();
    }

    protected static Predicate<DomainObject> distinctByKey() {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(key().apply(t), Boolean.TRUE) == null;
    }

    /**
     * Main method.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
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
            executeMojo();
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
    protected abstract void executeMojo() throws Exception;

    protected Stream<JavaClass> getClasses() {
        return javaDocBuilder.getClasses().stream()
                .filter(javaClass -> packageRoot == null || javaClass.getPackageName().startsWith(packageRoot))
                .filter(this::hasAnnotation) // if annotated
                ;
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

    private Optional<DomainObject> getMapping(JavaAnnotatedElement annotatedElement) {
        Optional<Integer> id = getGlossaryId(annotatedElement);
        return mappings == null || !id.isPresent() ? Optional.empty() : mappings.stream().filter(def -> id.get().equals(def.getId())).findFirst();
    }

    // *******************************************************
    // CLASSLOADING
    // *******************************************************

    protected String getName(JavaAnnotatedElement annotatedElement, String defaultValue) {
        return getMapping(annotatedElement).map(DomainObject::getName).orElse(defaultValue);
    }

    protected String getDescription(JavaAnnotatedElement element) {
        return getDescription(element, element.getComment());
    }

    private String getDescription(JavaAnnotatedElement annotatedElemen, String defaultValue) {
        return getMapping(annotatedElemen).map(DomainObject::getDescription).orElse(defaultValue);
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
                getLog().warn("Unable to load jar source " + artifact, e);
            }
        });
    }

    // *******************************************************
    // LINKING
    // *******************************************************

    /**
     * @return a formatted text with an asciidoc anchor.
     */
    protected String formatAndLink(String textTemplate, DomainObject domainObject) {
        String anchorLink = AnchorUtil.formatLink(glossaryAnchorTemplate, domainObject.getId(), domainObject.getFullName());
        return interpretNewLine(format(textTemplate, defaultString(domainObject.getId(), EMPTY), domainObject.getFullName(), anchorLink));
    }

    // *******************************************************
    // MAPPING DEFINITION
    // *******************************************************

    private DomainObject createMappingDefinition(JavaAnnotatedElement model, String name) {
        DomainObject domainObject = new DomainObject();
        domainObject.setId(getGlossaryId(model).orElse(null));
        domainObject.setName(getName(model, name));
        domainObject.setDescription(getDescription(model));
        domainObject.setMapped(getMapping(model).isPresent());
        return domainObject;
    }

    protected DomainObject map(JavaClass model) {
        DomainObject domainObject = createMappingDefinition(model, model.getName());
        domainObject.setNamespace(model.getPackageName());
        return domainObject;
    }

    protected DomainObject map(JavaField model) {
        DomainObject domainObject = createMappingDefinition(model, model.getName());
        domainObject.setParentName(model.getDeclaringClass().getName());
        domainObject.setNamespace(model.getDeclaringClass().getPackageName());
        model.getAnnotations().stream()
                .filter(annot -> annot.getType().getFullyQualifiedName().startsWith(JAVAX_VALIDATION_CONSTRAINTS)
                        || annot.getType().getFullyQualifiedName().startsWith(HIBERNATE_VALIDATION_CONSTRAINTS))
                .map(annot -> annot.toString().replace(JAVAX_VALIDATION_CONSTRAINTS, "").replace(HIBERNATE_VALIDATION_CONSTRAINTS, ""))
                .forEach(domainObject::addAnnotation);
        if (!model.isEnumConstant()) {
            domainObject.setType(model.getType());
        }
        return domainObject;
    }
}
