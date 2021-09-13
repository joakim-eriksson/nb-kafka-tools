package se.jocke.nb.kafka;

/**
 *
 * @author jocke
 */
public final class NBKafkaConfigEntry {

    private final String name;
    private final String value;
    private final String documentation;

    public NBKafkaConfigEntry(String name, String value, String documentation) {
        this.name = name;
        this.value = value;
        this.documentation = documentation;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDocumentation() {
        return documentation;
    }

}
