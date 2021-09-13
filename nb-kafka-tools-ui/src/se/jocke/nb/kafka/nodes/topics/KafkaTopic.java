package se.jocke.nb.kafka.nodes.topics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.apache.kafka.clients.admin.Config;
import se.jocke.nb.kafka.NBKafkaConfigEntry;

/**
 *
 * @author jocke
 */
public final class KafkaTopic {

    private final String name;

    private final List<NBKafkaConfigEntry> configs;

    public KafkaTopic(String name, Optional<Config> optionalConf) {
        this.name = name;
        this.configs = optionalConf.
                map(conf -> mapToConfigs(conf))
                .orElse(new ArrayList<>());
    }

    private static List<NBKafkaConfigEntry> mapToConfigs(Config conf) {
        return conf.entries()
                .stream()
                .map(cfgEntry -> new NBKafkaConfigEntry(cfgEntry.name(), cfgEntry.value(), cfgEntry.documentation()))
                .collect(toList());
    }

    public String getName() {
        return name;
    }

    public List<NBKafkaConfigEntry> getConfigs() {
        return new ArrayList<>(configs);
    }

    @Override
    public String toString() {
        return "KafkaTopic{" + "name=" + name + '}';
    }
}
