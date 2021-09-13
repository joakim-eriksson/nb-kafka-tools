package se.jocke.nb.kafka.client;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class NBKafkaConsumer {

    private final KafkaConsumer<String, String> consumer;
    private final Collection<String> topics;
    private final BlockingQueue<NBKafkaConsumerRecord> messages = new ArrayBlockingQueue<>(500);
    private final ExecutorService executorService;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);

    public NBKafkaConsumer(String topic) {
        Properties configProps = new Properties();
        configProps.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        this.consumer = new KafkaConsumer<>(configProps);
        this.executorService = Executors.newSingleThreadExecutor();
        this.topics = Collections.singletonList(topic);

    }

    public static NBKafkaConsumer create(String topic) {
        return new NBKafkaConsumer(topic);
    }

    public NBKafkaConsumer start() {
        if (started.compareAndSet(false, true)) {
            executorService.submit(() -> {
                consumer.subscribe(topics);

                while (running.get()) {
                    consumer.poll(Duration.of(100, ChronoUnit.MILLIS)).forEach(this::publish);
                }
            });
        }

        return this;
    }

    private void publish(ConsumerRecord<String, String> message) {
        messages.offer(NBKafkaConsumerRecord.of(message));
    }

    public void shutdown() {
        running.set(false);
        executorService.shutdown();
    }
}
