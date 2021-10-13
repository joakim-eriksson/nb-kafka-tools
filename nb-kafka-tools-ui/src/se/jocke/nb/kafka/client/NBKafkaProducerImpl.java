package se.jocke.nb.kafka.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openide.util.lookup.ServiceProvider;
import se.jocke.nb.kafka.preferences.KafkaPreferences;

@ServiceProvider(service = NBKafkaProducer.class)
public final class NBKafkaProducerImpl implements NBKafkaProducer {

    private final KafkaProducer<String, String> producer;

    public NBKafkaProducerImpl() {
        Map<String, String> prefs = KafkaPreferences.read();
        Map<String, Object> configProps = new HashMap<>(prefs);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        this.producer = new KafkaProducer<>(configProps);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<String, String> record, Callback callback) {
        return producer.send(record, callback);
    }

    @Override
    public void close() {
        producer.close();
    }
}
