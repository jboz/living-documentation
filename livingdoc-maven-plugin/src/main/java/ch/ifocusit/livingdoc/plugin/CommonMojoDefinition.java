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

import ch.ifocusit.livingdoc.plugin.common.Template;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Julien Boz
 */
public abstract class CommonMojoDefinition extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * Directory where the glossary will be generated
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-docs", required = true)
    protected File outputDirectory;

    /**
     * Output filename
     */
    @Parameter
    protected String outputFileName;

    /**
     * Output format of the glossary (default html, others : adoc)
     */
    @Parameter(defaultValue = "html")
    protected String format;

    @Parameter
    protected Template template;

    void write(final String newValue, final File output) throws MojoExecutionException {
        write(newValue, output, null);
    }

    void write(final String newValue, final File output, Template template) throws MojoExecutionException {
        // update content
        final String newContent = template == null ? newValue : template.process(newValue);

        // create output
        try {
            output.getParentFile().mkdirs();
            IOUtils.write(newContent, new FileOutputStream(output), Charset.defaultCharset());
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Unable to write output file '%s' !", output), e);
        }
    }

    void write(AsciiDocBuilder asciiDocBuilder) throws MojoExecutionException {
        outputDirectory.mkdirs();
        try {
            asciiDocBuilder.writeToFile(outputDirectory.getAbsolutePath(), getFilename().replace(".adoc", ""), StandardCharsets.UTF_8);
            if ("html".equals(format)) {
                Asciidoctor asciidoctor = Asciidoctor.Factory.create();
                asciidoctor.requireLibrary("asciidoctor-diagram");
                asciidoctor.convertFile(getOutput(), OptionsBuilder.options().backend("html5").safe(SafeMode.UNSAFE).asMap());
            }
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Unable to write asciidoc output file '%s' !", getOutput().getAbsolutePath()), e);
        }
    }

    protected abstract String getDefaultFilename();

    protected String getFilename() {
        return StringUtils.defaultIfBlank(outputFileName, getDefaultFilename());
    }

    protected File getOutput() {
        return new File(outputDirectory, getFilename());
    }
}
