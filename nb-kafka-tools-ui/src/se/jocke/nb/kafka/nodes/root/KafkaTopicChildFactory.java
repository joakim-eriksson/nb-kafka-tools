package se.jocke.nb.kafka.nodes.root;

import se.jocke.nb.kafka.nodes.topics.KafkaTopicNode;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import se.jocke.nb.kafka.preferences.KafkaPreferences;
import se.jocke.nb.kafka.client.AdminClientService;
import se.jocke.nb.kafka.model.KafkaTopic;

/**
 *
 * @author jocke
 */
public class KafkaTopicChildFactory extends ChildFactory<KafkaTopic> {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
   
    @Override
    protected boolean createKeys(List<KafkaTopic> topics) {
        if (KafkaPreferences.isValid()) {
            try ( AdminClientService adminClientService = new AdminClientService()) {
                adminClientService.listTopics(topics::addAll, throwable -> onException(topics, throwable));
            } catch (Exception e) {
                onException(topics, e);
            }
        }
        return true;
    }

    public void onException(List<KafkaTopic> topics, Throwable t) {
        logger.log(Level.SEVERE, "Faild to list topics", t);
    }

    @Override
    protected Node createNodeForKey(KafkaTopic key) {
        return new KafkaTopicNode(key);
    }
}
