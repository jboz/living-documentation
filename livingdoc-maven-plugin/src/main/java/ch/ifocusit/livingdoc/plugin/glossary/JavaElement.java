package ch.ifocusit.livingdoc.plugin.glossary;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.plugin.utils.AnchorUtil;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;

import java.util.Optional;
import java.util.stream.Collectors;

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.NEWLINE;
import static io.github.robwin.markup.builder.asciidoc.AsciiDoc.TABLE_COLUMN_DELIMITER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public interface JavaElement {
    String JAVAX_VALIDATION_CONSTRAINTS = "javax.validation.constraints.";
    String HIBERNATE_VALIDATION_CONSTRAINTS = "org.hibernate.validator.constraints.";

    String getName();

    String getType();

    JavaAnnotatedElement getModel();

    default String getDescription() {
        return getModel().getComment();
    }

    default String getAnchor() {
        return "anchor:" + getLinkContent() + "[]";
    }

    default String getLinkContent() {
        return AnchorUtil.formatLink(getGlossaryId().orElse(null), getName());
    }

    default String getLinkableName() {
        return getAnchor() + getName();
    }

    default String getAnnotations() {
        return getModel().getAnnotations().stream()
                .filter(annot -> annot.getType().getFullyQualifiedName().startsWith(JAVAX_VALIDATION_CONSTRAINTS)
                        || annot.getType().getFullyQualifiedName().startsWith(HIBERNATE_VALIDATION_CONSTRAINTS))
                .map(annot -> annot.toString().replace(JAVAX_VALIDATION_CONSTRAINTS, "")
                        .replace(HIBERNATE_VALIDATION_CONSTRAINTS, ""))
                .collect(Collectors.joining(NEWLINE + NEWLINE));
    }

    default Optional<JavaAnnotation> getGlossary(JavaAnnotatedElement annotatedElement) {
        return annotatedElement.getAnnotations().stream()
                .filter(a -> a.getType().getFullyQualifiedName().endsWith(UbiquitousLanguage.class.getSimpleName()))
                .findFirst();
    }

    default Optional<Integer> getGlossaryId(JavaAnnotatedElement annotatedElement) {
        return getGlossary(annotatedElement).map(annot -> annot.getProperty("id") == null ? null :
                Optional.ofNullable(Integer.valueOf(String.valueOf(annot.getNamedParameter("id")))))
                .orElse(Optional.empty());
    }

    default Optional<JavaAnnotation> getGlossary() {
        return getGlossary(getModel());
    }

    default Optional<Integer> getGlossaryId() {
        return getGlossaryId(getModel());
    }

    default boolean getIdDefined() {
        return getGlossaryId().isPresent();
    }

    default Integer getId() {
        return getGlossaryId().orElse(null);
    }

    default String getLinkableIdColumn() {
        return getGlossaryId().map(id -> TABLE_COLUMN_DELIMITER + getAnchor() + String.valueOf(id)).orElse(EMPTY);
    }
}
