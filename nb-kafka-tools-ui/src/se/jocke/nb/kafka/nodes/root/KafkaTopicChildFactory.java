package se.jocke.nb.kafka.nodes.root;

import se.jocke.nb.kafka.nodes.topics.KafkaTopicNode;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import se.jocke.nb.kafka.client.AdminClientService;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;

/**
 *
 * @author jocke
 */
public class KafkaTopicChildFactory extends ChildFactory<KafkaTopic> {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final KafkaServiceKey kafkaService;

    public KafkaTopicChildFactory(KafkaServiceKey kafkaService) {
        this.kafkaService = kafkaService;
    }

    @Override
    protected boolean createKeys(List<KafkaTopic> topics) {

        BlockingQueue<Collection<KafkaTopic>> topicTransfer = new LinkedBlockingDeque<>();

        AdminClientService adminClientService = Lookup.getDefault().lookup(AdminClientService.class);
        adminClientService.listTopics(kafkaService, topicTransfer::offer, throwable -> {
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

        return true;
    }

    public void onException(List<KafkaTopic> topics, Throwable t) {
        logger.log(Level.SEVERE, "Faild to list topics", t);
    }

    @Override
    protected Node createNodeForKey(KafkaTopic key) {
        return new KafkaTopicNode(kafkaService, key);
    }

    public void refresh() {
        this.refresh(false);
    }
}
