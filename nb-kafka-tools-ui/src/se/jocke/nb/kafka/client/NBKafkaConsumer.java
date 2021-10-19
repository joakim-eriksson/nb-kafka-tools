package se.jocke.nb.kafka.client;

import com.google.common.util.concurrent.RateLimiter;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toSet;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import se.jocke.nb.kafka.Disposable;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;
import se.jocke.nb.kafka.preferences.KafkaPreferences;

public class NBKafkaConsumer implements Disposable {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final Collection<KafkaTopic> topics;
    private final BlockingQueue<NBKafkaConsumerRecord> messages = new ArrayBlockingQueue<>(5000);
    private final ExecutorService executorService;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final Lock lock = new ReentrantLock();
    private final Condition waitCondition = lock.newCondition();

    private final AtomicInteger consumedCount = new AtomicInteger();

    private final Predicate<NBKafkaConsumerRecord> predicate;

    private final Consumer<NBKafkaConsumerRecord> observer;

    private final RateLimiter limit = RateLimiter.create(1);

    private final Map<String, Object> configProps;

    public NBKafkaConsumer(KafkaTopic topic,
            Consumer<NBKafkaConsumerRecord> observer,
            Predicate<NBKafkaConsumerRecord> predicates,
            Map<String, String> props,
            double rate) {
        this.predicate = predicates;

        if (!KafkaPreferences.isValid()) {
            throw new IllegalStateException("Invalid settings");
        }

        Map<String, String> prefs = KafkaPreferences.read();
        this.configProps = new HashMap<>(prefs);
        configProps.putAll(props);
        configProps.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);

        this.executorService = Executors.newSingleThreadExecutor();
        this.topics = Collections.singletonList(topic);
        this.observer = observer;
        this.limit.setRate(rate);
    }

    public NBKafkaConsumer start() {
        if (started.compareAndSet(false, true)) {
            executorService.submit(() -> {
                try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configProps)) {
                    Set<String> topicNames = topics.stream().map(KafkaTopic::getName).collect(toSet());
                    consumer.subscribe(topicNames);
                    while (!shutdown.get()) {
                        while (running.get()) {
                            consumer.poll(Duration.of(100, ChronoUnit.MILLIS)).forEach(this::publish);
                        }
                        limit.acquire();
                    }
                }
            });
        }
        return this;
    }

    private void publish(ConsumerRecord<String, String> message) {

        runWithLock(() -> {
            while (!running.get() && !shutdown.get()) {
                waitCondition.awaitUninterruptibly();
            }
        });

        NBKafkaConsumerRecord record = NBKafkaConsumerRecord.of(message);
        consumedCount.incrementAndGet();

        boolean isNotFiltered = predicate.test(record);

        if (isNotFiltered) {
            messages.offer(record);
            observer.accept(record);
            limit.acquire();
        }
    }

    @Override
    public void dispose() {
        runWithLock(() -> {
            shutdown.set(true);
            running.set(false);
            executorService.shutdown();
            waitCondition.signalAll();
        });
    }

    public void stop() {
        runWithLock(() -> {
            running.set(false);
            waitCondition.signalAll();
        });
    }

    public void restart() {
        runWithLock(() -> {
            running.set(true);
            waitCondition.signalAll();
        });
    }

    private void runWithLock(Runnable r) {
        lock.lock();
        try {
            r.run();
        } finally {
            lock.unlock();
        }
    }

    public final void setRate(double permitsPerSecond) {
        limit.setRate(permitsPerSecond);
    }

    public final int getCount() {
        return consumedCount.get();
    }

    public static class Builder {

        private KafkaTopic topic;
        private Consumer<NBKafkaConsumerRecord> observer;
        private Predicate<NBKafkaConsumerRecord> predicate = (record) -> true;
        private final Map<String, String> props = new HashMap<>();
        private double rate = 1;

        public Builder() {
        }

        public Builder topic(KafkaTopic topic) {
            this.topic = topic;
            return this;
        }

        public Builder observer(Consumer<NBKafkaConsumerRecord> observer) {
            this.observer = observer;
            return this;
        }

        public Builder predicate(Predicate<NBKafkaConsumerRecord> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder latest() {
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            return this;
        }

        public Builder earliest() {
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return this;
        }

        public Builder rate(double rate) {
            this.rate = rate;
            return this;
        }

        public NBKafkaConsumer build() {
            Objects.requireNonNull(topic, "Topic must not be null");
            Objects.requireNonNull(observer, "Topic must not be null");
            return new NBKafkaConsumer(topic, observer, predicate, props, rate);
        }
    }
}
