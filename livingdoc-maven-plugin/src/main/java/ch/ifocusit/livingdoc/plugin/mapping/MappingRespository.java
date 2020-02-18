package ch.ifocusit.livingdoc.plugin.mapping;

import com.thoughtworks.qdox.model.JavaAnnotatedElement;

import java.util.Optional;

public interface MappingRespository {
    Optional<DomainObject> getMapping(JavaAnnotatedElement annotatedElement);
}
