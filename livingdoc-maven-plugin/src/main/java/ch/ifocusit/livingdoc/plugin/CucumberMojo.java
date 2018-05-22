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

import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractDocsGeneratorMojo;
import com.github.cukedoctor.Cukedoctor;
import com.github.cukedoctor.api.CukedoctorConverter;
import com.github.cukedoctor.api.DocumentAttributes;
import com.github.cukedoctor.api.model.Feature;
import com.github.cukedoctor.config.GlobalConfig;
import com.github.cukedoctor.parser.FeatureParser;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Julien Boz
 */
@Mojo(name = "cucumber", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class CucumberMojo extends AbstractDocsGeneratorMojo {

    @Parameter(property = "livingdoc.cucumber.output.filename", defaultValue = "cucumber", required = true)
    private String cucumberOutputFilename;

    @Parameter(property = "livingdoc.cucumber.title")
    private String cucumberTitle;

    /**
     * Cucumber execution result json files.
     */
    @Parameter(defaultValue = "${project.build.directory}/cucumber/result.json")
    private List<String> jsons;

    @Parameter(required = false)
    private Boolean hideSummarySection;

    @Override
    protected String getOutputFilename() {
        return cucumberOutputFilename;
    }

    @Override
    protected String getTitle() {
        return cucumberTitle;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (hideSummarySection != null) {
            System.setProperty("HIDE_SUMMARY_SECTION", Boolean.toString(hideSummarySection));
        }
        AsciiDocBuilder asciiDocBuilder = this.createAsciiDocBuilder();
        appendTitle(asciiDocBuilder);

        List<Feature> featuresFound = new ArrayList<>();
        jsons.forEach(json -> {
            try {
                Files.walk(Paths.get(json))
                        .map(Path::toString)
                        // add a line into adoc
                        .forEach(path -> {
                            featuresFound.addAll(FeatureParser.findAndParse(path));
                        });
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Error browsing %s", json), e);
            }
        });
        if (featuresFound.isEmpty()) {
            getLog().warn("No cucumber json files found !");
            return;
        } else {
            getLog().info("Generating living documentation for " + featuresFound.size() + " feature(s)...");
        }

        DocumentAttributes documentAttributes = GlobalConfig.newInstance().getDocumentAttributes()
                .backend(format.name().toLowerCase())
                .docTitle(getTitle())
//                .toc(toc.name().toLowerCase())
//                .revNumber(docVersion)
//                .hardBreaks(hardBreaks)
//                .numbered(numbered)
                ;

        CukedoctorConverter converter = Cukedoctor.instance(featuresFound, documentAttributes);
        String report = converter.renderDocumentation();
        asciiDocBuilder.textLine(report).newLine();

        write(asciiDocBuilder);
    }
}
