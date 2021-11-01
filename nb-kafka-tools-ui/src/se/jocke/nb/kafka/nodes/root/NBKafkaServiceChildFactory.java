package se.jocke.nb.kafka.nodes.root;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;

public class NBKafkaServiceChildFactory extends ChildFactory<NBKafkaServiceKey> {

    @Override
    protected boolean createKeys(List<NBKafkaServiceKey> toPopulate) {
        NBKafkaPreferences.childrenNames()
                .stream()
                .map(NBKafkaServiceKey::new)
                .forEach(toPopulate::add);
        return true;
    }

    @Override
    protected Node createNodeForKey(NBKafkaServiceKey key) {
        return new NBKafkaServiceNode(key);
    }

    public void refresh() {
        this.refresh(false);
    }
    
    
}
