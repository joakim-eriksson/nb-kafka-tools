package se.jocke.nb.kafka.client;

import com.google.common.util.concurrent.RateLimiter;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toSet;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;
import se.jocke.nb.kafka.preferences.KafkaPreferences;

public class NBKafkaConsumer {
    
    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final KafkaConsumer<String, String> consumer;
    private final Collection<KafkaTopic> topics;
    private final BlockingQueue<NBKafkaConsumerRecord> messages = new ArrayBlockingQueue<>(5000);
    private final ExecutorService executorService;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Consumer<NBKafkaConsumerRecord> observer;
    
    private final RateLimiter limit = RateLimiter.create(1);

    public NBKafkaConsumer(KafkaTopic topic, Consumer<NBKafkaConsumerRecord> observer) {

        if (!KafkaPreferences.isValid()) {
            throw new IllegalStateException("Invalid settings");
        }

        Map<String, String> prefs = KafkaPreferences.read();
        Map<String, Object> configProps = new HashMap<>(prefs);
        configProps.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        this.consumer = new KafkaConsumer<>(configProps);
        this.executorService = Executors.newSingleThreadExecutor();
        this.topics = Collections.singletonList(topic);
        this.observer = observer;
    }

    public static NBKafkaConsumer create(KafkaTopic topic, Consumer<NBKafkaConsumerRecord> observer) {
        return new NBKafkaConsumer(topic, observer);
    }

    public NBKafkaConsumer start() {
        if (started.compareAndSet(false, true)) {
            executorService.submit(() -> {
                Set<String> topicNames = topics.stream().map(KafkaTopic::getName).collect(toSet());
                consumer.subscribe(topicNames);
                while (running.get()) {
                    consumer.poll(Duration.of(100, ChronoUnit.MILLIS)).forEach(this::publish);
                }
            });
        }
        return this;
    }

    private void publish(ConsumerRecord<String, String> message) {
        NBKafkaConsumerRecord record = NBKafkaConsumerRecord.of(message);
        messages.offer(record);
        observer.accept(record);
        limit.acquire();
    }

    public void shutdown() {
        running.set(false);
        executorService.shutdown();
    }
}
