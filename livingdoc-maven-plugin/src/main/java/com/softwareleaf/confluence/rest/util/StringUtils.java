/**
 * Copyright(C) 2015 <a href="mailto:Jonathon.a.hope@gmail.com" >Jonathon Hope</a>
 */

package com.softwareleaf.confluence.rest.util;

import com.google.common.collect.Iterables;

import java.util.Arrays;

/**
 * A collection of static {@code String} utilities.
 *
 * @author Jonathon Hope
 */
public final class StringUtils {

    /**
     * Converts the {@code String str}, to {@code "camelCase"} format.
     *
     * @param str
     *            the {@code String} to format.
     * @return the formatted {@code String}.
     */
    public static String convertToCamelCase(final String str) {
        final String[] parts = str.split("_");
        if (parts.length > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(parts[0].toLowerCase());
            for (String s : Iterables.skip(Arrays.asList(parts), 1)) {
                sb.append(Character.toUpperCase(s.charAt(0)));
                if (s.length() > 1) {
                    sb.append(s.substring(1, s.length()).toLowerCase());
                }
            }
            return sb.toString();
        }
        return str;
    }

    /**
     * Converts {@literal "UpperCamelCase"}.
     * 
     * <pre>
     * {@literal
     *     "RENDER_MODE" => "RenderMode"
     * }
     * </pre>
     *
     * @param str
     *            the String to convert. The Capital letters are chosen by the positions of {@code '_'} in the {@code String}.
     * @return a String in Upper Camel Case format.
     */
    public static String convertToUpperCamel(final String str) {
        StringBuilder sb = new StringBuilder();
        for (String s : str.split("_")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1, s.length()).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * Convert to {@literal "Proper case"}; capital first letter, lowercase suffix.
     *
     * @param s
     *            the {@code String} to convert to {@literal "ProperCase"}.
     * @return the {@code String s} converted to {@literal "ProperCase"}.
     */
    public static String toProperCase(final String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

}
