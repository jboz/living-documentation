package ch.ifocusit.livingdoc.plugin.glossary;

import com.thoughtworks.qdox.model.JavaAnnotatedElement;

import java.util.stream.Collectors;

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.NEWLINE;

public interface JavaElement {
    String JAVAX_VALIDATION_CONSTRAINTS = "javax.validation.constraints.";
    String HIBERNATE_VALIDATION_CONSTRAINTS = "org.hibernate.validator.constraints.";

    String getName();

    String getType();

    JavaAnnotatedElement getModel();

    default String getAnchor() {
        return "anchor:glossaryid-" + getName() + "[]";
    }

    default String getLinkableName() {
        return getAnchor() + getName();
    }

    default String getAnnotations() {
        return getModel().getAnnotations().stream()
                .filter(annot -> annot.getType().getFullyQualifiedName().startsWith(JAVAX_VALIDATION_CONSTRAINTS)
                        || annot.getType().getFullyQualifiedName().startsWith(HIBERNATE_VALIDATION_CONSTRAINTS))
                .map(annot -> annot.toString().replace(JAVAX_VALIDATION_CONSTRAINTS, "").replace(HIBERNATE_VALIDATION_CONSTRAINTS, ""))
                .collect(Collectors.joining(NEWLINE + NEWLINE));
    }
}
