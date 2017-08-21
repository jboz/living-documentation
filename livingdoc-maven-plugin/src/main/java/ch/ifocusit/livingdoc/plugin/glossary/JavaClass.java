package ch.ifocusit.livingdoc.plugin.glossary;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public static JavaClass from(com.thoughtworks.qdox.model.JavaClass javaClass, Predicate<com.thoughtworks.qdox.model.JavaField> fieldPredicate) {
        JavaClass clazz = new JavaClass();
        clazz.model = javaClass;
        clazz.fields = javaClass.getFields().stream().filter(fieldPredicate).map(javaField -> JavaField.of(javaField)).collect(Collectors.toList());
        return clazz;
    }
}
