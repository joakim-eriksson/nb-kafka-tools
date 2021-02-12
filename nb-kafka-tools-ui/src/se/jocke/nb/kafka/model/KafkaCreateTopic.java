package se.jocke.nb.kafka.model;

import java.util.Map;
import java.util.Optional;

/**
 *
 * @author jocke
 */
public class KafkaCreateTopic {

    private final String name;
    private final Optional<Integer> numPartitions;
    private final Optional<Short> replicationFactor;
    private final Map<String, String> configs;

    private KafkaCreateTopic(String name, Optional<Integer> numPartitions, Optional<Short> replicationFactor, Map<String, String> configs) {
        this.name = name;
        this.numPartitions = numPartitions;
        this.replicationFactor = replicationFactor;
        this.configs = configs;
    }

    public String getName() {
        return name;
    }

    public Optional<Integer> getNumPartitions() {
        return numPartitions;
    }

    public Optional<Short> getReplicationFactor() {
        return replicationFactor;
    }

    public Map<String, String> getConfigs() {
        return configs;
    }

    public static class KafkaCreateTopicBuilder {

        private String name;
        private Optional<Integer> numPartitions;
        private Optional<Short> replicationFactor;
        private Map<String, String> configs;

        public KafkaCreateTopicBuilder() {
        }

        public KafkaCreateTopicBuilder name(String name) {
            this.name = name;
            return this;
        }

        public KafkaCreateTopicBuilder numPartitions(Optional<Integer> numPartitions) {
            this.numPartitions = numPartitions;
            return this;
        }

        public KafkaCreateTopicBuilder configs(Map<String, String> configs) {
            this.configs = configs;
            return this;
        }

        public KafkaCreateTopic build() {
            return new KafkaCreateTopic(name, numPartitions, replicationFactor, configs);
        }
    }
}
