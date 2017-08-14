package com.softwareleaf.confluence.rest.macro;

import com.softwareleaf.confluence.rest.util.StringUtils;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a confluence Table of Contents Macro.
 * <p>
 * Usage
 * 
 * <pre>
 * {
 *     &#64;literal
 *     String macro = TableOfContentsMacro
 *         .builder()
 *         .enableNumbering()
 *         .outputType(OutputType.LIST)
 *         .bulletPointStyle(ListStyle.NONE)
 *         .build()
 *         .toWikiMarkup();
 * }
 * </pre>
 *
 * @author Jonathon Hope
 * @see <a href="https://confluence.atlassian.com/display/DOC/Table+of+Contents+Macro">table of Contents Macro Documentation</a>
 */
public class TableOfContentsMacro {

    /**
     * Parameters are options that you can set to control the content or format of the macro output.
     */
    public enum Parameters {
        /**
         * Enables section numbering.
         * 
         * <pre>
         * {@literal
         *      <ac:parameter ac:name=\"outline\">true</ac:parameter>
         * }
         * </pre>
         */
        OUTLINE,
        /**
         * @see TableOfContentsMacro.OutputType
         */
        TYPE,
        /**
         * The style of bullet point to use.
         *
         * @see TableOfContentsMacro.ListStyle
         */
        STYLE,
        /**
         * Sets the indent for a list according to CSS quantities. Entering 10px will successively indent heading groups by 10px.
         * For example, level 1 headings will be indented 10px and level 2 headings will be indented an additional 10px.
         *
         * @see TableOfContentsMacro.Indentation
         */
        INDENT,
        /**
         * This parameter applies to flat lists only. Configures the separator used for separating content items.
         *
         * @see TableOfContentsMacro.Separator
         */
        SEPARATOR,
        /**
         * Select the highest heading level to start your TOC list. For example, entering {@code 2} will include levels {@literal 
         * <h2>} and lower levels, but will not include level {@literal 
         * <h1>} headings and below.
         */
        MIN_LEVEL,
        /**
         * Select the lowest heading level to include. For example, entering {@code 2} will include levels {@literal 
         * <h1>} and {@literal 
         * <h2>}, but will not include level {@literal 
         * <h3>} headings and below.
         */
        MAX_LEVEL,
        /**
         * By default, the TOC is set to print. If you clear the check box, the TOC will not be visible when you print the page.
         */
        PRINTABLE;

        @Override
        public String toString() {
            return StringUtils.convertToUpperCamel(this.name());
        }
    }

    /**
     * The output style of the table of contents.
     */
    public enum OutputType {
        /**
         * Causes the output style of the table of contents to be laid out in a flat series of links separated by square brackets.
         * <p>
         * For Example:
         * 
         * <pre>
         * {@literal
         *      [ 1 Summary ] [ 2 Cacheable HTTPS response ] [ 2.1 Issue Background ] ...
         * }
         * </pre>
         */
        FLAT,
        /**
         * Causes the output style of the table of contents to be laid out as an indented list.
         * <p>
         * For example:
         * 
         * <pre>
         * {@literal
         *  1 Summary
         *  2 Cacheable HTTPS response
         *      2.1 Issue Background
         *          2.1.1 quickstreamdev3/
         *          2.1.2 quickstreamdev3/PreregisteredAccountsReportActionServlet
         * }
         * </pre>
         */
        LIST;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * The bullet point style to use for each list item.
     */
    public enum ListStyle {
        /**
         * none, no list style is displayed
         */
        NONE,
        /**
         * circle, the list style is a circle
         */
        CIRCLE,
        /**
         * disc, the list style is a filled circle. This is the typical bullet list, and is used for this example list.
         */
        DISC,
        /**
         * square, the list style is a square
         */
        SQUARE,
        /**
         * decimal, the list is numbered (1, 2, 3, 4, 5)
         */
        DECIMAL,
        /**
         * lower-alpha, the list is lower-case, alphabetised (a, b, c, d, e)
         */
        LOWER_ALPHA,
        /**
         * lower-roman, the list style is lower roman numerals (i, ii, iii, iv, v, vi)
         */
        LOWER_ROMAN,
        /**
         * upper-roman, the list style is upper roman numerals (I, II, III, IV, V, VI)
         */
        UPPER_ROMAN;

        @Override
        public String toString() {
            // replace the underscore with a dash and print as lower case
            return this.name().replace('_', '-').toLowerCase();
        }
    }

    /**
     * The available indentation levels.
     */
    public enum Indentation {
        TEN_PX, TWENTY_PX, THIRTY_PX, FORTY_PX;

        private String convert() {
            switch (name()) {
                case "TEN_PX":
                    return "10px";
                case "TWENTY_PX":
                    return "20px";
                case "THIRTY_PX":
                    return "30px";
                default:
                    return "40px";
            }
        }

        @Override
        public String toString() {
            return convert();
        }
    }

    /**
     * This parameter applies to flat lists only. You can enter any of the following values:
     */
    public enum Separator {
        /**
         * brackets � Each item is enclosed by square brackets: [ ].
         */
        BRACKETS,
        /**
         * braces � Each item is enclosed by braces: { }.
         */
        BRACES,
        /**
         * parens � Each item is enclosed by parentheses: ( ).
         */
        PARENS,
        /**
         * pipe � Each item is separated by a pipe:
         */
        PIPE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    // API

