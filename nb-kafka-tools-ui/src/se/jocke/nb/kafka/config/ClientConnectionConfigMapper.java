package se.jocke.nb.kafka.config;

import java.util.Map;
import se.jocke.nb.kafka.nodes.root.KafkaServiceKey;

public interface ClientConnectionConfigMapper {

    Map<String, Object> map(KafkaServiceKey key, Map<String, Object> config);

}
