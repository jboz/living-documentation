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
import java.util.List;

/**
 * @author Julien Boz
 */
@Mojo(name = "gherkin", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class GherkinMojo extends AbstractDocsGeneratorMojo {

    /**
     * List of source directories to browse
     */
    @Parameter(defaultValue = "${project.basedir}/src/test/resources/features")
    private List<String> features;

    /**
     * Options like use a custom template (template=path_to_my_template.erb).
     */
    @Parameter(defaultValue = "")
    private String gherkinOptions;

    @Override
    protected String getDefaultFilename() {
        return "gherkin";
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        AsciiDocBuilder asciiDocBuilder = this.createAsciiDocBuilder();
        if (Format.html.equals(format)) {
            asciiDocBuilder.sectionTitleLevel1("Business requirements");
        }
        asciiDocBuilder.textLine(StringUtils.EMPTY);
        features.forEach(path -> {
            try {
                if (Files.notExists(Paths.get(path))) {
                    return;
                }
                Files.walk(Paths.get(path))
                        .filter(p -> p.toString().endsWith(".feature"))
                        .map(Path::toString)
                        // add a line into adoc
                        .forEach(p -> asciiDocBuilder.textLine(String.format("gherkin::%s[%s]", p, gherkinOptions)));
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Error browsing %s", path), e);
            }
        });
        asciiDocBuilder.textLine(StringUtils.EMPTY);
        write(asciiDocBuilder);
    }
}
