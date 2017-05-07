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

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

/**
 * @author Julien Boz
 */
@Mojo(name = "glossary")
public class GlossaryMojo extends CommonMojoDefinition {

    /**
     * List of source directories to browse
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}")
    private List<String> sources;

    /**
     * Annotation used to comment classes that will be included in the glossary (default: Glossary)
     */
    @Parameter(defaultValue = "Glossary")
    private String annotation;

    @Override
    protected String getDefaultFilename() {
        return "glossary.adoc";
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        JavaProjectBuilder javaDocBuilder = buildJavaProjectBuilder();

        AsciiDocBuilder asciiDocBuilder = new AsciiDocBuilder();
        asciiDocBuilder.documentTitle("Glossary");

        javaDocBuilder.getClasses().stream()
                .filter(this::hasAnnotation)
                .forEach(c -> {
                    asciiDocBuilder.sectionTitleLevel1(c.getName());
                    asciiDocBuilder.textLine("");
                    asciiDocBuilder.textLine(c.getComment());
                    asciiDocBuilder.textLine("");
                });
        write(asciiDocBuilder);
    }

    private boolean hasAnnotation(JavaClass javaClass) {
        return javaClass.getAnnotations().stream()
                .anyMatch(a -> a.getType().getFullyQualifiedName().endsWith(annotation));
    }

    private JavaProjectBuilder buildJavaProjectBuilder() {
        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.setErrorHandler(e -> getLog().warn(e.getMessage()));
        sources.stream().map(File::new).forEach(javaDocBuilder::addSourceTree);
        return javaDocBuilder;
    }
}
