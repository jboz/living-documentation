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
package ch.ifocusit.livingdoc.plugin;

import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractDocsGeneratorMojo;
import com.github.domgold.doctools.asciidoctor.gherkin.MapFormatter;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.nio.charset.Charset.defaultCharset;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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

    @Parameter(property = "livingdoc.gherkin.title")
    private String gherkinTitle;

    /**
     * Flag to indicate if feature must be in separate files
     */
    @Parameter(property = "livingdoc.gherkin.separate", defaultValue = "false")
    private boolean gerkinSeparateFeature;

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

            if (gerkinSeparateFeature) {
                // read feature title
                try {
                    Map<String, Object> parsed = MapFormatter.parse(readFileToString(FileUtils.getFile(path), defaultCharset()));
                    String title = StringUtils.defaultString(getTitle(), EMPTY) + parsed.get("name");
                    appendTitle(getDocBuilder(pageCount.get()), title);
                } catch (IOException e) {
                    throw new IllegalStateException("Error reading " + path, e);
                }
            }
            getDocBuilder(pageCount.get()).textLine(String.format("gherkin::%s[%s]", path, gherkinOptions));
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
                        return Files.walk(Paths.get(path)).filter(p -> p.toString().endsWith(".feature"));
                    } catch (IOException e) {
                        throw new IllegalStateException(String.format("Error browsing %s", path), e);
                    }
                }).map(Path::toString);
    }
}
