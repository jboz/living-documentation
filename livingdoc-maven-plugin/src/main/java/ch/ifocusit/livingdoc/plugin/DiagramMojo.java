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

import ch.ifocusit.livingdoc.plugin.diagram.PlantumlDiagramBuilder;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Julien Boz
 */
@Mojo(name = "diagram")
public class DiagramMojo extends CommonMojoDefinition {

    @Parameter(required = true)
    private String prefix;

    @Parameter
    private String[] excludes = new String[0];

    @Parameter(defaultValue = "plantuml", required = true)
    private DiagramType diagramType;

    public static enum DiagramType {
        plantuml, dot;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // generate diagram
        String diagram = generateDiagram();
        // write to file
        writeDiagram(diagram);
    }

    String generateDiagram() throws MojoExecutionException {

        switch (diagramType) {
            case plantuml:
                return new PlantumlDiagramBuilder(project, prefix, excludes).generate();
            default:
                throw new NotImplementedException(String.format("format %s is not implemented yet", diagramType));
        }
    }

    void writeDiagram(String diagram) throws MojoExecutionException {
        write(diagram, output);
    }
}
