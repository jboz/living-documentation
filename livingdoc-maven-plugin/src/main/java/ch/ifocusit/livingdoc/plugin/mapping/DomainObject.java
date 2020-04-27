package ch.ifocusit.livingdoc.plugin.mapping;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.NEWLINE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class DomainObject implements Comparable<DomainObject> {
    private Integer id;
    private String namespace;
    private String parentName;
    private String name;
    private String description;
    private JavaClass type;
    private List<String> annotations = new ArrayList<>();
    /**
     * Indicate that this definition is mapped with a domain translation file
     */
    private boolean mapped = false;

    public DomainObject() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnotations() {
        return annotations.stream().collect(Collectors.joining(NEWLINE + NEWLINE));
    }

    public String getFullDescription() {
        return getAnnotations() + (annotations.isEmpty() ? "" : NEWLINE + NEWLINE) + getDescription();
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return Optional.ofNullable(description).orElse("");
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setMapped(boolean mapped) {
        this.mapped = mapped;
    }

    public void addAnnotation(String annotation) {
        this.annotations.add(annotation);
    }

    public void setType(final JavaClass type) {
        this.type = type;
    }

    public JavaClass getType() {
        return type;
    }

    /**
     * The full name of a domain object is it's qualified name like Class.field.
     * If the domain object is mapped, i.e. it has been translated by a business glossary file, then the fullName is only the translated name.
     * Parent class name is not showed because the field should already be fully qualified by it's translation.
     *
     * @return the full name as it will be represented in the glossary.
     */
    public String getFullName() {
        return (!mapped && StringUtils.isNoneBlank(parentName) ? parentName + "." : EMPTY) + name;
    }

    @Override
    public int compareTo(DomainObject o) {
        return new Ordering<DomainObject>() {
            @Override
            public int compare(DomainObject left, DomainObject right) {
                return ComparisonChain.start()
                        .compare(left.id, right.id, Ordering.natural().nullsFirst())
                        .compare(left.name, right.name, Ordering.natural().nullsFirst())
                        .compare(left.description, right.description, Ordering.natural().nullsFirst())
                        .result();
            }
        }.nullsFirst()
                .compare(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}