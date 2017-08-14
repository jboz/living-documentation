package com.softwareleaf.confluence.rest.macro;

/**
 * The HTML macro allows you to add HTML code to a Confluence page.
 *
 * @author Jonathon Hope
 * @see <a href="https://confluence.atlassian.com/display/DOC/HTML+Macro">HTML Macro Documentation</a>
 */
public class HtmlMacro {
    /**
     * The HTML source.
     */
    private final String content;

    /**
     * Constructor.
     *
     * @param content
     *            the HTML source. NOTE: This cannot contain a {@literal <DOCTYPE html>} tag.
     */
    public HtmlMacro(final String content) {
        this.content = content;
    }

    /**
     * @return a structured macro (XML formatted) String, according to the confluence HTML Macro documentation.
     */
    public String toMarkup() {
        final StringBuilder sb = new StringBuilder(content.length() + 140);
        sb.append("<ac:structured-macro ac:name=\"html\">");
        sb.append("<ac:plain-text-body>");
        sb.append("<![CDATA[");
        sb.append(content);
        sb.append("]]");
        sb.append("</ac:plain-text-body>");
        sb.append("</ac:structured-macro>");

        return sb.toString();
    }

}
