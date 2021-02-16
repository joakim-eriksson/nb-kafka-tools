package se.jocke.nb.kafka.nodes.topics;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author jocke
 */
public final class KafkaTopicNode extends AbstractNode {

    private final KafkaTopic kafkaTopic;

    public KafkaTopicNode(KafkaTopic kafkaTopic) {
        super(Children.LEAF, Lookups.singleton(kafkaTopic));
        this.kafkaTopic = kafkaTopic;
        setDisplayName(kafkaTopic.getName());
        setIconBaseWithExtension("se/jocke/nb/kafka/nodes/topics/topic.png");
    }
}
