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
package ch.ifocusit.livingdoc.plugin.baseMojo;

import ch.ifocusit.livingdoc.plugin.publish.confluence.PlantumlMacroBlockProcessor;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.asciidoctor.SafeMode.UNSAFE;

/**
 * @author Julien Boz
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class AbstractAsciidoctorMojo extends AbstractMojo {

    protected static final String TEMPLATES_OUTPUT = "${project.build.directory}/asciidoc-templates";
    private static final String TEMPLATES_CLASSPATH_PATTERN = "templates/*";

    protected static final String PLANTUML_MACRO_NAME = "plantuml";

    public enum Format {
        asciidoc, adoc, html, plantuml
    }

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * Directory where the documents will be generated
     */
    @Parameter(property = "livingdoc.output.directory", defaultValue = "${project.build.directory}/generated-docs", required = true)
    protected File generatedDocsDirectory;

    /**
     * Templates directories.
     */
    @Parameter(property = "livingdoc.asciidocTemplate", defaultValue = TEMPLATES_OUTPUT, readonly = true)
    private File asciidocTemplates;

    /**
     * Templates directories.
     */
    @Parameter(property = "livingdoc.newlineCharacter", defaultValue = "\r\n", readonly = true)
    private String newlineCharacter;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.setProperty("line.separator", "\r\n");
        if (StringUtils.isNotBlank(newlineCharacter)) {
            System.setProperty("line.separator", newlineCharacter);
        }
        this.executeMojo();
    }

    public abstract void executeMojo() throws MojoExecutionException, MojoFailureException;

    protected void write(AsciiDocBuilder asciiDocBuilder, Format format, String outputFilename) throws MojoExecutionException {
        generatedDocsDirectory.mkdirs();
        File output = getOutput(outputFilename, Format.adoc);
        try {
            // write adco file
            asciiDocBuilder.writeToFile(generatedDocsDirectory.getAbsolutePath(), FilenameUtils.removeExtension(outputFilename), StandardCharsets.UTF_8);
            if (Format.html.equals(format)) {
                // convert adoc to html
                createAsciidoctor().convertFile(output, options());
            }
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Unable to convert asciidoc file '%s' to html !", output.getAbsolutePath()), e);
        }
    }

    protected File getOutput(String filename, AbstractDocsGeneratorMojo.Format desiredExtension) {
        filename = FilenameUtils.isExtension(filename, desiredExtension.name()) ? filename : filename + "." + desiredExtension;
        return new File(generatedDocsDirectory, filename);
    }

    public Asciidoctor createAsciidoctor() {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.requireLibrary("asciidoctor-diagram");
        JavaExtensionRegistry extensionRegistry = asciidoctor.javaExtensionRegistry();
        // override plantuml macro
        extensionRegistry.blockMacro(PLANTUML_MACRO_NAME, PlantumlMacroBlockProcessor.class);
        return asciidoctor;
    }

    protected Options options() {
        this.asciidocTemplates.mkdirs();

        String imagesOutputDirectory = generatedDocsDirectory.getAbsolutePath();

        return Options.builder()
                .backend("html5")
                .safe(UNSAFE)
                .baseDir(generatedDocsDirectory)
                .templateDirs(asciidocTemplates)
                .attributes(Attributes.builder()
                        .attribute("imagesoutdir", imagesOutputDirectory)
                        .attribute("outdir", imagesOutputDirectory)
                        .build())
                .build();
    }

    protected void extractTemplatesFromJar() {
        this.asciidocTemplates.mkdirs();
        try {
            Arrays.asList(new PathMatchingResourcePatternResolver().getResources(TEMPLATES_CLASSPATH_PATTERN))
                    .forEach(templateResource -> {
                        try {
                            copyInputStreamToFile(templateResource.getInputStream(), new File(this.asciidocTemplates,
                                    Objects.requireNonNull(templateResource.getFilename())));
                        } catch (IOException e) {
                            throw new RuntimeException("Could not write template to target file", e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
