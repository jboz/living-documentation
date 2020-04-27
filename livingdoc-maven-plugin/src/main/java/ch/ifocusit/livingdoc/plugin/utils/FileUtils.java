package ch.ifocusit.livingdoc.plugin.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileUtils {

    public static String read(File file) throws MojoExecutionException {
        if (file == null || !file.exists()) {
            return StringUtils.EMPTY;
        }
        try {
            return IOUtils.toString(file.toURI(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read header file !", e);
        }
    }

}
