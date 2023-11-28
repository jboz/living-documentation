package ch.ifocusit.livingdoc.plugin.utils;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.NEWLINE;

public class FileUtils {

    public static String[] read(File file) throws MojoExecutionException {
        if (file == null || !file.exists()) {
            return new String[]{};
        }
        try {
            return IOUtils.toString(file.toURI(), Charset.defaultCharset()).split(NEWLINE);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read header file !", e);
        }
    }

}
