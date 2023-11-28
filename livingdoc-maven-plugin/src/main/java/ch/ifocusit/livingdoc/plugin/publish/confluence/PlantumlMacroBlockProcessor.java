package ch.ifocusit.livingdoc.plugin.publish.confluence;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;

import java.util.Map;

public class PlantumlMacroBlockProcessor extends BlockMacroProcessor {

    public PlantumlMacroBlockProcessor(String macroName, Map<String, Object> config) {
        super(macroName, config);
    }

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
        String content = "<ac:structured-macro ac:name='plantuml' ac:schema-version='1' ac:macro-id='c93806da-006e-4cfa-9d67-bfe2f255e2fb'>\n" +
                "    <ac:parameter ac:name='atlassian-macro-output-type'>INLINE</ac:parameter>\n" +
                "    <ac:plain-text-body><![CDATA[!include ^" + target + "]]></ac:plain-text-body>\n" +
                "</ac:structured-macro>\n" +
                "<ri:attachment ri:filename=\"" + target + "\"></ri:attachment>";

        return createBlock(parent, "pass", content);
    }
}
