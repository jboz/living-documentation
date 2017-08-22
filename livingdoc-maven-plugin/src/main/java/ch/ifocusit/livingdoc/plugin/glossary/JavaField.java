package ch.ifocusit.livingdoc.plugin.glossary;

import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.List;

import static ch.ifocusit.livingdoc.plugin.baseMojo.AbstractDocsGeneratorMojo.GLOSSARY_ANCHOR;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class JavaField implements JavaElement {

    private com.thoughtworks.qdox.model.JavaField model;
    private boolean partOfDomain = false;

    @Override
    public String getName() {
        return model.getName();
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
            name = "<<" + format(GLOSSARY_ANCHOR, model.getType().getName()) + "," + model.getType().getName() + ">>";
        }
        if (model.getType().isEnum()) {
            name += ", Enumeration";
        }
        return name;
    }

    public static JavaField of(com.thoughtworks.qdox.model.JavaField javaField, List<JavaClass> domainClasses) {
        JavaField field = new JavaField();
        field.model = javaField;
        field.partOfDomain = domainClasses.stream().anyMatch(javaField.getType()::equals);
        return field;
    }
}
