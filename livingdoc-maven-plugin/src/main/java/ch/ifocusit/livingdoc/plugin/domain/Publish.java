package ch.ifocusit.livingdoc.plugin.domain;

import org.apache.maven.plugins.annotations.Parameter;

import lombok.ToString;

@ToString
public class Publish {

    public enum Provider {
        confluence;
    }

    /**
     * Publication provider.
     */
    @Parameter(property = "provider", required = true, defaultValue = "confluence")
    private Provider provider = Provider.confluence;

    @Parameter(property = "endpoint", required = true)
    private String endpoint;

    @Parameter(property = "spaceKey", required = true)
    private String spaceKey;

    @Parameter(property = "ancestorId", required = true)
    private String ancestorId;

    @Parameter(property = "username")
    private String username;

    @Parameter(property = "password")
    private String password;

    @Parameter(property = "authorizationToken")
    private String token;

    @Parameter(property = "headers")
    private Header[] headers = new Header[0];

    public Provider getProvider() {
        return provider;
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

    public Header[] getHeaders() {
        return headers;
    }

    public String getToken() {
        return token;
    }

    public static class Header {
        @Parameter(property = "name", required = true)
        private String name;

        @Parameter(property = "value", required = true)
        private String value;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
