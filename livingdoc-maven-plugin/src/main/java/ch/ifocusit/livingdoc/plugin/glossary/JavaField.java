package ch.ifocusit.livingdoc.plugin.glossary;

import com.thoughtworks.qdox.model.JavaAnnotatedElement;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class JavaField implements JavaElement {

    private com.thoughtworks.qdox.model.JavaField model;

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public String getType() {
        return model.getType().getName();
    }

    @Override
    public JavaAnnotatedElement getModel() {
        return model;
    }

    public String getLinkedType() {
        if (model.isEnumConstant()) {
            return EMPTY;
        }
        String name = model.getType().getName();
//        TODO reactive : if (getClasses().anyMatch(model.getType()::equals)) {
            // type class is in the same domain, create a relative link
            name = "<<glossaryid-" + model.getType().getName() + "," + model.getType().getName() + ">>";
//        }
        if (model.getType().isEnum()) {
            name += ", Enumeration";
        }
        return name;
    }

    public static JavaField of(com.thoughtworks.qdox.model.JavaField javaField) {
        JavaField field = new JavaField();
        field.model = javaField;
        return field;
    }
}
