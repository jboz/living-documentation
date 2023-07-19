package ch.ifocusit.livingdoc.plugin.gherkin;

import com.github.domgold.doctools.asciidoctor.gherkin.MapFormatter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class GherkinToAsciidocTransformer {
    public static final String NEWLINE = System.getProperty("line.separator");

    private final StringBuilder builder = new StringBuilder();

    public String transform(String fileContent) {
        return format(MapFormatter.parse(fileContent));
    }

    private String format(Map<String, Object> parsed) {
        builder.append("=== ").append(parsed.get("name")).append(NEWLINE).append(NEWLINE);
        manageDescription(parsed);
        if (parsed.containsKey("background")) {
            Map<String, Object> background = (Map<String, Object>) parsed.get("background");
            manageScenario(background);
        }
        if (parsed.containsKey("scenarios")) {
            List<Map<String, Object>> scenarios = (List<Map<String, Object>>) parsed.get("scenarios");
            for (Map<String, Object> scenario : scenarios) {
                manageScenario(scenario);
            }
        }
        return builder.toString();
    }

    private void manageDescription(Map<String, Object> container) {
        String description = (String) container.get("description");
        if (StringUtils.isNotBlank(description)) {
            builder.append(description).append(NEWLINE);
        }
        builder.append(NEWLINE);
    }

    private void manageScenario(Map<String, Object> scenario) {
        builder.append("==== ").append(scenario.get("name")).append(NEWLINE);
        manageDescription(scenario);
        manageSteps(scenario);
        manageTable(scenario);
    }

    private void manageSteps(Map<String, Object> container) {
        if (container.containsKey("steps")) {
            List<Map<String, Object>> steps = (List<Map<String, Object>>) container.get("steps");
            for (Map<String, Object> step : steps) {
                String keyword = (String) step.get("keyword");
                builder.append("* *").append(keyword.trim()).append("* ").append(step.get("name")).append(NEWLINE);
                if (step.containsKey("doc_string")) {
                    Map<String, Object> doc_string = (Map<String, Object>) step.get("doc_string");
                    builder.append("+").append(NEWLINE).append("....").append(NEWLINE).append(doc_string.get("value")).append("....").append(NEWLINE);
                }
                builder.append(NEWLINE);
                manageTable(step);
            }
        }
    }

    private void manageTable(Map<String, Object> container) {
        if (container.containsKey("examples")) {
            Map<String, Object> examples = (Map<String, Object>) container.get("examples");
            builder.append("===== ").append(examples.get("keyword")).append(NEWLINE);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) examples.get("rows");
            builder.append("|====").append(NEWLINE);
            for (Map<String, Object> row : rows) {
                List<Object> cells = (List<Object>) row.get("cells");
                for (Object cell : cells) {
                    builder.append("| ").append(cell);
                }
                builder.append(NEWLINE);
            }
            builder.append("|====").append(NEWLINE).append(NEWLINE);
        }
    }
}
