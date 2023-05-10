package ch.ifocusit.livingdoc.plugin;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratedResourcesTest {

    @Test
    void assertContentIsEquals() throws IOException {
        try (Stream<Path> expects = Files.list(Paths.get("src/test/resources/expected"))) {
            expects.forEach(path -> {
                try {
                    verify(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        assert new File("target/generated-docs/diagram.png").exists();
        assert new File("target/generated-docs/diagram.html").exists();
    }

    private void verify(Path path) throws IOException {
        File expected = path.toFile();
        File actual = new File("target/generated-docs", expected.getName());
        assertThat(actual).exists();
        assertThat(FileUtils.contentEqualsIgnoreEOL(actual, expected, StandardCharsets.UTF_8.toString()))
                .describedAs("Content check of '%s' failed", expected.getName())
                .isTrue();
    }
}
