package se.jocke.nb.kafka.model;

/**
 *
 * @author jocke
 */
public class KafkaTopic {

    private final String name;

    public KafkaTopic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "KafkaTopic{" + "name=" + name + '}';
    }
}
