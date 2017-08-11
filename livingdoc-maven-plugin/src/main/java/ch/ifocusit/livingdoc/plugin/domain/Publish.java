package ch.ifocusit.livingdoc.plugin.domain;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public class Publish {
    @Parameter(defaultValue = "${project.build.directory}/generated-docs")
    private File asciidocFolder;

    @Parameter(required = true)
    private String endpoint;

    @Parameter(required = true)
    private String spaceKey;

    @Parameter(required = true)
    private String ancestorId;

    @Parameter
    private String username;

    @Parameter
    private String password;

    public File getAsciidocFolder() {
        return asciidocFolder;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public String getAncestorId() {
        return ancestorId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
