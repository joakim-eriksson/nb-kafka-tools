package se.jocke.nb.kafka.config;

import java.util.Map;
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;

public interface ClientConnectionConfigMapper {

    Map<String, Object> map(NBKafkaServiceKey key, Map<String, Object> config);

}
