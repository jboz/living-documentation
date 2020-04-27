/*
 * Living Documentation
 *
 * Copyright (C) 2020 Focus IT
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

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.NEWLINE;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ifocusit.livingdoc.annotations.RootAggregate;
import ch.ifocusit.livingdoc.plugin.domain.Color;
import ch.ifocusit.livingdoc.plugin.utils.AnnotationUtil;
import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import ch.ifocusit.plantuml.classdiagram.model.clazz.JavaClazz;

/**
 * @author Julien Boz
 */
public class PlantumlClassDiagramBuilder extends AbstractClassDiagramBuilder {

    Logger LOG = LoggerFactory.getLogger(PlantumlClassDiagramBuilder.class);

    private ClassDiagramBuilder classDiagramBuilder;
    private Predicate<ClassInfo> additionalClassPredicate = a -> true; // default predicate always true
    private boolean showMethods;
    private boolean showFields;

    public PlantumlClassDiagramBuilder(MavenProject project, String prefix, String[] excludes,
            String rootAggregateClassMatcher, Color rootAggregateColor, File header, File footer, boolean showMethods,
            boolean showFields, boolean diagramWithDependencies, String linkPage) {
        super(project, prefix, excludes, header, footer, diagramWithDependencies, linkPage);

        this.showMethods = showMethods;
        this.showFields = showFields;

        // override creation to change root aggregate class color
        classDiagramBuilder = new ClassDiagramBuilder() {
            @Override
            protected JavaClazz createJavaClass(Class clazz) {
                JavaClazz javaClass = super.createJavaClass(clazz);
                if (rootAggregateColor != null) {
                    if (StringUtils.isNotBlank(rootAggregateClassMatcher)) {
                        if (clazz.getName().matches(rootAggregateClassMatcher)) {
                            javaClass.setBackgroundColor(rootAggregateColor.getBackground())
                                    .setBorderColor(rootAggregateColor.getBorder());
                        }
                    } else {
                        AnnotationUtil.tryFind(clazz, RootAggregate.class)
                                .ifPresent(annot -> javaClass.setBackgroundColor(rootAggregateColor.getBackground())
                                        .setBorderColor(rootAggregateColor.getBorder()));
                    }
                }
                return javaClass;
            }
        };
    }

    public String generate() throws MojoExecutionException {
        final ClassPath classPath = initClassPath();
        final Set<ClassInfo> allClasses = classPath.getTopLevelClassesRecursive(prefix);

        LOG.info("Initial classes size: " + allClasses.size());

        String diagram = classDiagramBuilder.addClasse(allClasses.stream()
                // apply filters
                .filter(defaultFilter()).filter(additionalClassPredicate).map(classInfo -> {
                    try {
                        return classInfo.load();
                    } catch (Throwable e) {
                        LOG.warn(e.toString());
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList())).excludes(excludes).setHeader(readHeader())
                .setFooter(readFooter()).withNamesMapper(namesMapper).withLinkMaker(this)
                .withDependencies(diagramWithDependencies).build();
        return diagram;
    }

    @Override
    protected String readHeader() throws MojoExecutionException {
        String header = super.readHeader();
        header += showFields ? StringUtils.EMPTY : ("hide fields" + NEWLINE);
        header += showMethods ? StringUtils.EMPTY : ("hide methods" + NEWLINE);
        return header;
    }

    public void filterOnAnnotation(Class<? extends Annotation> annotation) {
        if (annotation == null) {
            return; // nothing to do
        }
        // create class predicate
        additionalClassPredicate = additionalClassPredicate
                .and(classInfo -> classInfo.load().isAnnotationPresent(annotation));
        // add field predicate
        classDiagramBuilder.addFieldPredicate(attribut -> attribut.getField().isAnnotationPresent(annotation));
    }
}
