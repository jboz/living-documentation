package ch.ifocusit.livingdoc.plugin.mapping;

import com.google.common.collect.ComparisonChain;
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
        return new Ordering<MappingDefinition>() {
            @Override
            public int compare(MappingDefinition left, MappingDefinition right) {
                return ComparisonChain.start()
                        .compare(left.getId(), right.getId(), Ordering.natural().nullsFirst())
                        .compare(left.getName(), right.getName(), Ordering.natural().nullsFirst())
                        .compare(left.getDescription(), right.getDescription(), Ordering.natural().nullsFirst())
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