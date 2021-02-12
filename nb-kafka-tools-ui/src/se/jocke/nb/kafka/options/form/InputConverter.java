package se.jocke.nb.kafka.options.form;

/**
 *
 * @author jocke
 */
public interface InputConverter<T> {

    public static final InputConverter<String> STRING_CONVERTER = new InputConverter<String>() {

        @Override
        public String toString(String value) throws IllegalArgumentException {
            return value;
        }

        @Override
        public String fromString(String value) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Required value");
            }
            return value;
        }
    };

    String toString(T value) throws IllegalArgumentException;

    T fromString(String value);

}
