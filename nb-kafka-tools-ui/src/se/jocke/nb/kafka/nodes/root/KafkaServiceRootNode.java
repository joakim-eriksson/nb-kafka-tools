package se.jocke.nb.kafka.nodes.root;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import static java.util.stream.Collectors.toMap;
import javax.swing.Action;
import org.openide.*;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;
import se.jocke.nb.kafka.nodes.topics.KafkaCreateTopic;
import se.jocke.nb.kafka.nodes.topics.TopicEditor;
import static se.jocke.nb.kafka.action.Actions.actions;
import static se.jocke.nb.kafka.action.Actions.action;
import static se.jocke.nb.kafka.action.ActionCommanDispatcher.*;
import se.jocke.nb.kafka.client.AdminClientService;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;
import se.jocke.nb.kafka.window.RecordsTopComponent;
import static se.jocke.nb.kafka.window.RecordsTopComponent.RECORDS_TOP_COMPONENT_ID;

/**
 *
 * @author jocke
 */
public class KafkaServiceRootNode extends AbstractNode {

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
        DialogDescriptor descriptor = new DialogDescriptor(topicEditor, "Create", true, onAction(ok(e -> onCreateTopicDialogDescriptorActionOK(topicEditor))));
        DialogDisplayer.getDefault().notifyLater(descriptor);
    }

    private void onCreateTopicDialogDescriptorActionOK(TopicEditor topicEditor) {

        Map<String, String> configs = topicEditor.getTopicProperties().entrySet()
                .stream()
                .collect(toMap(Entry::getKey, e -> e.getValue().toString()));

        KafkaCreateTopic createTopic = new KafkaCreateTopic.KafkaCreateTopicBuilder()
                .name(topicEditor.getTopicName())
                .numPartitions(Optional.ofNullable(topicEditor.getNumberOfPartitions()))
                .replicationFactor(Optional.ofNullable(topicEditor.getReplicationFactor()))
                .configs(configs)
                .build();

        AdminClientService client = Lookup.getDefault().lookup(AdminClientService.class);

        client.createTopics(Collections.singletonList(createTopic), this::refreshTopics, Exceptions::printStackTrace);

    }

    public void refreshTopics() {
        kafkaTopicChildFactory.refresh();
    }

    public void viewTopic() {
        ViewTopicPanel viewTopicPanel = new ViewTopicPanel();
        DialogDescriptor descriptor = new DialogDescriptor(viewTopicPanel, "View Topic", true,
                onAction(ok(event -> {
                    KafkaTopic kafkaTopic = new KafkaTopic(viewTopicPanel.getTopicName(), Optional.empty());
                    RecordsTopComponent component = (RecordsTopComponent) WindowManager.getDefault().findTopComponent(RECORDS_TOP_COMPONENT_ID);
                    component.showTopic(kafkaTopic);
                })));
        DialogDisplayer.getDefault().notifyLater(descriptor);
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions(
                action("Refresh", this::refreshTopics),
                action("Create Topic", this::showTopicEditor),
                action("View topic", this::viewTopic)
        );
    }
}
