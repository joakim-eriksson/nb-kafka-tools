package se.jocke.nb.kafka.nodes.root;

import java.awt.event.ActionEvent;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;
import javax.swing.Action;
import org.openide.*;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import static se.jocke.nb.kafka.action.Actions.actionsOf;
import static se.jocke.nb.kafka.action.Actions.createAction;
import se.jocke.nb.kafka.client.AdminClientService;
import se.jocke.nb.kafka.nodes.topics.KafkaCreateTopic;
import se.jocke.nb.kafka.nodes.topics.TopicEditor;

/**
 *
 * @author jocke
 */
public class KafkaServiceRootNode extends AbstractNode {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final KafkaTopicChildFactory kafkaTopicChildFactory;

    private KafkaServiceRootNode(KafkaTopicChildFactory kafkaTopicChildFactory) {
        super(Children.create(kafkaTopicChildFactory, true));
        this.kafkaTopicChildFactory = kafkaTopicChildFactory;
        setDisplayName("Kafka");
        setIconBaseWithExtension("se/jocke/nb/kafka/nodes/root/kafka.png");
    }

    public KafkaServiceRootNode() {
        this(new KafkaTopicChildFactory());
    }

    public void showTopicEditor() {
        TopicEditor topicEditor = new TopicEditor();
        DialogDescriptor descriptor = new DialogDescriptor(topicEditor, "Create", true, (ActionEvent event) -> {
            LOG.log(Level.INFO, "Action triggered with command {0}", event.getActionCommand());
            
            if ("OK".equalsIgnoreCase(event.getActionCommand())) {
                Map<String, String> configs = topicEditor.getTopicProperties().entrySet().stream().collect(toMap(Entry::getKey, e -> e.getValue().toString()));
                KafkaCreateTopic createTopic = new KafkaCreateTopic.KafkaCreateTopicBuilder()
                        .name(topicEditor.getTopicName())
                        .numPartitions(Optional.ofNullable(topicEditor.getNumberOfPartitions()))
                        .replicationFactor(Optional.ofNullable(topicEditor.getReplicationFactor()))
                        .configs(configs)
                        .build();

                try (AdminClientService client = new AdminClientService()) {
                    client.createTopics(Collections.singletonList(createTopic), this::refreshTopics, Exceptions::printStackTrace);
                }

            } else if ("Cancel".equalsIgnoreCase(event.getActionCommand())) {

            } else {
                throw new AssertionError("Unknown command " + event.getActionCommand());
            }
        });

        DialogDisplayer.getDefault().notifyLater(descriptor);
    }

    public void refreshTopics() {
        kafkaTopicChildFactory.refresh();
    }

    @Override
    public Action[] getActions(boolean context) {
        return actionsOf(
                createAction("Refresh", this::refreshTopics),
                createAction("Create Topic", this::showTopicEditor)
        );
    }
}
