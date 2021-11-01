package se.jocke.nb.kafka.nodes.root;

import se.jocke.nb.kafka.nodes.topics.KafkaTopicNode;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import static java.util.function.Predicate.not;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import se.jocke.nb.kafka.client.AdminClientService;
import se.jocke.nb.kafka.config.ClientConnectionConfig;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;

/**
 *
 * @author jocke
 */
public class KafkaTopicChildFactory extends ChildFactory<KafkaTopic> {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final KafkaServiceKey kafkaServiceKey;

    public KafkaTopicChildFactory(KafkaServiceKey kafkaService) {
        this.kafkaServiceKey = kafkaService;
    }

    @Override
    protected boolean createKeys(List<KafkaTopic> topics) {

        if (!NBKafkaPreferences.getBoolean(kafkaServiceKey, ClientConnectionConfig.LIST_TOPICS_DISABLED)) {

            BlockingQueue<Collection<KafkaTopic>> topicTransfer = new LinkedBlockingDeque<>();

            AdminClientService adminClientService = Lookup.getDefault().lookup(AdminClientService.class);
            adminClientService.listTopics(kafkaServiceKey, topicTransfer::offer, throwable -> {
                onException(topics, throwable);
            });

            Collection<KafkaTopic> poll;
            try {
                poll = topicTransfer.poll(20, TimeUnit.SECONDS);
                if (poll != null) {
                    topics.addAll(poll);
                }
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        NBKafkaPreferences.getStrings(kafkaServiceKey, ClientConnectionConfig.SAVED_TOPICS)
                .stream()
                .map(name -> new KafkaTopic(name, Optional.empty()))
                .filter(not(topics::contains))
                .forEach(topics::add);

        return true;
    }

    public void onException(List<KafkaTopic> topics, Throwable t) {
        logger.log(Level.SEVERE, "Faild to list topics", t);
    }

    @Override
    protected Node createNodeForKey(KafkaTopic key) {
        return new KafkaTopicNode(kafkaServiceKey, key);
    }

    public void refresh() {
        this.refresh(false);
    }
}
