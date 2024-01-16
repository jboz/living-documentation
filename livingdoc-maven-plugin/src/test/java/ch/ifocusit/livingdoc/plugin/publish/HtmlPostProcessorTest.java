package ch.ifocusit.livingdoc.plugin.publish;

import static org.asciidoctor.SafeMode.UNSAFE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractAsciidoctorMojo;

@ExtendWith(MockitoExtension.class)
class HtmlPostProcessorTest {

    // @Test
    void usePlantumlMacroShouldReplaceContent() throws IOException, URISyntaxException {
        // given
        Asciidoctor asciidoctor = new AbstractAsciidoctorMojo() {
            @Override
            public void executeMojo() {
            }
        }.createAsciidoctor();

        Options options = Options.builder()
                .backend("html5")
                .safe(UNSAFE)
                .baseDir(new File("target"))
                .templateDirs(new File(Objects.requireNonNull(getClass().getResource("/templates")).toURI()))
                .attributes(Attributes.builder()
                        .attribute("imagesoutdir", new File("target").getAbsolutePath())
                        .attribute("outdir", new File("target").getAbsolutePath())
                        .build())
                .build();

        HtmlPostProcessor processor = new HtmlPostProcessor(asciidoctor, options);

        Map<String, String> attachmentCollector = new HashMap<>();
        Path diagram = Path.of(Objects.requireNonNull(getClass().getResource("/diagram.adoc")).toURI());

        String expected = "<ac:structured-macro ac:name=\"info\"><ac:rich-text-body>this include plantuml diagram will be convert to  confluence plantuml macro</ac:rich-text-body></ac:structured-macro>\n"
                +
                "<ac:structured-macro ac:name='plantuml' ac:schema-version='1' ac:macro-id='c93806da-006e-4cfa-9d67-bfe2f255e2fb'>\n"
                +
                "    <ac:parameter ac:name='atlassian-macro-output-type'>INLINE</ac:parameter>\n" +
                "    <ac:plain-text-body><![CDATA[!include ^diagram.plantuml]]></ac:plain-text-body>\n" +
                "</ac:structured-macro>\n" +
                "<ri:attachment ri:filename=\"diagram.plantuml\"></ri:attachment>";

        // when
        String processed = processor.process(diagram, attachmentCollector);

        // then
        assertThat(processed).isEqualTo(expected);
    }
}
