package ch.ifocusit.livingdoc.plugin.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;

public class MustacheUtil {

    public static String execute(File externalTemplate, String defautInternalTemplate, Map<String, Object> scopes) throws IOException, URISyntaxException {
        Writer writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();

        String templateName = externalTemplate != null ? externalTemplate.getName() : defautInternalTemplate;
        Reader template = externalTemplate != null ? Files.newBufferedReader(externalTemplate.toPath()) : getReader(defautInternalTemplate);
        Mustache mustache = mf.compile(template, templateName);
        mustache.execute(writer, scopes);
        return writer.toString();
    }

    private static Reader getReader(String classpathResource) {
        return new InputStreamReader(MustacheUtil.class.getResourceAsStream(classpathResource));
    }
}
