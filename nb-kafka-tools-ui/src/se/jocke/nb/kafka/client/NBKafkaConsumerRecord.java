package se.jocke.nb.kafka.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class NBKafkaConsumerRecord {

    private final Object key;

    private final String value;

    private final int partition;

    private final long timestamp;

    private final long offset;

    public NBKafkaConsumerRecord(Object key, String value, int partition, long timestamp, long offset) {
        this.key = key;
        this.value = value;
        this.partition = partition;
        this.timestamp = timestamp;
        this.offset = offset;
    }

    public Object getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getPartition() {
        return partition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getOffset() {
        return offset;
    }

    public static NBKafkaConsumerRecord of(ConsumerRecord<String, String> consumerRecord) {
        return new NBKafkaConsumerRecord(consumerRecord.key(), consumerRecord.value(), consumerRecord.partition(), consumerRecord.timestamp(), consumerRecord.offset());
    }

}
