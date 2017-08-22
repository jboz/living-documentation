package ch.ifocusit.livingdoc.plugin.glossary;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class JavaClass implements JavaElement {

    private com.thoughtworks.qdox.model.JavaClass model;

    private List<JavaField> fields;

    @Override
    public com.thoughtworks.qdox.model.JavaClass getModel() {
        return model;
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public String getType() {
        return model.isEnum() ? "Enumeration" : EMPTY;
    }

    public List<JavaField> getFields() {
        return fields;
    }

    public static JavaClass from(com.thoughtworks.qdox.model.JavaClass javaClass, Predicate<com.thoughtworks.qdox.model.JavaField> fieldPredicate, Stream<com.thoughtworks.qdox.model.JavaClass> domainClasses) {
        JavaClass clazz = new JavaClass();
        clazz.model = javaClass;
        clazz.fields = javaClass.getFields().stream()
                .filter(javaField -> !javaField.isStatic()) // exclude static
                .filter(fieldPredicate).map(javaField -> JavaField.of(javaField, domainClasses))
                .collect(Collectors.toList());
        return clazz;
    }

    public boolean hasId() {
        return getGlossary().isPresent() || fields.stream().anyMatch(javaField -> javaField.getGlossary().isPresent());
    }
}
