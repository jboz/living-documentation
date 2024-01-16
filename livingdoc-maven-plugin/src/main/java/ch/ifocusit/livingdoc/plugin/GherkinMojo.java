/*
 * Living Documentation
 *
 * Copyright (C) 2024 Focus IT
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

import static java.nio.charset.Charset.defaultCharset;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import ch.ifocusit.asciidoctor.gherkin.GherkinAsciidocBuilder;
import ch.ifocusit.asciidoctor.gherkin.GherkinExtensionHelper;
import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractDocsGeneratorMojo;
import io.cucumber.messages.types.Feature;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;

/**
 * @author Julien Boz
 */
@Mojo(name = "gherkin", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM, defaultPhase = LifecyclePhase.PROCESS_TEST_RESOURCES)
public class GherkinMojo extends AbstractDocsGeneratorMojo {

    /**
     * List of source directories to browse
     */
    @Parameter(property = "livingdoc.gherkin.features", defaultValue = "${project.basedir}/src/test/resources/features")
    private List<String> features;

    /**
     * Options like use a custom template (template=path_to_my_template.erb).
     */
    @Parameter(property = "livingdoc.gherkin.options")
    private String gherkinOptions;

    @Parameter(property = "livingdoc.gherkin.output.filename", defaultValue = "gherkin", required = true)
    private String gherkinOutputFilename;

    /**
     * Page title or page title prefix when use in combinaison with
     * {@link #gerkinSeparateFeature} option to true
     */
    @Parameter(property = "livingdoc.gherkin.title")
    private String gherkinTitle;

    /**
     * Flag to indicate if feature must be in separate files
     */
    @Parameter(property = "livingdoc.gherkin.separate", defaultValue = "true")
    private boolean gerkinSeparateFeature;

    /**
     * Flag to indicate if generated asciidoc file must use the asciidoc gherkin
     * macro (like include macro)
     */
    @Parameter(property = "livingdoc.gherkin.gherkinAsciidocMacro", defaultValue = "false")
    private boolean gherkinAsciidocMacro;

    /**
     * Replace gherkin processor default template. Must be used with
     * gherkinAsciidocPlugin option to false
     */
    @Parameter(property = "livingdoc.gherkin.gherkinAsciidocTemplate")
    private File gherkinAsciidocTemplate;

    protected boolean somethingWasGenerated = false;

    @Override
    protected String getOutputFilename() {
        return gherkinOutputFilename;
    }

    @Override
    protected String getTitle() {
        return gherkinTitle;
    }

    private final List<AsciiDocBuilder> docBuilders = new ArrayList<>();
    private final AtomicInteger pageCount = new AtomicInteger(0);

    @Override
    public void executeMojo() {

        if (!gerkinSeparateFeature) {
            appendTitle(getDocBuilder(pageCount.get()));
        }

        readFeatures().forEach(path -> {

            getLog().info("Gherkin goal - read " + path);

            if (gerkinSeparateFeature && StringUtils.isNotBlank(getTitle())) {
                // read feature title
                try {
                    Feature feature = GherkinExtensionHelper
                            .parse(readFileToString(FileUtils.getFile(path), defaultCharset()));
                    String title = StringUtils.defaultString(getTitle(), EMPTY) + " " + feature.getName();
                    appendTitle(getDocBuilder(pageCount.get()), title);
                } catch (IOException e) {
                    throw new IllegalStateException("Error reading " + path, e);
                }
            }
            if (gherkinAsciidocMacro) {
                getDocBuilder(pageCount.get()).textLine(String.format("gherkin::%s[%s]", path, gherkinOptions));
            } else {
                try {
                    getDocBuilder(pageCount.get()).textLine(
                            gherkinBuilder(gherkinOptions)
                                    .featureContent(readFileToString(FileUtils.getFile(path), defaultCharset()))
                                    .build());
                } catch (IOException e) {
                    throw new IllegalStateException("Error reading " + path, e);
                }
            }
            getDocBuilder(pageCount.get()).textLine(EMPTY);
            somethingWasGenerated = true;

            if (gerkinSeparateFeature) {
                pageCount.incrementAndGet();
            }
        });

        if (!somethingWasGenerated) {
            // nothing generated
            return;
        }

        for (int i = 0; i < docBuilders.size(); i++) {
            try {
                write(docBuilders.get(i), getOutputFilename() + (docBuilders.size() > 1 ? "_" + i : ""));
            } catch (MojoExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static GherkinAsciidocBuilder gherkinBuilder(String input) throws IOException {
        GherkinAsciidocBuilder builder = GherkinAsciidocBuilder.builder();

        String[] optionsString = input.split(",");
        for (String option : optionsString) {
            String[] pair = option.split("=");
            if (pair.length == 2) {
                if ("withChildSeparator".equalsIgnoreCase(pair[0])) {
                    builder.withChildSeparator(Boolean.valueOf(pair[1]));
                } else if ("withKeyword".equalsIgnoreCase(pair[0])) {
                    builder.withKeyword(Boolean.valueOf(pair[1]));
                } else if ("withTitle".equalsIgnoreCase(pair[0])) {
                    builder.withTitle(Boolean.valueOf(pair[1]));
                } else if ("template".equalsIgnoreCase(pair[0])) {
                    builder.templateContent(readFileToString(FileUtils.getFile(pair[1]), Charset.defaultCharset()));
                }
            }
        }

        return builder;
    }

    private AsciiDocBuilder getDocBuilder(int index) {
        if (docBuilders.size() <= index) {
            docBuilders.add(createAsciiDocBuilder());
        }
        return docBuilders.get(index);
    }

    private Stream<String> readFeatures() {
        return features.stream()
                .filter(path -> Files.exists(Paths.get(path)))
                .flatMap(path -> {
                    try {
                        // noinspection resource
                        return Files.walk(Paths.get(path)).filter(p -> p.toString().endsWith(".feature"));
                    } catch (IOException e) {
                        throw new IllegalStateException(String.format("Error browsing %s", path), e);
                    }
                }).map(Path::toString);
    }
}
