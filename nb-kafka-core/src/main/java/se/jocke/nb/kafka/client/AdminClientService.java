package se.jocke.nb.kafka.client;

import java.util.Collection;
import java.util.function.Consumer;
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;
import se.jocke.nb.kafka.nodes.topics.NBKafkaCreateTopic;
import se.jocke.nb.kafka.nodes.topics.NBKafkaTopic;

/**
 *
 * @author jocke
 */
public interface AdminClientService {

    void close();

    void createTopics(NBKafkaServiceKey kafkaServiceKey, Collection<NBKafkaCreateTopic> createTopics, Runnable runnable, Consumer<Throwable> throwConsumer);

    void listTopics(NBKafkaServiceKey kafkaServiceKey, Consumer<Collection<NBKafkaTopic>> namesConsumer, Consumer<Throwable> throwConsumer);

}
