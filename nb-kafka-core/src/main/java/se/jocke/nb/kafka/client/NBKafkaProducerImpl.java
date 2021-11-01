package se.jocke.nb.kafka.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openide.util.lookup.ServiceProvider;
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;

@ServiceProvider(service = NBKafkaProducer.class)
public final class NBKafkaProducerImpl implements NBKafkaProducer {

    private final Map<NBKafkaServiceKey, KafkaProducer<String, String>> producers = new ConcurrentHashMap<>();

    @Override
    public Future<RecordMetadata> send(NBKafkaServiceKey kafkaServiceKey, ProducerRecord<String, String> record, Callback callback) {
        return getProducer(kafkaServiceKey).send(record, callback);
    }

    @Override
    public void close() {
        producers.values().stream().forEach(KafkaProducer::close);
    }

    private KafkaProducer<String, String> getProducer(NBKafkaServiceKey key) {
        return producers.computeIfAbsent(key, (mapKey) -> {
            Map<String, Object> prefs = NBKafkaPreferences.readProducerConfigs(mapKey);
            Map<String, Object> configProps = new HashMap<>(prefs);
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return new KafkaProducer<>(configProps);
        });
    }

}
