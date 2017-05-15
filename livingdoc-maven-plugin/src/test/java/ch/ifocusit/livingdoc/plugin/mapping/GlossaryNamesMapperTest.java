package ch.ifocusit.livingdoc.plugin.mapping;

import ch.ifocusit.livingdoc.annotations.Glossary;
import org.junit.Test;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class GlossaryNamesMapperTest {

    @Test
    public void testReadFile() throws Exception {
        GlossaryNamesMapper mapper = new GlossaryNamesMapper(new File(getClass().getResource("/mappings.csv").toURI()), Glossary.class, "id-{0}");
        assertThat(mapper.mappings).hasSize(1);
    }
}