package se.jocke.nb.kafka.client;

import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 *
 * @author jocke
 */
public interface NBKafkaProducer extends AutoCloseable {

    Future<RecordMetadata> send(ProducerRecord<String, String> record, Callback callback);
    
}
