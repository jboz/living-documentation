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
package ch.ifocusit.livingdoc.plugin.diagram;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.plugin.mapping.GlossaryNamesMapper;
import ch.ifocusit.livingdoc.plugin.utils.AnchorUtil;
import ch.ifocusit.livingdoc.plugin.utils.AnnotationUtil;
import ch.ifocusit.livingdoc.plugin.utils.ClassLoaderUtil;
import ch.ifocusit.plantuml.classdiagram.LinkMaker;
import ch.ifocusit.plantuml.classdiagram.NamesMapper;
import ch.ifocusit.plantuml.classdiagram.model.Link;
import com.google.common.reflect.ClassPath;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Predicate;

import static ch.ifocusit.livingdoc.plugin.utils.FileUtils.read;
import static java.util.Arrays.stream;

/**
 * @author Julien Boz
 */
public abstract class AbstractClassDiagramBuilder implements LinkMaker, NamesMapper {

    private static final String TEST = "Test";
    private static final String IT = "IT";
    private static final String PACKAGE_INFO = "package-info";

    protected final MavenProject project;
    protected final String prefix;
    protected final String[] excludes;
    protected final File header;
    protected final File footer;
    protected final boolean diagramWithDependencies;
    protected String linkPage;

    protected NamesMapper namesMapper = this;
    private Class<? extends Annotation> mappingAnnotation;

    public AbstractClassDiagramBuilder(MavenProject project, String prefix, String[] excludes, File header, File footer,
                                       boolean diagramWithDependencies, String linkPage) {
        this.project = project;
        this.prefix = prefix;
        this.excludes = excludes;
        this.header = header;
        this.footer = footer;
        this.diagramWithDependencies = diagramWithDependencies;
        this.linkPage = linkPage;
    }

    public abstract void filterOnAnnotation(Class<? extends Annotation> annotation);

    public abstract String generate() throws MojoExecutionException;

    protected String readHeader() throws MojoExecutionException {
        return read(header);
    }

    protected String readFooter() throws MojoExecutionException {
        return read(footer);
    }

    protected Predicate<ClassPath.ClassInfo> defaultFilter() {
        return ci -> ci.getPackageName().startsWith(prefix)
                && !ci.getSimpleName().equalsIgnoreCase(PACKAGE_INFO)
                && !ci.getSimpleName().endsWith(TEST)
                && !ci.getSimpleName().endsWith(IT)
                // do not load class if must be filtered
                && stream(excludes).noneMatch(excl -> ci.getName().matches(excl));
    }

    protected ClassPath initClassPath() throws MojoExecutionException {
        try {
            return ClassPath.from(ClassLoaderUtil.getRuntimeClassLoader(project));
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to initialize classPath !", e);
        }
    }

    public void mapNames(File mappings) throws MojoExecutionException {
        try {
            namesMapper = new GlossaryNamesMapper<>(mappings, UbiquitousLanguage.class);
        } catch (IOException e) {
            throw new MojoExecutionException("error reading mappings file", e);
        }
    }

    private Optional<Link> createLink(String label, Integer linkId, String defaultLinkId) {
        Link link = new Link();
        link.setLabel(label);
        link.setUrl(AnchorUtil.formatLinkWithPage(linkPage, linkId, defaultLinkId));
        return Optional.of(link);
    }

    @Override
    public Optional<Link> getClassLink(Class aClass) {
        Integer id = AnnotationUtil.tryFind(aClass, UbiquitousLanguage.class).map(UbiquitousLanguage::id).orElse(null);
        String label = namesMapper.getClassName(aClass);
        return createLink(label, id, label);
    }

    @Override
    public Optional<Link> getFieldLink(Field field) {
        String parentLabel = namesMapper.getClassName(field.getDeclaringClass());
        String label = namesMapper.getFieldName(field);
        Integer id = AnnotationUtil.tryFind(field, UbiquitousLanguage.class).map(UbiquitousLanguage::id).orElse(null);
        String defaultLinkId = AnchorUtil.glossaryLink(parentLabel, label);
        return createLink(label, id, defaultLinkId);
    }
}
