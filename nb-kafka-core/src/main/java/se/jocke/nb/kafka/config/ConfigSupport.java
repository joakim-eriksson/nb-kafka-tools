package se.jocke.nb.kafka.config;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public interface ConfigSupport<T> {

    T valueFromString(String value);

    default String valueToString(T value) {
        return value.toString();
    }

    Class<T> getSupportType();

    public static class BaseConfigSupport<T> implements ConfigSupport<T> {

        private final Class<T> clazz;

        private final Function<String, T> converter;

        public BaseConfigSupport(Class<T> clazz, Function<String, T> converter) {
            this.clazz = clazz;
            this.converter = converter;
        }

        @Override
        public T valueFromString(String value) {
            return converter.apply(value);
        }

        @Override
        public Class<T> getSupportType() {
            return clazz;
        }

    }

    public static final ConfigSupport<Boolean> BOOLEAN = new BaseConfigSupport<>(Boolean.class, Boolean::parseBoolean);

    public static final ConfigSupport<String> STRING = new BaseConfigSupport<>(String.class, String::new);

    public static final ConfigSupport<Long> LONG = new BaseConfigSupport<>(Long.class, Long::parseLong);

    public static final ConfigSupport<Set<String>> SET = new ConfigSupport<>() {

        @Override
        public Set<String> valueFromString(String value) {
            return Sets.newLinkedHashSet(Splitter.on(",").omitEmptyStrings().split(value));
        }

        @Override
        public String valueToString(Set<String> value) {
            return Joiner.on(",").join(value);
        }

        @Override
        public Class<Set<String>> getSupportType() {
            Set<String> set = new HashSet<>();
            return (Class<Set<String>>) set.getClass();
        }
    };
}
