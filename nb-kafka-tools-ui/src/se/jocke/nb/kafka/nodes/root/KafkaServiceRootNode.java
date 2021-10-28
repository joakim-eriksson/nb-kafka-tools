package se.jocke.nb.kafka.nodes.root;

import javax.swing.Action;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import static se.jocke.nb.kafka.action.Actions.actions;
import static se.jocke.nb.kafka.action.Actions.action;
import static se.jocke.nb.kafka.action.ActionCommanDispatcher.*;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;

public final class KafkaServiceRootNode extends AbstractNode {

    private final KafkaServiceChildFactory kafkaServiceChildFactory;

    public KafkaServiceRootNode(KafkaServiceChildFactory kafkaServiceChildFactory) {
        super(Children.create(kafkaServiceChildFactory, true));
        this.kafkaServiceChildFactory = kafkaServiceChildFactory;
        setIconBaseWithExtension("se/jocke/nb/kafka/nodes/root/kafka.png");
        setDisplayName("Kafka");

    }

    public KafkaServiceRootNode() {
        this(new KafkaServiceChildFactory());
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions(
                action("Create Connection", this::createConnection)
        );
    }

    public void onCreateKafakaServiceDialogDescriptorActionOK(KafkaServiceEditor editor) {
        String name = editor.getServiceName();

        if (name != null && !name.isBlank()) {
            NBKafkaPreferences.store(new KafkaServiceKey(name), editor.getProps());
            kafkaServiceChildFactory.refresh();
        }
    }

    private void createConnection() {
        KafkaServiceEditor editKafkaServicePanel = new KafkaServiceEditor();
        DialogDescriptor descriptor = new DialogDescriptor(editKafkaServicePanel, "Create connection", true, onAction(ok(e -> onCreateKafakaServiceDialogDescriptorActionOK(editKafkaServicePanel))));
        DialogDisplayer.getDefault().notifyLater(descriptor);
    }
}