    /**
     * Stores the parameters of this code block macro.
     */
    private EnumMap<Parameters, String> parameters;

    /**
     * Constructor.
     *
     * @param builder
     *            the factory object used to build an instance of this class.
     */
    public TableOfContentsMacro(final Builder builder) {
        this.parameters = builder.parameters;
    }

    /**
     * @return a String containing the XML markup for the confluence storage format.
     */
    public String toMarkup() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<ac:structured-macro ac:name=\"toc\">");
        for (Map.Entry<Parameters, String> entry : parameters.entrySet()) {
            sb.append("<ac:parameter ac:name=\"");
            sb.append(entry.getKey().toString());
            sb.append("\">");
            sb.append(entry.getValue());
            sb.append("</ac:parameter>");
        }
        sb.append("</ac:structured-macro>");
        return sb.toString();
    }

    /**
     * Builder factory method.
     *
     * @return a {@code Builder} instance for chain-building a CodeBlockMacro.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A class for implementing the Builder Pattern for {@code CodeBlockMacro}.
     */
    public static class Builder {
        private EnumMap<Parameters, String> parameters;

        // check if a parameter has already been set.
        private boolean parameterNotSet(final Parameters p) {
            return !parameters.containsKey(p);
        }

        // Constructor.
        private Builder() {
            parameters = new EnumMap<>(Parameters.class);
        }

        /**
         * Creates an instance of {@code TableOfContentsMacro}.
         *
         * @return an instance {@code TableOfContentsMacro} that was configured by this Builder.
         */
        public TableOfContentsMacro build() {
            return new TableOfContentsMacro(this);
        }

        /**
         * Enables numbering of sections.
         *
         * @return {@code this}.
         */
        public Builder enableNumbering() {
            if (parameterNotSet(Parameters.OUTLINE)) {
                parameters.put(Parameters.OUTLINE, "true");
            }
            return this;
        }

        /**
         * Sets the output type, which is really the style of Table of Contents.
         *
         * @param outputType
         *            the style of Table of Contents to produce.
         * @return {@code this}.
         */
        public Builder outputType(final OutputType outputType) {
            if (parameterNotSet(Parameters.TYPE)) {
                parameters.put(Parameters.TYPE, outputType.toString());
            }
            return this;
        }

        /**
         * Set the bullet point style to use for each list item.
         *
         * @param listStyle
         *            the style to set.
         * @return {@code this}.
         */
        public Builder bulletPointStyle(final ListStyle listStyle) {
            if (parameterNotSet(Parameters.TYPE)) {
                parameters.put(Parameters.STYLE, listStyle.toString());
            }
            return this;
        }

        /**
         * Set the indentation level.
         *
         * @param indentation
         *            the indentation level to use.
         * @return {@code this}.
         */
        public Builder indentation(final Indentation indentation) {
            if (parameterNotSet(Parameters.INDENT)) {
                parameters.put(Parameters.INDENT, indentation.toString());
            }
            return this;
        }

        /**
         * Set builtin separator.
         *
         * @param separator
         *            the builtin separator to use.
         * @return {@code this}.
         */
        public Builder separator(final Separator separator) {
            if (parameterNotSet(Parameters.SEPARATOR)) {
                parameters.put(Parameters.SEPARATOR, separator.toString());
            }
            return this;
        }

        /**
         * Set a custom separator. Each item is separated by the value you enter. You can enter any text as a separator, for example
         * "***". If using a custom separator, be aware that text displays exactly as entered, with no additional white space to
         * further separate the characters.
         *
         * @param separator
         *            the custom separator to use.
         * @return {@code this}.
         */
        public Builder separator(final String separator) {
            if (parameterNotSet(Parameters.SEPARATOR)) {
                parameters.put(Parameters.SEPARATOR, separator);
            }
            return this;
        }

        /**
         * Set the minimum heading level to include in the table of contents
         *
         * @param i
         *            the number between 1 and 6 corresponding to the {@literal <h1> - <h6>}. For example: {@code 1} for
         *            {@literal <h1>}.
         * @return {@code this}.
         */
        public Builder minHeadingLevel(final int i) {
            if (i <= 6 && i > 0) // h1 - h6
            {
                if (parameterNotSet(Parameters.MIN_LEVEL)) {
                    parameters.put(Parameters.MIN_LEVEL, Integer.toString(i));
                }
            }
            return this;
        }

        /**
         * Set the maximum heading level to include in the table of contents
         *
         * @param i
         *            the number between 1 and 6 corresponding to the {@literal <h1> - <h6>}. For example: {@code 1} for
         *            {@literal <h1>}.
         * @return {@code this}.
         */
        public Builder maxHeadingLevel(final int i) {
            if (i <= 6 && i > 0) // h1 - h6
            {
                if (parameterNotSet(Parameters.MAX_LEVEL)) {
                    parameters.put(Parameters.MAX_LEVEL, Integer.toString(i));
                }
            }
            return this;
        }

        /**
         * Disables inclusion of the TOC when the page is printed.
         *
         * @return {@code this}.
         */
        public Builder disablePrinting() {
            if (parameterNotSet(Parameters.PRINTABLE)) {
                parameters.put(Parameters.PRINTABLE, "false");
            }
            return this;
        }

    }

}
