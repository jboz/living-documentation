package ch.ifocusit.livingdoc.plugin.glossary;

import ch.ifocusit.livingdoc.plugin.mapping.DomainObject;
import ch.ifocusit.livingdoc.plugin.mapping.MappingRespository;
import ch.ifocusit.livingdoc.plugin.utils.AnchorUtil;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class JavaField implements JavaElement {

    private com.thoughtworks.qdox.model.JavaField model;
    private Optional<DomainObject> mapping = Optional.empty();
    private boolean partOfDomain = false;

    @Override
    public String getName() {
        return mapping.map(DomainObject::getName).orElse(model.getName());
    }

    @Override
    public String getDescription() {
        return mapping.map(DomainObject::getDescription).orElse(JavaElement.super.getDescription());
    }

    public String getFullName() {
        return AnchorUtil.glossaryLink(model.getDeclaringClass().getName(), getName());
    }

    public String getLinkableFullName() {
        return getAnchor() + getFullName();
    }

    @Override
    public String getType() {
        if (model.isEnumConstant()) {
            return EMPTY;
        }
        String typeName = model.getType().getName();
        if (model.getType().isEnum()) {
            typeName += ", Enumeration";
        }
        return typeName;
    }

    @Override
    public JavaAnnotatedElement getModel() {
        return model;
    }

    public boolean isPartOfDomain() {
        return partOfDomain;
    }

    public String getLinkedType() {
        if (model.isEnumConstant()) {
            return EMPTY;
        }
        String name = model.getType().getName();
        if (isPartOfDomain()) {
            // type class is in the same domain, create a relative link
            name = "<<" + AnchorUtil.formatLink(getGlossaryId(model.getType()).orElse(null), model.getType().getName())
                    + "," + model.getType().getName() + ">>";
        }
        if (model.getType().isEnum()) {
            name += ", Enumeration";
        }
        return name;
    }

    @Override
    public String getAnchor() {
        return "anchor:" + AnchorUtil.formatLink(getGlossaryId().orElse(null), getFullName()) + "[]";
    }

    public static JavaField of(com.thoughtworks.qdox.model.JavaField javaField, List<JavaClass> domainClasses,
                               MappingRespository mappingRespository) {
        JavaField field = new JavaField();
        field.model = javaField;
        field.mapping = mappingRespository.getMapping(javaField);
        field.partOfDomain = domainClasses.stream().anyMatch(javaField.getType()::equals);
        return field;
    }
}
