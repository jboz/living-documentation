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

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractDocsGeneratorMojo;
import ch.ifocusit.livingdoc.plugin.diagram.PlantumlClassDiagramBuilder;
import ch.ifocusit.livingdoc.plugin.domain.Cluster;
import ch.ifocusit.livingdoc.plugin.domain.Color;
import io.github.robwin.markup.builder.asciidoc.AsciiDocBuilder;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Julien Boz
 */
@Mojo(name = "diagram", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM, defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class DiagramMojo extends AbstractDocsGeneratorMojo {

    private static final Color DEFAULT_ROOT_COLOR = Color.from("wheat", null);

    @Parameter
    private String[] excludes = new String[0];

    /**
     * Output diagram format
     */
    @Parameter(property = "livingdoc.diagram.type", defaultValue = "plantuml")
    private DiagramType diagramType;

    /**
     * Output diagram image format
     */
    @Parameter(property = "livingdoc.diagram.image.format", defaultValue = "png")
    private DiagramImageType diagramImageType;

    /**
     * Add link into diagram to glossary
     */
    @Parameter(property = "livingdoc.diagram.link.activate", defaultValue = "false")
    private boolean diagramWithLink = true;

    /**
     * Link template to use in diagram.
     */
    @Parameter(property = "livingdoc.diagram.link.page", defaultValue = "glossary.html")
    private String diagramLinkPage;

    /**
     * Define the root aggregare class.
     */
    @Parameter(property = "livingdoc.diagram.rootAggregate.class")
    private String rootAggregateClassMatcher;

    /**
     * Class color for @{@link ch.ifocusit.livingdoc.annotations.RootAggregate}
     * class.
     */
    @Parameter(property = "livingdoc.diagram.rootAggregate.color")
    private Color rootAggregateColor;

    /**
     * Indicate if cluster must automatically detected.
     */
    @Parameter(property = "livingdoc.diagram.cluster.detect", defaultValue = "true")
    private boolean detectCluster;

    /**
     * Effective cluster list.
     */
    @Parameter
    private List<Cluster> clusters;

    @Parameter(property = "livingdoc.diagram.methods.show", defaultValue = "true")
    private boolean diagramShowMethods;

    @Parameter(property = "livingdoc.diagram.fields.show", defaultValue = "true")
    private boolean diagramShowFields;

    /**
     * Header of the diagram
     */
    @Parameter(property = "livingdoc.diagram.start-options", defaultValue = "${project.basedir}/src/main/livingdoc/diagram-start.options")
    private File diagramStartOptions;

    /**
     * Footer of the diagram
     */
    @Parameter(property = "livingdoc.diagram.end-options", defaultValue = "${project.basedir}/src/main/livingdoc/diagram-end.options")
    private File diagramEndOptions;

    /**
     * Header of the diagram
     */
    @Parameter(property = "livingdoc.diagram.header")
    private String diagramHeader;

    /**
     * Footer of the diagram
     */
    @Parameter(property = "livingdoc.diagram.footer")
    private String diagramFooter;

    @Parameter(property = "livingdoc.diagram.interactive", defaultValue = "false")
    private boolean interactive;

    @Parameter(property = "livingdoc.diagram.output.filename", defaultValue = "diagram")
    private String diagramOutputFilename;

    @Parameter(property = "livingdoc.diagram.title")
    private String diagramTitle;

    @Parameter(property = "livingdoc.diagram.withDeps", defaultValue = "false")
    private boolean diagramWithDependencies;

    /**
     * The graphviz java implementation image processor will be use by default.
     * Set this option to true to use external graphviz image processor.
     */
    @Parameter(property = "livingdoc.diagram.useExternalGraphviz", defaultValue = "false")
    private boolean diagramUseExternalGraphviz;

    @Override
    protected String getOutputFilename() {
        return diagramOutputFilename;
    }

    @Override
    protected String getTitle() {
        return diagramTitle;
    }

    @Override
    public void executeMojo() throws MojoExecutionException {
        if (interactive) {
            diagramWithLink = true;
            diagramImageType = DiagramImageType.svg;
        }
        if (StringUtils.isNotBlank(diagramLinkPage)) {
            diagramWithLink = true;
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

                if (Objects.requireNonNull(diagramType) == DiagramType.plantuml) {
                    asciiDocBuilder.textLine(String.format("[plantuml, %s, format=%s" + (interactive ? ", opts=interactive" : "") + "]", getOutputFilename(), diagramImageType));
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

        getLog().info("generate diagram with packageRoot=" + packageRoot);

        if (diagramType == DiagramType.plantuml) {
            PlantumlClassDiagramBuilder builder = new PlantumlClassDiagramBuilder();
            builder.setProject(project);
            builder.setPrefix(packageRoot);
            builder.setDiagramWithLink(diagramWithLink);
            builder.setExcludes(Stream.of(excludes)
                    .map(s -> s.replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", ""))
                    .toArray(String[]::new));
            builder.setRootAggregateClassMatcher(rootAggregateClassMatcher);
            builder.setRootAggregateColor(rootAggregateColor == null || rootAggregateColor.isEmpty() ? DEFAULT_ROOT_COLOR : rootAggregateColor);
            builder.setHeader(diagramHeader);
            builder.setStartOptions(diagramStartOptions);
            builder.setEndOptions(diagramEndOptions);
            builder.setFooter(diagramFooter);
            builder.setShowMethods(diagramShowMethods);
            builder.setShowFields(diagramShowFields);
            builder.setDiagramWithDependencies(diagramWithDependencies);
            builder.setLinkPage(diagramLinkPage);
            builder.setDiagramTitle(diagramTitle);

            if (onlyAnnotated) {
                builder.filterOnAnnotation(UbiquitousLanguage.class);
            }
            if (diagramWithLink && !DiagramImageType.png.equals(diagramImageType)) {
                builder.mapNames(glossaryMapping);
            }
            builder.setUseExternalGraphiz(diagramUseExternalGraphviz);
            return builder.build();
        }
        throw new NotImplementedException(String.format("format %s is not implemented yet", diagramType));
    }

    public enum DiagramType {
        plantuml;
    }

    public enum DiagramImageType {
        png, svg, txt;
    }

    public DiagramMojo setDiagramTitle(String diagramTitle) {
        this.diagramTitle = diagramTitle;
        return this;
    }

    public DiagramMojo setDiagramType(DiagramType diagramType) {
        this.diagramType = diagramType;
        return this;
    }
}
