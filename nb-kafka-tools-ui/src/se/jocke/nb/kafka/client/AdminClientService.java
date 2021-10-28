package se.jocke.nb.kafka.client;

import java.util.Collection;
import java.util.function.Consumer;
import se.jocke.nb.kafka.nodes.root.KafkaServiceKey;
import se.jocke.nb.kafka.nodes.topics.KafkaCreateTopic;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;

/**
 *
 * @author jocke
 */
public interface AdminClientService {

    void close();

    void createTopics(KafkaServiceKey kafkaServiceKey, Collection<KafkaCreateTopic> createTopics, Runnable runnable, Consumer<Throwable> throwConsumer);

    void listTopics(KafkaServiceKey kafkaServiceKey, Consumer<Collection<KafkaTopic>> namesConsumer, Consumer<Throwable> throwConsumer);

}
