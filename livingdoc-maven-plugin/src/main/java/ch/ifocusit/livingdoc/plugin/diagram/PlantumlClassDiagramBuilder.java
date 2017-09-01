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

import ch.ifocusit.livingdoc.annotations.RootAggregate;
import ch.ifocusit.livingdoc.plugin.utils.AnnotationUtil;
import ch.ifocusit.livingdoc.plugin.domain.Color;
import ch.ifocusit.livingdoc.plugin.mapping.GlossaryNamesMapper;
import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import ch.ifocusit.plantuml.classdiagram.model.clazz.JavaClazz;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Julien Boz
 */
public class PlantumlClassDiagramBuilder extends AbstractClassDiagramBuilder {

    private ClassDiagramBuilder classDiagramBuilder;
    private Predicate<ClassInfo> additionalClassPredicate = a -> true; // default predicate always true
    private GlossaryNamesMapper namesMapper;

    public PlantumlClassDiagramBuilder(MavenProject project, String prefix, String[] excludes, Color rootAggregateColor, File header, File footer,
                                       boolean diagramWithDependencies) {
        super(project, prefix, excludes, header, footer, diagramWithDependencies);

        // override creation to change root aggregate class color
        classDiagramBuilder = new ClassDiagramBuilder() {
            @Override
            protected JavaClazz createJavaClass(Class clazz) {
                JavaClazz javaClass = super.createJavaClass(clazz);
                if (rootAggregateColor != null) {
                    AnnotationUtil.tryFind(clazz, RootAggregate.class).ifPresent(annot ->
                            javaClass.setBackgroundColor(rootAggregateColor.getBackgroundColor())
                                    .setBorderColor(rootAggregateColor.getBorderColor()));
                }
                return javaClass;
            }
        };
    }

    public String generate() throws MojoExecutionException {
        final ClassPath classPath = initClassPath();
        final Set<ClassInfo> allClasses = classPath.getTopLevelClassesRecursive(prefix);

        if (namesMapper != null) {
            classDiagramBuilder.withNamesMapper(namesMapper);
        }

        String diagram = classDiagramBuilder
                .addClasse(allClasses.stream()
                        // apply filters
                        .filter(defaultFilter())
                        .filter(additionalClassPredicate)
                        .map(ClassInfo::load).collect(Collectors.toList()))
                .excludes(excludes)
                .setHeader(readHeader())
                .setFooter(readFooter())
                .withDependencies(diagramWithDependencies)
                .build();
        return diagram;
    }

    public PlantumlClassDiagramBuilder filterOnAnnotation(Class<? extends Annotation> annotation) {
        if (annotation == null) {
            return this; // nothing to do
        }
        // create class predicate
        additionalClassPredicate = additionalClassPredicate.and(classInfo -> classInfo.load().isAnnotationPresent(annotation));
        // add field predicate
        classDiagramBuilder.addFieldPredicate(attribut -> attribut.getField().isAnnotationPresent(annotation));
        return this;
    }

    public void mapNames(File mappings, Class<? extends Annotation> annotation, String linkTemplate) throws MojoExecutionException {
        try {
            namesMapper = new GlossaryNamesMapper(mappings, annotation, linkTemplate);
        } catch (IOException e) {
            throw new MojoExecutionException("error reading mappings file", e);
        }
    }
}
