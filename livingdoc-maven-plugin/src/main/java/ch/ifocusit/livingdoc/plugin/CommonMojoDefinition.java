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

import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
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
    public static final String GLOSSARY_ANCHOR = "glossaryid-{0}";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Component
    RepositorySystem repositorySystem;

    /**
     * Directory where the glossary will be generated
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-docs", required = true)
    private File outputDirectory;

    /**
     * Output filename
     */
    @Parameter
    private String outputFilename;

    /**
     * Output format of the glossary (default html, others : adoc)
     */
    @Parameter(defaultValue = "html")
    Format format;

//    /**
//     * Header of the generated asciidoc file
//     */
//    @Parameter
//    private File headerAsciidoc;
//
//    /**
//     * Footer of generated asciidoc file
//     */
//    @Parameter
//    private File footerAsciidoc;

    /**
     * Temple for glossary anchor.
     */
    @Parameter(defaultValue = GLOSSARY_ANCHOR)
    String glossaryAnchorTemplate;

    /**
     * File to use for UbiquitousLanguage mapping.
     */
    @Parameter
    File glossaryMapping;

    /**
     * Indicate that only annotated classes/fields will be used.
     */
    @Parameter(defaultValue = "false")
    boolean onlyAnnotated = false;

    public enum Format {
        asciidoc, adoc, html, plantuml
    }

    void write(final String newContent, final File output) throws MojoExecutionException {
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
        File output = getOutput(Format.adoc);
        try {
            // write adco file
            asciiDocBuilder.writeToFile(outputDirectory.getAbsolutePath(), getFilenameWithoutExtension(), StandardCharsets.UTF_8);
            if (Format.html.equals(format)) {
                Asciidoctor asciidoctor = Asciidoctor.Factory.create();
                asciidoctor.requireLibrary("asciidoctor-diagram");
                // write html from .adoc
                asciidoctor.convertFile(output, OptionsBuilder.options().backend("html5").safe(SafeMode.UNSAFE).asMap());
            }
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Unable to convert asciidoc file '%s' to html !", output.getAbsolutePath()), e);
        }
    }

    protected abstract String getDefaultFilename();

    private String getFilename() {
        return StringUtils.defaultIfBlank(outputFilename, getDefaultFilename());
    }

    String getFilenameWithoutExtension() {
        String filename = getFilename();
        return filename.indexOf(".") > 0 ? filename.substring(0, filename.lastIndexOf(".")) : filename;
    }

    File getOutput(Format desiredExtension) {
        String filename = StringUtils.defaultIfBlank(outputFilename, getDefaultFilename());
        filename = filename.endsWith("." + desiredExtension.name()) ? filename : filename + "." + desiredExtension;
        return new File(outputDirectory, filename);
    }

    AsciiDocBuilder createAsciiDocBuilder() {
        AsciiDocBuilder asciiDocBuilder = new AsciiDocBuilder();
        asciiDocBuilder.textLine(":sectlinks:");
        asciiDocBuilder.textLine(":sectanchors:");
        return asciiDocBuilder;
    }
}
