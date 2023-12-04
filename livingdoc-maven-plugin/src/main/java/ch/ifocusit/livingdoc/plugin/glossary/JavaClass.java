package ch.ifocusit.livingdoc.plugin.glossary;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

import ch.ifocusit.livingdoc.plugin.mapping.DomainObject;
import ch.ifocusit.livingdoc.plugin.mapping.MappingRespository;

public class JavaClass implements JavaElement, Comparable<JavaClass> {

    private com.thoughtworks.qdox.model.JavaClass model;
    private Optional<DomainObject> mapping = Optional.empty();

    private List<JavaField> fields;

    @Override
    public com.thoughtworks.qdox.model.JavaClass getModel() {
        return model;
    }

    @Override
    public String getName() {
        return mapping.map(DomainObject::getName).orElse(model.getName());
    }

    @Override
    public String getDescription() {
        return mapping.map(DomainObject::getDescription).orElse(JavaElement.super.getDescription());
    }

    @Override
    public String getType() {
        return model.isEnum() ? "Enumeration" : EMPTY;
    }

    public List<JavaField> getFields() {
        return fields;
    }

    public static JavaClass from(com.thoughtworks.qdox.model.JavaClass javaClass,
                                 Predicate<com.thoughtworks.qdox.model.JavaField> fieldPredicate,
                                 List<com.thoughtworks.qdox.model.JavaClass> domainClasses,
                                 MappingRespository mappingRespository) {
        JavaClass clazz = new JavaClass();
        clazz.model = javaClass;
        clazz.mapping = mappingRespository.getMapping(javaClass);
        clazz.fields = javaClass.getFields().stream()
                .filter(javaField -> !javaField.isStatic()) // exclude static
                .filter(fieldPredicate)
                .map(javaField -> JavaField.of(javaField, domainClasses, mappingRespository))
                .collect(Collectors.toList());
        return clazz;
    }

    public boolean hasId() {
        return getGlossary().isPresent() || fields.stream().anyMatch(javaField -> javaField.getGlossary().isPresent());
    }

    @Override
    public int compareTo(JavaClass o) {
        if (hasId() && !o.hasId()) {
            return 1;
        }
        if (!hasId() && o.hasId()) {
            return -1;
        }
        if (!hasId() && !o.hasId()) {
            return getName().compareTo(o.getName());
        }
        return ObjectUtils.compare(getId(), o.getId(), false);
    }
}
