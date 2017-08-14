package ch.ifocusit.livingdoc.plugin.domain;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public class Publish {

    public enum Provider {
        confluence;
    }

    /**
     * Publication provider.
     */
    @Parameter(required = true, defaultValue = "confluence")
    private Provider provider = Provider.confluence;

    @Parameter(defaultValue = "${project.build.directory}/generated-docs", required = true)
    private File docFolder;

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

    public Provider getProvider() {
        return provider;
    }

    public File getDocFolder() {
        return docFolder;
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
