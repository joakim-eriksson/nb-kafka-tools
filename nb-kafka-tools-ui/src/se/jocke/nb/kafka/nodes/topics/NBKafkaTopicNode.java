package se.jocke.nb.kafka.nodes.topics;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.Action;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import se.jocke.nb.kafka.NBKafkaConfigEntry;
import se.jocke.nb.kafka.window.RecordsTopComponent;
import static se.jocke.nb.kafka.action.Actions.actions;
import static se.jocke.nb.kafka.action.Actions.action;
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;

/**
 *
 * @author jocke
 */
public final class NBKafkaTopicNode extends AbstractNode {

    private final NBKafkaTopic kafkaTopic;

    private final NBKafkaServiceKey kafkaServiceKey;

    public NBKafkaTopicNode(NBKafkaServiceKey kafkaServiceKey, NBKafkaTopic kafkaTopic) {
        super(Children.LEAF, Lookups.singleton(kafkaTopic));
        this.kafkaTopic = kafkaTopic;
        this.kafkaServiceKey = kafkaServiceKey;
        setDisplayName(kafkaTopic.getName());
        setIconBaseWithExtension("se/jocke/nb/kafka/nodes/topics/topic.png");
    }

    public void openRecords() {
        RecordsTopComponent component = new RecordsTopComponent();
        component.showTopic(kafkaServiceKey, kafkaTopic);
    }

    @Override
    protected Sheet createSheet() {

        List<ReadOnlyImpl> props = kafkaTopic.getConfigs()
                .stream()
                .map(ReadOnlyImpl::new)
                .collect(toList());

        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(props.toArray(new ReadOnlyImpl[0]));
        sheet.put(set);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {

        return actions(
                action("View records", this::openRecords),
                SystemAction.get(PropertiesAction.class)
        );
    }

    private static final class ReadOnlyImpl extends PropertySupport.ReadOnly<String> {

        private final NBKafkaConfigEntry configEntry;

        public ReadOnlyImpl(NBKafkaConfigEntry configEntry) {
            super(configEntry.getName(), String.class, configEntry.getName(), configEntry.getDocumentation());
            this.configEntry = configEntry;
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return configEntry.getValue();
        }

    }
}
