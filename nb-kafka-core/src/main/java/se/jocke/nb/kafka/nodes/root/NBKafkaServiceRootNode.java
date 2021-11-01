package se.jocke.nb.kafka.nodes.root;

import java.util.Arrays;
import javax.swing.Action;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import static se.jocke.nb.kafka.action.Actions.actions;
import static se.jocke.nb.kafka.action.Actions.action;
import static se.jocke.nb.kafka.action.ActionCommandDispatcher.*;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;

public final class NBKafkaServiceRootNode extends AbstractNode {

    private final NBKafkaServiceChildFactory kafkaServiceChildFactory;

    public NBKafkaServiceRootNode(NBKafkaServiceChildFactory kafkaServiceChildFactory) {
        super(Children.create(kafkaServiceChildFactory, true));
        this.kafkaServiceChildFactory = kafkaServiceChildFactory;
        setIconBaseWithExtension("se/jocke/nb/kafka/nodes/root/kafka.png");
        setDisplayName("Kafka");

    }

    public NBKafkaServiceRootNode() {
        this(new NBKafkaServiceChildFactory());
    }
        
    @Override
    public Action[] getActions(boolean context) {
        return actions(
                action("Create Connection", this::createConnection)
        );
    }

    public void onCreateKafakaServiceDialogDescriptorActionOK(NBKafkaServiceEditor editor) {
        String name = editor.getServiceName();

        if (name != null && !name.isBlank()) {
            NBKafkaPreferences.store(new NBKafkaServiceKey(name), editor.getProps());
            kafkaServiceChildFactory.refresh();
        }
    }

    private void createConnection() {
        NBKafkaServiceEditor editKafkaServicePanel = new NBKafkaServiceEditor();
        DialogDescriptor descriptor = new DialogDescriptor(editKafkaServicePanel, "Create connection", true, onAction(ok(e -> onCreateKafakaServiceDialogDescriptorActionOK(editKafkaServicePanel))));
        DialogDisplayer.getDefault().notifyLater(descriptor);
    }
}
