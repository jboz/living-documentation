/*
 * Living Documentation
 *
 * Copyright (C) 2023 Focus IT
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
import ch.ifocusit.livingdoc.plugin.domain.Color;
import ch.ifocusit.livingdoc.plugin.utils.AnnotationUtil;
import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import ch.ifocusit.plantuml.classdiagram.model.attribute.ClassAttribute;
import ch.ifocusit.plantuml.classdiagram.model.clazz.JavaClazz;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.NEWLINE;

/**
 * @author Julien Boz
 */
@SuppressWarnings("UnstableApiUsage")
@Setter
public class PlantumlClassDiagramBuilder extends AbstractClassDiagramBuilder {
    private static final String PRAGMA_LAYOUT_SMETANA = "!pragma layout smetana";

    Logger LOG = LoggerFactory.getLogger(PlantumlClassDiagramBuilder.class);

    private ClassDiagramBuilder classDiagramBuilder;
    private Predicate<ClassInfo> additionalClassPredicate = a -> true; // default predicate always true
    private Predicate<ClassAttribute> fieldPredicate = null;
    private boolean showMethods;
    private boolean showFields;
    private boolean useExternalGraphviz = false;

    private String rootAggregateClassMatcher;
    private Color rootAggregateColor;

    public String build() throws MojoExecutionException {
        // override creation to change root aggregate class color
        classDiagramBuilder = new ClassDiagramBuilder() {
            @Override
            public JavaClazz createJavaClass(Class clazz) {
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
        if (fieldPredicate != null) {
            classDiagramBuilder.addFieldPredicate(fieldPredicate);
        }

        final ClassPath classPath = initClassPath();
        final Set<ClassInfo> allClasses = classPath.getTopLevelClassesRecursive(prefix);
        //noinspection rawtypes
        List<Class> classes = allClasses.stream()
                // apply filters
                .filter(defaultFilter()).filter(additionalClassPredicate).map(classInfo -> {
                    try {
                        return classInfo.load();
                    } catch (Throwable e) {
                        LOG.warn(e.toString());
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        if (StringUtils.isNotBlank(singleClass)) {
            try {
                classes.add(getClassLoader().loadClass(singleClass));
            } catch (ClassNotFoundException e) {
                LOG.error("Class '" + singleClass + "' not found! Will be ignored.");
            }
        }

        LOG.info("Initial classes size: " + classes.size());

        String[] startOptions = readStartOptions();
        if (!useExternalGraphviz) {
            startOptions = ArrayUtils.add(startOptions, PRAGMA_LAYOUT_SMETANA + NEWLINE);
        }

        return classDiagramBuilder.addClasses(classes)
                .withNamesMapper(namesMapper)
                .excludes(excludes)
                .setStartOptions(startOptions)
                .setTitle(diagramTitle)
                .setHeader(header)
                .setFooter(footer)
                .setEndOptions(readEndOptions())
                .withLinkMaker(this)
                .withDependencies(diagramWithDependencies)
                .build();
    }

    @Override
    protected String[] readStartOptions() throws MojoExecutionException {
        String[] headers = super.readStartOptions();
        if (!showFields) {
            headers = ArrayUtils.add(headers, "hide fields" + NEWLINE);
        }
        if (!showMethods) {
            headers = ArrayUtils.add(headers, "hide methods" + NEWLINE);
        }
        return headers;
    }

    public void filterOnAnnotation(Class<? extends Annotation> annotation) {
        if (annotation == null) {
            return; // nothing to do
        }
        // create class predicate
        additionalClassPredicate = additionalClassPredicate.and(classInfo -> classInfo.load().isAnnotationPresent(annotation));
        // add field predicate
        fieldPredicate = attribut -> attribut.getField().isAnnotationPresent(annotation);
    }

    public void setUseExternalGraphiz(boolean useExternalGraphviz) {
        this.useExternalGraphviz = useExternalGraphviz;
    }
}
