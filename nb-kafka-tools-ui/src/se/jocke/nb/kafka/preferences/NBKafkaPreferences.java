package se.jocke.nb.kafka.preferences;

import se.jocke.nb.kafka.nodes.root.ClientConnectionConfig;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import static java.util.stream.Collectors.toMap;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import se.jocke.nb.kafka.nodes.root.KafkaServiceKey;

public final class NBKafkaPreferences {

    private static final Preferences PREFS_FOR_MODULE = NbPreferences.forModule(NBKafkaPreferences.class);

    public static void createNode(String name) {
        try {
            if (!PREFS_FOR_MODULE.nodeExists(name)) {
                Preferences node = PREFS_FOR_MODULE.node(name);
                node.put(ClientConnectionConfig.BOOTSTRAP_SERVERS.getKey(), "localhost:9092");
                node.sync();
            }
        } catch (BackingStoreException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private NBKafkaPreferences() {
    }

    public static Map<String, Object> readConsumerConfigs(KafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isConsumerConfig());
    }

    public static Map<String, Object> readProducerConfigs(KafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isProducerConfig());
    }

    public static Map<String, Object> readAdminConfigs(KafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isAdminConfig());
    }

    public static Map<String, Object> readConfigsByType(KafkaServiceKey kafkaServiceKey, Predicate<ClientConnectionConfig> predicate) {
        try {
            return Arrays.stream(PREFS_FOR_MODULE.node(kafkaServiceKey.getName()).keys())
                    .peek(key -> System.out.println(key))
                    .filter(key -> predicate.test(ClientConnectionConfig.ofKey(key)))
                    .map(key -> new AbstractMap.SimpleEntry<>(key, get(kafkaServiceKey, ClientConnectionConfig.ofKey(key))))
                    .filter(entry -> entry.getValue().isPresent())
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get()))
                    .peek(entry -> System.out.println(entry))
                    .collect(toMap(Entry::getKey, Entry::getValue));
        } catch (BackingStoreException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static void store(KafkaServiceKey key, Map<ClientConnectionConfig, Object> config) {
        config.forEach((conf, value) -> put(key, conf, value));
        Preferences preferences = PREFS_FOR_MODULE.node(key.getName());
        try {
            preferences.sync();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void put(KafkaServiceKey key, ClientConnectionConfig config, Object value) {
        try {
            Preferences preferences = PREFS_FOR_MODULE.node(key.getName());
            Set<String> keySet = new HashSet<>(Arrays.asList(preferences.keys()));
            if (value == null && keySet.contains(config.getKey())) {
                preferences.remove(config.getKey());
            } else if (config.getPropertyType() == Boolean.class) {
                preferences.putBoolean(config.getKey(), (boolean) value);
            } else if (config.getPropertyType() == String.class) {
                preferences.put(config.getKey(), (String) value);
            } else if (config.getPropertyType() == Long.class) {
                preferences.putLong(config.getKey(), (Long) value);
            } else if (config.getPropertyType() == Double.class) {
                preferences.putDouble(config.getKey(), (double) value);
            } else {
                throw new IllegalStateException("Unknown type " + config.getPropertyType());
            }
        } catch (BackingStoreException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Optional<Object> get(KafkaServiceKey key, ClientConnectionConfig config) {
        try {
            if (PREFS_FOR_MODULE.nodeExists(key.getName())) {
                Preferences preferences = PREFS_FOR_MODULE.node(key.getName());
                Set<String> keySet = new HashSet<>(Arrays.asList(preferences.keys()));
                if (keySet.contains(config.getKey())) {
                    if (config.getPropertyType() == Boolean.class) {
                        return Optional.ofNullable(preferences.getBoolean(config.getKey(), false));
                    } else if (config.getPropertyType() == String.class) {
                        return Optional.ofNullable(preferences.get(config.getKey(), null));
                    } else if (config.getPropertyType() == Long.class) {
                        return Optional.ofNullable(preferences.getLong(config.getKey(), -1));
                    } else if (config.getPropertyType() == Double.class) {
                        return Optional.ofNullable(preferences.getDouble(config.getKey(), -1));
                    } else {
                        throw new IllegalStateException("Unknown type " + config.getPropertyType());
                    }
                }
            }
        } catch (BackingStoreException e) {
            throw new IllegalStateException(e);
        }

        return Optional.empty();
    }

    public static void sync() {
        try {
            PREFS_FOR_MODULE.sync();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static Set<String> childrenNames() {
        try {
            return new HashSet<>(Arrays.asList(PREFS_FOR_MODULE.childrenNames()));
        } catch (BackingStoreException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
