package ch.ifocusit.livingdoc.plugin.mapping;

import com.google.common.collect.Ordering;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MappingDefinition implements Comparable<MappingDefinition> {
    private Integer id;
    private String name;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Check the name
     */
    public MappingDefinition checkName() {
        name = name.trim().replace(" ", "_");
        return this;
    }

    @Override
    public int compareTo(MappingDefinition o) {
        return BY_ID.compound(BY_NAME).compound(BY_DESCRIPTION).nullsFirst().compare(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    private static final Ordering<MappingDefinition> BY_ID = Ordering.natural().onResultOf(MappingDefinition::getId);
    private static final Ordering<MappingDefinition> BY_NAME = Ordering.natural().nullsFirst().onResultOf(MappingDefinition::getName);
    private static final Ordering<MappingDefinition> BY_DESCRIPTION = Ordering.natural().nullsFirst().onResultOf(MappingDefinition::getDescription);
}