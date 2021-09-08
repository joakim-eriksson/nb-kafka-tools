package se.jocke.nb.kafka.nodes.topics;

import java.util.Map;
import java.util.Optional;
import org.apache.kafka.common.config.TopicConfig;

/**
 *
 * @author jocke
 */
public class KafkaCreateTopic {

    private final String name;
    private final Optional<Integer> numPartitions;
    private final Optional<Short> replicationFactor;
    private final Map<String, String> configs;

    public enum CreateProperties {
        CLEANUP_POLICY(TopicConfig.CLEANUP_POLICY_DOC, String.class),
        COMPRESSION_TYPE(TopicConfig.COMPRESSION_TYPE_DOC, String.class),
        DELETE_RETENTION_MS(TopicConfig.DELETE_RETENTION_MS_DOC, Long.class),
        FILE_DELETE_DELAY_MS(TopicConfig.FILE_DELETE_DELAY_MS_DOC, Long.class),
        FLUSH_MESSAGES(TopicConfig.FLUSH_MESSAGES_INTERVAL_DOC, Long.class),
        FLUSH_MS(TopicConfig.FLUSH_MS_DOC, Long.class),
        INDEX_INTERVAL_BYTES(TopicConfig.INDEX_INTERVAL_BYTES_DOCS, Integer.class),
        MAX_COMPACTION_LAG_MS(TopicConfig.MAX_COMPACTION_LAG_MS_DOC, Long.class),
        MAX_MESSAGE_BYTES(TopicConfig.MAX_MESSAGE_BYTES_DOC, Integer.class),
        MESSAGE_FORMAT_VERSION(TopicConfig.MESSAGE_FORMAT_VERSION_DOC, String.class),
        MESSAGE_TIMESTAMP_DIFFERENCE_MAX_MS(TopicConfig.MESSAGE_TIMESTAMP_DIFFERENCE_MAX_MS_DOC, Long.class),
        MESSAGE_TIMESTAMP_TYPE(TopicConfig.MESSAGE_TIMESTAMP_TYPE_DOC, String.class),
        MIN_CLEANABLE_DIRTY_RATIO(TopicConfig.MIN_CLEANABLE_DIRTY_RATIO_DOC, Double.class),
        MIN_COMPACTION_LAG_MS(TopicConfig.MIN_COMPACTION_LAG_MS_DOC, Long.class),
        MIN_INSYNC_REPLICAS(TopicConfig.MIN_IN_SYNC_REPLICAS_DOC, Integer.class),
        PREALLOCATE(TopicConfig.PREALLOCATE_DOC, Boolean.class),
        RETENTION_BYTES(TopicConfig.RETENTION_BYTES_DOC, Long.class),
        RETENTION_MS(TopicConfig.RETENTION_MS_DOC, Long.class),
        SEGMENT_BYTES(TopicConfig.SEGMENT_BYTES_DOC, Integer.class),
        SEGMENT_INDEX_BYTES(TopicConfig.SEGMENT_INDEX_BYTES_DOC, Integer.class),
        SEGMENT_JITTER_MS(TopicConfig.SEGMENT_JITTER_MS_DOC, Long.class),
        SEGMENT_MS(TopicConfig.SEGMENT_MS_DOC, Long.class),
        UNCLEAN_LEADER_ELECTION_ENABLE(TopicConfig.UNCLEAN_LEADER_ELECTION_ENABLE_DOC, Boolean.class),
        MESSAGE_DOWNCONVERSION_ENABLE(TopicConfig.MESSAGE_DOWNCONVERSION_ENABLE_DOC, Boolean.class);

        private final String description;

        private final Class<?> type;

        private CreateProperties(String description, Class<?> type) {
            this.description = description;
            this.type = type;
        }

        public String getKey() {
            return name().toLowerCase().replaceAll("_", ".");
        }

        public String getDescription() {
            return description;
        }

        public Class<?> getType() {
            return type;
        }

        public Object getValue(Map<String, Object> props) {
            return props.get(getKey());
        }

        public void setValue(Map<String, Object> props, Object value) {
            props.put(getKey(), value);
        }
    }

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

        private Optional<Integer> numPartitions = Optional.empty();

        private Optional<Short> replicationFactor = Optional.empty();

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

        public KafkaCreateTopicBuilder replicationFactor(Optional<Short> replicationFactor) {
            this.replicationFactor = replicationFactor;
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
