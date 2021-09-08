package se.jocke.nb.kafka.client;

import java.io.Closeable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import se.jocke.nb.kafka.nodes.topics.KafkaCreateTopic;
import se.jocke.nb.kafka.preferences.KafkaPreferences;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;

/**
 *
 * @author jocke
 */
public final class AdminClientService implements Closeable {

    private final AdminClient adminClient;

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public AdminClientService() {
        if (!KafkaPreferences.isValid()) {
            throw new IllegalStateException("Invalid settings");
        }
        Map<String, String> prefs = KafkaPreferences.read();
        Map<String, Object> conf = new HashMap<>(prefs);
        conf.put(AdminClientConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        this.adminClient = AdminClient.create(conf);
    }

    public void listTopics(Consumer<Collection<KafkaTopic>> namesConsumer, Consumer<Throwable> throwConsumer) {
        adminClient.listTopics().names().whenComplete((Set<String> names, Throwable ex) -> {
            if (ex == null) {
                namesConsumer.accept(names.stream().map(name -> new KafkaTopic(name)).collect(toSet()));
            } else {
                throwConsumer.accept(ex);
            }
        });
    }

    public void createTopics(Collection<KafkaCreateTopic> createTopics, Runnable runnable, Consumer<Throwable> throwConsumer) {
        
        List<NewTopic> newTopics = createTopics
                .stream()
                .map(c -> new NewTopic(c.getName(), c.getNumPartitions(), c.getReplicationFactor()).configs(c.getConfigs()))
                .collect(toList());
        
        System.out.println(newTopics);
        
        adminClient.createTopics(newTopics).all().whenComplete((v, ex) -> {
            if (ex == null) {
                runnable.run();
            } else {
                throwConsumer.accept(ex);
            }
        });
    }

    @Override
    public void close() {
        if (adminClient != null) {
            adminClient.close();
        }
    }
}
