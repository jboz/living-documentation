package ch.ifocusit.livingdoc.plugin.confluence.utils;

public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws Exception
     */
    R apply(T t) throws Exception;
}