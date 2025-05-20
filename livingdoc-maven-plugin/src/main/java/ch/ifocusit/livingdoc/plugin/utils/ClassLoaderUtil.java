/*
 * Living Documentation
 *
 * Copyright (C) 2025 Focus IT
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
package ch.ifocusit.livingdoc.plugin.utils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * @author Julien Boz
 */
public class ClassLoaderUtil {

    public static ClassLoader getRuntimeClassLoader(MavenProject project) throws MojoExecutionException {
        try {
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

        } catch (Exception e) {
            throw new MojoExecutionException("Unable to load project runtime !", e);
        }
    }
}
