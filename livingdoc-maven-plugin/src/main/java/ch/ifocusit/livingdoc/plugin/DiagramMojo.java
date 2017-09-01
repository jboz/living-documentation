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

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractDocsGeneratorMojo;
import ch.ifocusit.livingdoc.plugin.diagram.PlantumlClassDiagramBuilder;
import ch.ifocusit.livingdoc.plugin.domain.Cluster;
import ch.ifocusit.livingdoc.plugin.domain.Color;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.List;

/**
 * @author Julien Boz
 */
@Mojo(name = "diagram", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class DiagramMojo extends AbstractDocsGeneratorMojo {

    private static final Color DEFAULT_ROOT_COLOR = Color.from("wheat", null);

    @Parameter(defaultValue = "${project.groupId}.${project.artifactId}.domain", required = true)
    private String packageRoot;

    @Parameter
    private String[] excludes = new String[0];

    /**
     * Output diagram format
     */
    @Parameter(defaultValue = "plantuml", required = true)
    private DiagramType diagramType;

    /**
     * Output diagram image format
     */
    @Parameter(defaultValue = "png", required = true)
    private DiagramImageType diagramImageType;

    /**
     * Add link into diagram to glossary
     */
    @Parameter(defaultValue = "true")
    private boolean diagramWithLink = true;

    /**
     * Link template to use in diagram.
     */
    @Parameter(defaultValue = "glossary.html")
    private String diagramLinkPage;

    /**
     * Class color for @{@link ch.ifocusit.livingdoc.annotations.RootAggregate} class.
     */
    @Parameter
    private Color rootAggregateColor = DEFAULT_ROOT_COLOR;

    /**
     * Indicate if cluster must automatically detected.
     */
    @Parameter(defaultValue = "true")
    private boolean detectCluster = true;

    /**
     * Effective cluster list.
     */
    @Parameter
    private List<Cluster> clusters;

    /**
     * Header of the diagram
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/livingdoc/diagram.header")
    private File diagramHeader;

    /**
     * Footer of the diagram
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/livingdoc/diagram.footer")
    private File diagramFooter;

    @Parameter(defaultValue = "false")
    private boolean interactive = false;

    @Parameter(defaultValue = "diagram", required = true)
    private String diagramOutputFilename;

    @Parameter
    private String diagramTitle;

    @Parameter
    private boolean diagramWithDependencies = false;

    @Parameter
    private boolean diagramShowMethods = true;

    @Parameter
    private boolean diagramShowFields = true;

    @Override
    protected String getOutputFilename() {
        return diagramOutputFilename;
    }

    @Override
    protected String getTitle() {
        return diagramTitle;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (interactive) {
            diagramWithLink = true;
            diagramImageType = DiagramImageType.svg;
        }
        // generate diagram
        String diagram = generateDiagram();

        if (StringUtils.isBlank(diagram)) {
            // nothing to generate
            return;
        }

        switch (format) {
            case html:
            case adoc:
            case asciidoc:
                AsciiDocBuilder asciiDocBuilder = this.createAsciiDocBuilder();
                appendTitle(asciiDocBuilder);

                switch (diagramType) {
                    case plantuml:
                        asciiDocBuilder.textLine(String.format("[plantuml, %s, format=%s, opts=interactive]", getOutputFilename(), diagramImageType));
                }
                asciiDocBuilder.textLine("----");
                asciiDocBuilder.textLine(diagram);
                asciiDocBuilder.textLine("----");
                // write to file
                write(asciiDocBuilder);
                break;

            case plantuml:
                write(diagram, getOutput(getOutputFilename(), Format.plantuml));
        }
    }

    String generateDiagram() throws MojoExecutionException {

        switch (diagramType) {
            case plantuml:
                PlantumlClassDiagramBuilder builder = new PlantumlClassDiagramBuilder(project, packageRoot, excludes,
                        rootAggregateColor, diagramHeader, diagramFooter, diagramWithDependencies);
                if (onlyAnnotated) {
                    builder.filterOnAnnotation(UbiquitousLanguage.class);
                }
                if (diagramWithLink && !DiagramImageType.png.equals(diagramImageType)) {
                    builder.mapNames(glossaryMapping, UbiquitousLanguage.class, diagramLinkPage + "#" + glossaryAnchorTemplate);
                }
                return builder.generate();
            default:
                throw new NotImplementedException(String.format("format %s is not implemented yet", diagramType));
        }
    }

    public enum DiagramType {
        plantuml;
    }

    public enum DiagramImageType {
        png, svg, txt;
    }
}
