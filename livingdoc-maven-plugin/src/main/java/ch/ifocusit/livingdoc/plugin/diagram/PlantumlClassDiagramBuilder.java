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

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Julien Boz
 */
public class PlantumlClassDiagramBuilder extends ClassDiagramBuilder {

    private ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder classDiagramBuilder = new ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder();
    private Predicate<ClassInfo> additionalClassPredicate = a -> true; // default predicate always true

    public PlantumlClassDiagramBuilder(MavenProject project, String prefix, String[] excludes) {
        super(project, prefix, excludes);
    }

    public String generate() throws MojoExecutionException {
        final ClassPath classPath = initClassPath();
        final Set<ClassInfo> allClasses = classPath.getTopLevelClassesRecursive(prefix);

        String diagram = classDiagramBuilder
                .addClasses(allClasses.stream()
                        // apply filters
                        .filter(defaultFilter())
                        .filter(additionalClassPredicate)
                        .map(ClassInfo::load).collect(Collectors.toList()))
                .excludes(excludes)
                .build();
        return diagram;
    }

    public PlantumlClassDiagramBuilder filterOnAnnotation(Class<? extends Annotation> annotation) {
        // create class predicate
        additionalClassPredicate = additionalClassPredicate.and(classInfo -> classInfo.load().isAnnotationPresent(annotation));
        // add field predicate
        classDiagramBuilder.addFieldPredicate(attribut -> attribut.getField().isAnnotationPresent(annotation));
        return this;
    }
}
