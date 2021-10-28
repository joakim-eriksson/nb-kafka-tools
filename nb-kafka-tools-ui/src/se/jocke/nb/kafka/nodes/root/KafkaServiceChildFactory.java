package se.jocke.nb.kafka.nodes.root;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;

public class KafkaServiceChildFactory extends ChildFactory<KafkaServiceKey> {

    @Override
    protected boolean createKeys(List<KafkaServiceKey> toPopulate) {
        NBKafkaPreferences.childrenNames()
                .stream()
                .map(KafkaServiceKey::new)
                .forEach(toPopulate::add);
        return true;
    }

    @Override
    protected Node createNodeForKey(KafkaServiceKey key) {
        return new KafkaServiceNode(key);
    }

    public void refresh() {
        this.refresh(false);
    }
    
    
}
