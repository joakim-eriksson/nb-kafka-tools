package se.jocke.nb.kafka.nodes.root;

import java.awt.event.ActionEvent;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.*;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import se.jocke.nb.kafka.nodes.topics.TopicEditor;

/**
 *
 * @author jocke
 */
public class KafkaServiceRootNode extends AbstractNode {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public KafkaServiceRootNode() {
        super(Children.create(new KafkaTopicChildFactory(), true));
        setDisplayName("Kafka");
        setIconBaseWithExtension("se/jocke/nb/kafka/nodes/root/kafka.png");
    }

    public void showTopicEditor() {
        DialogDescriptor descriptor = new DialogDescriptor(new TopicEditor(), "Create", true, (ActionEvent event) -> {
            LOG.log(Level.INFO, "Action triggered with command {0}", event.getActionCommand());
            if ("OK".equalsIgnoreCase(event.getActionCommand())) {

            } else if ("Cancel".equalsIgnoreCase(event.getActionCommand())) {

            } else {
                throw new AssertionError("Unknown command " + event.getActionCommand());
            }
        });
        descriptor.setClosingOptions(null);
        DialogDisplayer.getDefault().notifyLater(descriptor);
    }

    @Override
    public Action[] getActions(boolean context) {
        Action create = new AbstractAction("Create Topic") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                showTopicEditor();
            }
        };
        return new Action[]{create};
    }
}
