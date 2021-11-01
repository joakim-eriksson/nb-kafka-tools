package se.jocke.nb.kafka.client;

import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;

/**
 *
 * @author jocke
 */
public interface NBKafkaProducer  {

    Future<RecordMetadata> send(NBKafkaServiceKey kafkaServiceKey, ProducerRecord<String, String> record, Callback callback);
    
    void close();
    
}
