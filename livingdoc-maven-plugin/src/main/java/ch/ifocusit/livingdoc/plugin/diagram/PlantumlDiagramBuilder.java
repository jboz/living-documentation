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

import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * @author Julien Boz
 */
public class PlantumlDiagramBuilder {

    private static final String TEST = "Test";
    private static final String PACKAGE_INFO = "package-info";
    private final String IT = "IT";

    private MavenProject project;
    private String prefix;
    private String[] excludes;

    public PlantumlDiagramBuilder(MavenProject project, String prefix, String[] excludes) {
        this.project = project;
        this.prefix = prefix;
        this.excludes = excludes;
    }

    public String generate() throws MojoExecutionException {
        final ClassPath classPath = initClassPath();
        final Set<ClassInfo> allClasses = classPath.getTopLevelClassesRecursive(prefix);

        String diagram = new ClassDiagramBuilder()
                // filter some classes
                .addClasses(allClasses.stream().filter(filter()).map(ClassInfo::load).collect(Collectors.toList()))
                .build();
        return diagram;
    }

    private Predicate<ClassInfo> filter() {
        return ci -> ci.getPackageName().startsWith(prefix)
                && !ci.getSimpleName().equalsIgnoreCase(PACKAGE_INFO)
                && !ci.getSimpleName().endsWith(TEST)
                && !ci.getSimpleName().endsWith(IT)
                && stream(excludes).noneMatch(excl -> ci.getName().matches(excl));
    }

    private ClassPath initClassPath() throws MojoExecutionException {
        final ClassPath classPath;
        try {
            try {
                classPath = ClassPath.from(getRuntimeClassLoader());
            } catch (DependencyResolutionRequiredException e) {
                throw new MojoExecutionException("Unable to load project runtime !", e);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to initialize classPath !", e);
        }
        return classPath;
    }

    private ClassLoader getRuntimeClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
        List<String> runtimeClasspathElements = project.getRuntimeClasspathElements();
        List<String> compileClasspathElements = project.getCompileClasspathElements();
        URL[] runtimeUrls = new URL[runtimeClasspathElements.size() + compileClasspathElements.size()];
        for (int i = 0; i < runtimeClasspathElements.size(); i++) {
            String element = runtimeClasspathElements.get(i);
            runtimeUrls[i] = new File(element).toURI().toURL();
        }

        int j = runtimeClasspathElements.size();

        for (int i = 0; i < compileClasspathElements.size(); i++) {
            String element = compileClasspathElements.get(i);
            runtimeUrls[i + j] = new File(element).toURI().toURL();
        }

        return new URLClassLoader(runtimeUrls, Thread.currentThread().getContextClassLoader());
    }
}
