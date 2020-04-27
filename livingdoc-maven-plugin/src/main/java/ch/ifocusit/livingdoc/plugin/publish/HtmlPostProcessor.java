/*
 * Living Documentation
 *
 * Copyright (C) 2020 Focus IT
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ch.ifocusit.livingdoc.plugin.publish;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.internal.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.getTitle;
import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.isAdoc;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.DOTALL;

/**
 * @author Julien Boz
 */
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

    private Function<String, String> unescapeCdataHtmlContent() {
        return (content) -> replaceAll(content, CDATA_PATTERN, (matchResult) -> StringEscapeUtils.unescapeHtml4(matchResult.group()));
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
                return getTitle(asciidoctor, pageContent)
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
