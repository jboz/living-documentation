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

import ch.ifocusit.livingdoc.plugin.utils.ClassLoaderUtil;
import com.google.common.reflect.ClassPath;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.function.Predicate;

import static java.util.Arrays.stream;

/**
 * @author Julien Boz
 */
public abstract class AbstractClassDiagramBuilder {

    private static final String TEST = "Test";
    private static final String PACKAGE_INFO = "package-info";
    protected final MavenProject project;
    protected final String prefix;
    protected final String[] excludes;
    protected final File header;
    protected final File footer;
    private final String IT = "IT";

    public AbstractClassDiagramBuilder(MavenProject project, String prefix, String[] excludes, File header, File footer) {
        this.project = project;
        this.prefix = prefix;
        this.excludes = excludes;
        this.header = header;
        this.footer = footer;
    }

    public abstract AbstractClassDiagramBuilder filterOnAnnotation(Class<? extends Annotation> annotation);

    public abstract String generate() throws MojoExecutionException;

    protected String readHeader() throws MojoExecutionException {
        return read(header);
    }

    protected String readFooter() throws MojoExecutionException {
        return read(footer);
    }

    protected String read(File file) throws MojoExecutionException {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            return IOUtils.toString(file.toURI(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read header file !", e);
        }
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
}
