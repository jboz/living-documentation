package ch.ifocusit.livingdoc.plugin.mapping;

import com.google.common.collect.Ordering;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MappingDefinition implements Comparable<MappingDefinition> {
    private Integer id;
    private String name;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return ORDERING.compare(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    private static final Ordering<MappingDefinition> ORDERING = Ordering.natural().nullsFirst()
            .onResultOf(MappingDefinition::getId)
            .compound(Ordering.natural().nullsFirst()
                    .onResultOf(MappingDefinition::getName))
            .compound(Ordering.natural().nullsFirst()
                    .onResultOf(MappingDefinition::getDescription))
            .nullsFirst();
}