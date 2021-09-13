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
import org.openide.windows.WindowManager;
import se.jocke.nb.kafka.NBKafkaConfigEntry;
import se.jocke.nb.kafka.window.RecordsTopComponent;
import static se.jocke.nb.kafka.window.RecordsTopComponent.RECORDS_TOP_COMPONENT_ID;
import static se.jocke.nb.kafka.action.Actions.actions;
import static se.jocke.nb.kafka.action.Actions.action;

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

    public void openRecords() {
        RecordsTopComponent component = (RecordsTopComponent) WindowManager.getDefault().findTopComponent(RECORDS_TOP_COMPONENT_ID);
        component.showTopic(kafkaTopic);
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
