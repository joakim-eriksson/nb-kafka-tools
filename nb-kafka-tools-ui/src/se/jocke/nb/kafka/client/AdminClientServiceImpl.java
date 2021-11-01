package se.jocke.nb.kafka.client;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.Optional.ofNullable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigResource;
import org.openide.util.lookup.ServiceProvider;
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;
import se.jocke.nb.kafka.nodes.topics.NBKafkaCreateTopic;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;
import se.jocke.nb.kafka.nodes.topics.NBKafkaTopic;

/**
 *
 * @author jocke
 */
@ServiceProvider(service = AdminClientService.class)
public final class AdminClientServiceImpl implements Closeable, AdminClientService {

    private final Map<NBKafkaServiceKey, AdminClient> clients = new ConcurrentHashMap<>();

    @Override
    public void listTopics(NBKafkaServiceKey kafkaServiceKey, Consumer<Collection<NBKafkaTopic>> namesConsumer, Consumer<Throwable> throwConsumer) {
        getAdminClient(kafkaServiceKey).listTopics().names().whenComplete((Set<String> names, Throwable ex) -> {
            stopOnException(ex, throwConsumer, () -> {
                getAdminClient(kafkaServiceKey).describeConfigs(mapTopicNameToResourceDesc(names)).all().whenComplete((descs, exDesc) -> {
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
        return names.stream().map(nm -> new ConfigResource(ConfigResource.Type.TOPIC, nm)).collect(toSet());
    }

    private Set<NBKafkaTopic> mapResourceDescToKafkaTopic(Map<ConfigResource, Config> descs) {
        return descs.entrySet()
                .stream()
                .map(entry -> new NBKafkaTopic(entry.getKey().name(), ofNullable(entry.getValue())))
                .collect(toSet());
    }

    @Override
    public void createTopics(NBKafkaServiceKey kafkaServiceKey, Collection<NBKafkaCreateTopic> createTopics, Runnable runnable, Consumer<Throwable> throwConsumer) {

        List<NewTopic> newTopics = createTopics
                .stream()
                .map(c -> new NewTopic(c.getName(), c.getNumPartitions(), c.getReplicationFactor()).configs(c.getConfigs()))
                .collect(toList());

        getAdminClient(kafkaServiceKey).createTopics(newTopics).all().whenComplete((v, ex) -> {
            stopOnException(ex, throwConsumer, runnable);
        });
    }

    @Override
    public void close() {
        clients.values().forEach(AdminClient::close);
    }

    private AdminClient getAdminClient(NBKafkaServiceKey key) {
        return clients.computeIfAbsent(key, (mapKey) -> AdminClient.create(NBKafkaPreferences.readAdminConfigs(key)));
    }
}
