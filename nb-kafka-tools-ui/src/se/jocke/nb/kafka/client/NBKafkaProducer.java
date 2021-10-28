package se.jocke.nb.kafka.client;

import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import se.jocke.nb.kafka.nodes.root.KafkaServiceKey;

/**
 *
 * @author jocke
 */
public interface NBKafkaProducer  {

    Future<RecordMetadata> send(KafkaServiceKey kafkaServiceKey, ProducerRecord<String, String> record, Callback callback);
    
    void close();
    
}
