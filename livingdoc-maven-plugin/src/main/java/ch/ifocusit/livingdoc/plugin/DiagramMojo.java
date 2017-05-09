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

import ch.ifocusit.livingdoc.annotations.Glossary;
import ch.ifocusit.livingdoc.plugin.diagram.PlantumlAbstractClassDiagramBuilder;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * @author Julien Boz
 */
@Mojo(name = "diagram")
public class DiagramMojo extends CommonMojoDefinition {

    @Parameter(required = true, defaultValue = "${groupid}.${artifactid}.domain")
    private String packageRoot;

    @Parameter
    private String[] excludes = new String[0];

    /**
     * Output diagram format
     */
    @Parameter(defaultValue = "plantuml", required = true)
    private DiagramType diagramType;

    /**
     * Extract only class/field/method annotated with @Glossary
     */
    @Parameter(defaultValue = "false")
    private boolean onlyGlossary = false;

    /**
     * File to use for Glossary mapping.
     */
    @Parameter
    private File glossaryMapping;

    public static enum DiagramType {
        plantuml;
    }

    @Override
    protected String getDefaultFilename() {
        return "diagram";
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // generate diagram
        String diagram = generateDiagram();

        switch (format) {
            case html:
            case adoc:
            case asciidoc:
                AsciiDocBuilder asciiDocBuilder = new AsciiDocBuilder();
                if (!withoutTitle) {
                    asciiDocBuilder.documentTitle("Class diagram");
                }
                switch (diagramType) {
                    case plantuml:
                        asciiDocBuilder.textLine(String.format("[plantuml, %s, png]", getFilenameWithoutExtension()));
                }
                asciiDocBuilder.textLine("----");
                asciiDocBuilder.textLine(diagram);
                asciiDocBuilder.textLine("----");
                // write to file
                write(asciiDocBuilder);
                break;

            case plantuml:
                write(diagram, getOutput(Format.plantuml));
        }
    }

    String generateDiagram() throws MojoExecutionException {

        switch (diagramType) {
            case plantuml:
                PlantumlAbstractClassDiagramBuilder builder = new PlantumlAbstractClassDiagramBuilder(project, packageRoot, excludes);
                if (onlyGlossary) {
                    builder.filterOnAnnotation(Glossary.class);
                }
                if (glossaryMapping != null) {
                    builder.mapNames(glossaryMapping, Glossary.class);
                }
                return builder.generate();
            default:
                throw new NotImplementedException(String.format("format %s is not implemented yet", diagramType));
        }
    }
}
