package com.softwareleaf.confluence.rest.macro;

/**
 * Represents an expandable body of rich text.
 *
 * @author Jonathon Hope
 * @see <a href="https://confluence.atlassian.com/display/DOC/Expand+Macro"> Expand Macro docs </a>
 */
public class ExpandMacro {

    /**
     * The title of the expandable.
     */
    private String title;
    /**
     * The contents of the expandable.
     */
    private String body;

    /**
     * @param builder
     *            the builder factory to use.
     */
    protected ExpandMacro(final Builder builder) {
        this.title = builder.title;
        this.body = builder.body;
    }

    /**
     * @return converts this instance to confluence markup.
     */
    public String toMarkup() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ac:structured-macro ac:name=\"expand\">");
        sb.append("<ac:parameter ac:name=\"title\">");
        sb.append(title);
        sb.append("</ac:parameter>");

        sb.append("<ac:rich-text-body>");
        sb.append(body);
        sb.append("</ac:rich-text-body>");
        sb.append("</ac:structured-macro>");
        return sb.toString();
    }

    /**
     * Builder factory method.
     *
     * @return a {@code Builder} instance for chain-building a {@code ExpandMacro}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A class for implementing the Builder Pattern for {@code ExpandMacro}.
     */
    public static class Builder {

        private String title;
        private String body;

        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public ExpandMacro build() {
            return new ExpandMacro(this);
        }

    }

}
