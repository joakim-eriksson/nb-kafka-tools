package se.jocke.nb.kafka.client;

import java.io.Closeable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Optional.ofNullable;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigResource;
import org.openide.util.lookup.ServiceProvider;
import se.jocke.nb.kafka.nodes.topics.KafkaCreateTopic;
import se.jocke.nb.kafka.preferences.KafkaPreferences;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;

/**
 *
 * @author jocke
 */
@ServiceProvider(service = AdminClientService.class)
public final class AdminClientServiceImpl implements Closeable, AdminClientService {

    private final AdminClient adminClient;

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public AdminClientServiceImpl() {
        if (!KafkaPreferences.isValid()) {
            throw new IllegalStateException("Invalid settings");
        }
        Map<String, String> prefs = KafkaPreferences.read();
        Map<String, Object> conf = new HashMap<>(prefs);
        conf.put(AdminClientConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        this.adminClient = AdminClient.create(conf);
    }

    @Override
    public void listTopics(Consumer<Collection<KafkaTopic>> namesConsumer, Consumer<Throwable> throwConsumer) {
        adminClient.listTopics().names().whenComplete((Set<String> names, Throwable ex) -> {
            stopOnException(ex, throwConsumer, () -> {
                adminClient.describeConfigs(mapTopicNameToResourceDesc(names)).all().whenComplete((descs, exDesc) -> {
                    stopOnException(exDesc, throwConsumer, () -> namesConsumer.accept(mapResourceDescToKafkaTopic(descs)));
                });
            });
        });
    }

    public <T> void stopOnException(Throwable ex, Consumer<Throwable> throwConsumer, Runnable dataConsumer) {
        if (ex == null) {
            dataConsumer.run();
        } else {
            throwConsumer.accept(ex);
        }
    }

    private Set<ConfigResource> mapTopicNameToResourceDesc(Set<String> names) {
        System.out.println(names);
        return names.stream().map(nm -> new ConfigResource(ConfigResource.Type.TOPIC, nm)).collect(toSet());
    }

    private Set<KafkaTopic> mapResourceDescToKafkaTopic(Map<ConfigResource, Config> descs) {
        System.out.println(descs);
        return descs.entrySet()
                .stream()
                .map(entry -> new KafkaTopic(entry.getKey().name(), ofNullable(entry.getValue())))
                .collect(toSet());
    }

    @Override
    public void createTopics(Collection<KafkaCreateTopic> createTopics, Runnable runnable, Consumer<Throwable> throwConsumer) {

        List<NewTopic> newTopics = createTopics
                .stream()
                .map(c -> new NewTopic(c.getName(), c.getNumPartitions(), c.getReplicationFactor()).configs(c.getConfigs()))
                .collect(toList());

        adminClient.createTopics(newTopics).all().whenComplete((v, ex) -> {
            stopOnException(ex, throwConsumer, runnable);
        });
    }

    @Override
    public void close() {
        if (adminClient != null) {
            try {
                adminClient.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to close admin client {0} ", e.getMessage());
            }
        }
    }
}
