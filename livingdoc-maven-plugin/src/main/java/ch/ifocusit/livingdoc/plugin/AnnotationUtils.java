package ch.ifocusit.livingdoc.plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public class AnnotationUtils {

    public static <T extends Annotation> Optional<T> tryFind(Field field, Class<T> annotationClass) {
        return field.isAnnotationPresent(annotationClass) ?
                Optional.of(field.getAnnotation(annotationClass)) : Optional.empty();
    }

    public static <T extends Annotation> Optional<T> tryFind(Class clazz, Class<T> annotationClass) {
        return clazz.isAnnotationPresent(annotationClass) ?
                Optional.of(annotationClass.cast(clazz.getAnnotation(annotationClass))) : Optional.empty();
    }
}
