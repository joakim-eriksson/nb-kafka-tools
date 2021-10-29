package se.jocke.nb.kafka.preferences;

import se.jocke.nb.kafka.config.ClientConnectionConfig;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import static java.util.stream.Collectors.toMap;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import se.jocke.nb.kafka.config.ClientConnectionConfigMapper;
import se.jocke.nb.kafka.nodes.root.KafkaServiceKey;

public final class NBKafkaPreferences {

    private static final Preferences PREFS_FOR_MODULE = NbPreferences.forModule(NBKafkaPreferences.class);

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

    public static Map<ClientConnectionConfig, Object> readAll(KafkaServiceKey key) {
        return readConfigsByType(key, (k) -> true).entrySet()
                .stream().map(e -> new SimpleEntry<>(ClientConnectionConfig.ofKey(e.getKey()), e.getValue()))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    public static Map<String, Object> readConfigsByType(KafkaServiceKey kafkaServiceKey, Predicate<ClientConnectionConfig> predicate) {
        try {
            Map<String, Object> conf = Arrays.stream(PREFS_FOR_MODULE.node(kafkaServiceKey.getName()).keys())
                    .filter(key -> predicate.test(ClientConnectionConfig.ofKey(key)))
                    .map(key -> new AbstractMap.SimpleEntry<>(key, get(kafkaServiceKey, ClientConnectionConfig.ofKey(key))))
                    .filter(entry -> entry.getValue().isPresent())
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get()))
                    .collect(toMap(Entry::getKey, Entry::getValue));
            
            //No overrides allowed
            Lookup.getDefault()
                    .lookupAll(ClientConnectionConfigMapper.class)
                    .stream()
                    .forEach(mapper -> mapper.map(kafkaServiceKey, new LinkedHashMap<>(conf)).forEach(conf::putIfAbsent));            

            return conf;

        } catch (BackingStoreException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static void store(KafkaServiceKey key, Map<ClientConnectionConfig, Object> config) {
        config.forEach((conf, value) -> put(key, conf, value));
        sync(key);
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

    public static boolean getBoolean(KafkaServiceKey key, ClientConnectionConfig config) {
        try {
            if (PREFS_FOR_MODULE.nodeExists(key.getName())) {
                Preferences preferences = PREFS_FOR_MODULE.node(key.getName());
                return preferences.getBoolean(config.getKey(), false);
            }
        } catch (BackingStoreException e) {
            throw new IllegalStateException(e);
        }
        return false;
    }

    public static String getString(KafkaServiceKey key, ClientConnectionConfig config) {
        try {
            if (PREFS_FOR_MODULE.nodeExists(key.getName())) {
                Preferences preferences = PREFS_FOR_MODULE.node(key.getName());
                return preferences.get(config.getKey(), null);
            }
        } catch (BackingStoreException e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    public static void sync(KafkaServiceKey key) {
        Preferences preferences = PREFS_FOR_MODULE.node(key.getName());
        try {
            preferences.sync();
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
