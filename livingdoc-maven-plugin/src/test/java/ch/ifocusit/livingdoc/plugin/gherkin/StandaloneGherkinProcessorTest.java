package ch.ifocusit.livingdoc.plugin.gherkin;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class StandaloneGherkinProcessorTest {

    @Test
    void transform() throws Exception {
        // given
        String fileContent = FileUtils.readFileToString(new File(Objects.requireNonNull(this.getClass().getResource("/simple.feature")).toURI()), Charset.defaultCharset());
        String expected = FileUtils.readFileToString(new File(Objects.requireNonNull(this.getClass().getResource("/simple.adoc")).toURI()), Charset.defaultCharset());
        // when
        String adoc = StandaloneGherkinProcessor.builder().build().process(fileContent);
        // then
        assertThat(adoc).isEqualToIgnoringWhitespace(expected);
    }

    @Test
    void transformComplex() throws Exception {
        // given
        String fileContent = FileUtils.readFileToString(new File(Objects.requireNonNull(this.getClass().getResource("/complex.feature")).toURI()), Charset.defaultCharset());
        String expected = FileUtils.readFileToString(new File(Objects.requireNonNull(this.getClass().getResource("/complex.adoc")).toURI()), Charset.defaultCharset());
        // when
        String adoc = StandaloneGherkinProcessor.builder().build().process(fileContent);
        // then
        assertThat(adoc).isEqualToIgnoringWhitespace(expected);
    }
}
