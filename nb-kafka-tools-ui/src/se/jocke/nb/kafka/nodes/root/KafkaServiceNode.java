package se.jocke.nb.kafka.nodes.root;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import static java.util.stream.Collectors.toMap;
import javax.swing.Action;
import org.openide.*;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.actions.SystemAction;
import org.openide.windows.WindowManager;
import se.jocke.nb.kafka.nodes.topics.KafkaCreateTopic;
import se.jocke.nb.kafka.nodes.topics.TopicEditor;
import static se.jocke.nb.kafka.action.Actions.actions;
import static se.jocke.nb.kafka.action.Actions.action;
import static se.jocke.nb.kafka.action.ActionCommanDispatcher.*;
import se.jocke.nb.kafka.client.AdminClientService;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;
import se.jocke.nb.kafka.window.RecordsTopComponent;
import static se.jocke.nb.kafka.window.RecordsTopComponent.RECORDS_TOP_COMPONENT_ID;

/**
 *
 * @author jocke
 */
public class KafkaServiceNode extends AbstractNode {

    private final KafkaTopicChildFactory kafkaTopicChildFactory;
    
    private final KafkaServiceKey kafkaServiceKey;

    private KafkaServiceNode(KafkaTopicChildFactory kafkaTopicChildFactory, KafkaServiceKey kafkaServiceKey) {
        super(Children.create(kafkaTopicChildFactory, true));
        this.kafkaTopicChildFactory = kafkaTopicChildFactory;
        this.kafkaServiceKey = kafkaServiceKey;
    }

    public KafkaServiceNode(KafkaServiceKey kafkaServiceKey) {
        this(new KafkaTopicChildFactory(kafkaServiceKey), kafkaServiceKey);
        setDisplayName(kafkaServiceKey.getName());
        setIconBaseWithExtension("se/jocke/nb/kafka/nodes/root/kafka.png");
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        final Map<ClientConnectionConfig, Object> props = NBKafkaPreferences.readAll(kafkaServiceKey);
                
        Map<ClientConnectionConfig, Object> edit = new LinkedHashMap<>(props) {
            @Override
            public Object put(ClientConnectionConfig key, Object value) {
                NBKafkaPreferences.put(kafkaServiceKey, key, value);
                NBKafkaPreferences.sync(kafkaServiceKey);
                return super.put(key, value);
            }    
        };
        
        set.setDisplayName("Connection config");
        
        Arrays.asList(ClientConnectionConfig.values())
                .stream()
                .map(conf -> new ClientConnectionConfigPropertySupport(conf, edit))
                .forEach(set::put);
        sheet.put(set);
        return sheet;
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
        
        client.createTopics(kafkaServiceKey, Collections.singletonList(createTopic), this::refreshTopics, Exceptions::printStackTrace);

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
                    component.showTopic(kafkaServiceKey, kafkaTopic);
                })));
        DialogDisplayer.getDefault().notifyLater(descriptor);
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions(
                action("Refresh", this::refreshTopics),
                action("Create Topic", this::showTopicEditor),
                action("View topic", this::viewTopic),
                SystemAction.get(PropertiesAction.class)
        );
    }
}
