package se.jocke.nb.kafka.options.form;

/**
 *
 * @author jocke
 */
public interface InputConverter<T> {

    public static final InputConverter<String> REQUIRED_STRING_CONVERTER = (String value) -> {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Required value");
        }
        return value;
    };
    
    public static final InputConverter<String> STRING_CONVERTER = (String value) -> {
        if (value == null) {
            return "";
        }
        return value;
    };

    T fromString(String value);

}
