package ch.ifocusit.livingdoc.plugin.domain;

import org.apache.maven.plugins.annotations.Parameter;

public class Color {

    @Parameter
    private String backgroundColor;

    @Parameter
    private String borderColor;


    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public static Color from(String bg, String border) {
        Color color = new Color();
        color.backgroundColor = bg;
        color.borderColor = border;
        return color;
    }
}
