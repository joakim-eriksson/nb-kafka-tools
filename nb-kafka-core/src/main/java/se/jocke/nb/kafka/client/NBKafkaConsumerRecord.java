package se.jocke.nb.kafka.client;

import java.util.Objects;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class NBKafkaConsumerRecord {

    private final String key;

    private final String value;

    private final int partition;

    private final long timestamp;

    private final long offset;

    private final int hash;

    public NBKafkaConsumerRecord(String key, String value, int partition, long timestamp, long offset) {
        this.key = key;
        this.value = value;
        this.partition = partition;
        this.timestamp = timestamp;
        this.offset = offset;
        this.hash = computeHash();
    }

    public String getKey() {
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

    @Override
    public int hashCode() {
        return hash;
    }

    private int computeHash() {
        int hashValue = 7;
        hashValue = 17 * hashValue + Objects.hashCode(this.key);
        hashValue = 17 * hashValue + Objects.hashCode(this.value);
        hashValue = 17 * hashValue + this.partition;
        hashValue = 17 * hashValue + (int) (this.timestamp ^ (this.timestamp >>> 32));
        hashValue = 17 * hashValue + (int) (this.offset ^ (this.offset >>> 32));
        return hashValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NBKafkaConsumerRecord other = (NBKafkaConsumerRecord) obj;
        if (this.partition != other.partition) {
            return false;
        }
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if (this.offset != other.offset) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NBKafkaConsumerRecord{" + "key=" + key + ", value=" + value + ", partition=" + partition + ", timestamp=" + timestamp + ", offset=" + offset + ", hash=" + hash + '}';
    }
}
