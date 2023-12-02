package ch.ifocusit.livingdoc.plugin.gherkin;

import com.github.domgold.doctools.asciidoctor.gherkin.MapFormatter;
import lombok.Builder;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.jruby.embed.ScriptingContainer;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Objects;

@Builder
public class StandaloneGherkinProcessor {

    private File gherkinTemplate;

    @SneakyThrows
    public String process(String fileContent) {
        ScriptingContainer container = new ScriptingContainer();
        container.put("feature_file_content", fileContent);
        container.put("template_content", gherkinTemplate != null ? IOUtils.toString(gherkinTemplate.toURI(), Charset.defaultCharset()) : MapFormatter.getDefaultTemplate());
        String scriptPath = "/standaloneGherkinProcessor.rb";
        String script = IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream(scriptPath)), Charset.defaultCharset());
        return (String) container.runScriptlet(script);
    }
}
