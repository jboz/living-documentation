package ch.ifocusit.livingdoc.plugin.publish;

import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractAsciidoctorMojo.Format;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.ast.Title;
import org.asciidoctor.internal.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static java.util.regex.Pattern.DOTALL;

public class HtmlPostProcessor {

    private static final Pattern CDATA_PATTERN = Pattern.compile("<!\\[CDATA\\[.*?\\]\\]>", DOTALL);
    private static final Pattern ATTACHMENT_PATH_PATTERN = Pattern.compile("<ri:attachment ri:filename=\"(.*?)\"");
    private static final Pattern PAGE_TITLE_PATTERN = Pattern.compile("<ri:page ri:content-title=\"(.*?)\"");

    private final Asciidoctor asciidoctor;
    private final Options options;

    public HtmlPostProcessor(Asciidoctor asciidoctor, Options options) {
        this.asciidoctor = asciidoctor;
        this.options = options;
    }

    /**
     * @param path                : html file
     * @param attachmentCollector : map that will contains attachement
     * @return a clean html with attachement derived
     */
    public String process(Path path, Map<String, String> attachmentCollector) throws IOException {

        // read input
        String content = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());

        if (isAdoc(path)) {
            // convert adoc to html
            content = asciidoctor.convert(content, options);
        }

        return postProcessContent(content,
                replaceCrossReferenceTargets(path),
                collectAndReplaceAttachmentFileNames(attachmentCollector),
                unescapeCdataHtmlContent()
        );
    }

    private boolean isAdoc(Path path) {
        return FilenameUtils.isExtension(path.toString(), new String[]{Format.adoc.name(), Format.asciidoc.name()});
    }

    private Function<String, String> unescapeCdataHtmlContent() {
        return (content) -> replaceAll(content, CDATA_PATTERN, (matchResult) -> StringEscapeUtils.unescapeHtml(matchResult.group()));
    }

    private String deriveAttachmentName(String path) {
        return path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
    }

    private Function<String, String> collectAndReplaceAttachmentFileNames(Map<String, String> attachmentCollector) {
        return (content) -> replaceAll(content, ATTACHMENT_PATH_PATTERN, (matchResult) -> {
            String attachmentPath = matchResult.group(1);
            String attachmentFileName = deriveAttachmentName(attachmentPath);

            attachmentCollector.put(attachmentPath, attachmentFileName);

            return "<ri:attachment ri:filename=\"" + attachmentFileName + "\"";
        });
    }

    private String replaceAll(String content, Pattern pattern, Function<MatchResult, String> replacer) {
        StringBuffer replacedContent = new StringBuffer();
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            matcher.appendReplacement(replacedContent, replacer.apply(matcher.toMatchResult()));
        }

        matcher.appendTail(replacedContent);

        return replacedContent.toString();
    }

    private String postProcessContent(String initialContent, Function<String, String>... postProcessors) {
        return stream(postProcessors).reduce(initialContent,
                (accumulator, postProcessor) -> postProcessor.apply(accumulator), unusedCombiner());
    }

    public String getPageTitle(Path path) {
        String pageContent = null;
        try {
            pageContent = IOUtils.readFull(new FileInputStream(path.toFile()));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Unable to read page title !", e);
        }
        try {
            if (isAdoc(path)) {
                return Optional.ofNullable(asciidoctor.readDocumentHeader(pageContent).getDocumentTitle())
                        .map(Title::getMain)
                        .orElseThrow(() -> new IllegalStateException("top-level heading or title meta information must be set"));

            }
            // try to read h1 tag
            return tagText(pageContent, "h1");
        } catch (IllegalStateException e) {
            return FilenameUtils.removeExtension(path.getFileName().toString());
        }
    }

    private String tagText(String content, String tag) {
        String tagBegin = "<" + tag + ">";
        String tagEnd = "</" + tag + ">";
        try {
            String substring = content.substring(0, content.indexOf(tagEnd));
            return substring.substring(content.indexOf(tagBegin) + tagBegin.length());
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalStateException("Unable to retrieve text of tag '" + tag + "' in content '" + content + "'", e);
        }
    }

    private File parentFolder(Path pagePath) {
        return pagePath.getParent().toFile();
    }

    private Function<String, String> replaceCrossReferenceTargets(Path pagePath) {
        return (content) -> replaceAll(content, PAGE_TITLE_PATTERN, (matchResult) -> {
            String htmlTarget = matchResult.group(1);
            String referencedPageTitle = htmlTarget;
            if (htmlTarget.indexOf(".") > -1) {
                //#glossaryid-StatutAnnonce_ACCEPTE
                Path referencedPagePath = pagePath.getParent().resolve(
                        Paths.get(htmlTarget.substring(0, htmlTarget.lastIndexOf('.')) + ".adoc"));
                referencedPageTitle = getPageTitle(referencedPagePath);
            }

            return "<ri:page ri:content-title=\"" + referencedPageTitle + "\"";
        });
    }

    private BinaryOperator<String> unusedCombiner() {
        return (a, b) -> a;
    }

}
