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
package ch.ifocusit.livingdoc.plugin.baseMojo;

import io.github.robwin.markup.builder.asciidoc.AsciiDoc;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.repository.RepositorySystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Julien Boz
 */
public abstract class AbstractDocsGeneratorMojo extends AbstractAsciidoctorMojo {
    public static final String GLOSSARY_ANCHOR = "glossaryid-{0}";

    private static final String TITLE_MARKUP = AsciiDoc.DOCUMENT_TITLE.toString();

    @Component
    protected RepositorySystem repositorySystem;

    /**
     * Output format of the glossary (default html, others : adoc)
     */
    @Parameter(defaultValue = "html")
    protected Format format;

    /**
     * Temple for glossary anchor.
     */
    @Parameter(defaultValue = GLOSSARY_ANCHOR)
    // TODO remove this parameter, no more need to change this behavior
    protected String glossaryAnchorTemplate;

    /**
     * File to use for UbiquitousLanguage mapping.
     */
    @Parameter
    protected File glossaryMapping;

    // TODO active header/footer capabilities
//    /**
//     * Header of the generated asciidoc file
//     */
//    @Parameter
//    private File headerAsciidoc;

//    /**
//     * Footer of generated asciidoc file
//     */
//    @Parameter
//    private File footerAsciidoc;

    /**
     * Indicate that only annotated classes/fields will be used.
     */
    @Parameter(defaultValue = "false")
    protected boolean onlyAnnotated = false;

    /**
     * @return the filename is defined by each mojo
     */
    protected abstract String getOutputFilename();

    /**
     * @return the document title is defined by each mojo
     */
    protected abstract String getTitle();

    /**
     * Simple write content to a file.
     *
     * @param newContent : file content
     * @param output     : destination file
     * @throws MojoExecutionException
     */
    protected void write(final String newContent, final File output) throws MojoExecutionException {
        try {
            output.getParentFile().mkdirs();
            IOUtils.write(newContent, new FileOutputStream(output), Charset.defaultCharset());
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Unable to write output file '%s' !", output), e);
        }
    }

    /**
     * Write asciidoc to defined output in defined format
     *
     * @param asciiDocBuilder : asciidoc content
     * @throws MojoExecutionException
     */
    protected void write(AsciiDocBuilder asciiDocBuilder) throws MojoExecutionException {
        write(asciiDocBuilder, getOutputFilename());
    }

    protected void write(AsciiDocBuilder asciiDocBuilder, String outputFilename) throws MojoExecutionException {
        write(asciiDocBuilder, format, outputFilename);
    }

    protected AsciiDocBuilder createAsciiDocBuilder() {
        AsciiDocBuilder asciiDocBuilder = new AsciiDocBuilder();
        asciiDocBuilder.textLine(":sectlinks:");
        asciiDocBuilder.textLine(":sectanchors:");
        return asciiDocBuilder;
    }

    protected void appendTitle(AsciiDocBuilder asciiDocBuilder) {
        String definedTitle = getTitle();
        if (StringUtils.isNotBlank(definedTitle)) {
            String title = definedTitle.startsWith(TITLE_MARKUP) ? definedTitle : TITLE_MARKUP + definedTitle;
            appendTitle(asciiDocBuilder, title);
        }
    }

    protected void appendTitle(AsciiDocBuilder asciiDocBuilder, String title) {
        asciiDocBuilder.textLine(title).newLine();
    }
}
