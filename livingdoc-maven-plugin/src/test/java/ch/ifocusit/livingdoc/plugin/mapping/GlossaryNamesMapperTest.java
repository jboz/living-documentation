package ch.ifocusit.livingdoc.plugin.mapping;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;


public class GlossaryNamesMapperTest {

    @Test
    void testReadFile() throws Exception {
        GlossaryNamesMapper mapper = new GlossaryNamesMapper<>(
                new File(getClass().getResource("/mappings.csv").toURI()), UbiquitousLanguage.class);
        assertThat(mapper.mappings).hasSize(1);
    }
}