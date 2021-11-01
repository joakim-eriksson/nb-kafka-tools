package se.jocke.nb.kafka.preferences;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import se.jocke.nb.kafka.config.ClientConnectionConfig;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
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
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;

public final class NBKafkaPreferences {

    private static final Preferences PREFS_FOR_MODULE = NbPreferences.forModule(NBKafkaPreferences.class);

    private NBKafkaPreferences() {
    }

    public static Map<String, Object> readConsumerConfigs(NBKafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isConsumerConfig());
    }

    public static Map<String, Object> readProducerConfigs(NBKafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isProducerConfig());
    }

    public static Map<String, Object> readAdminConfigs(NBKafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isAdminConfig());
    }

    public static Map<ClientConnectionConfig, Object> readAll(NBKafkaServiceKey key) {
        return readConfigsByType(key, (k) -> true).entrySet()
                .stream().map(e -> new SimpleEntry<>(ClientConnectionConfig.ofKey(e.getKey()), e.getValue()))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    public static Map<String, Object> readConfigsByType(NBKafkaServiceKey kafkaServiceKey, Predicate<ClientConnectionConfig> predicate) {
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

    public static void store(NBKafkaServiceKey key, Map<ClientConnectionConfig, Object> config) {
        config.forEach((conf, value) -> put(key, conf, value));
        sync(key);
    }

    public static void put(NBKafkaServiceKey key, ClientConnectionConfig config, Object value) {
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
            } else if (config.getPropertyType() == Set.class) {
                preferences.put(config.getKey(), String.join(",", ((Set<String>) value)));
            } else {
                throw new IllegalStateException("Unknown type " + config.getPropertyType());
            }
        } catch (BackingStoreException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Optional<Object> get(NBKafkaServiceKey key, ClientConnectionConfig config) {
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
                    } else if (config.getPropertyType() == Set.class) {
                        return Optional.ofNullable(Sets.newLinkedHashSet(Splitter.on(",").omitEmptyStrings().split(preferences.get(config.getKey(), ""))));
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
    
    //JEP 301 Closed / Withdrawn ):
    public static boolean getBoolean(NBKafkaServiceKey key, ClientConnectionConfig config) {
        return get(key, config).map(conf -> Boolean.class.cast(conf)).orElse(Boolean.FALSE);
    }

    public static Optional<String> getString(NBKafkaServiceKey key, ClientConnectionConfig config) {
        return get(key, config).map(conf -> String.class.cast(conf));
    }
    
    public static Set<String> getStrings(NBKafkaServiceKey key, ClientConnectionConfig config) {
        return get(key, config).map(conf -> (Set<String>) conf).orElse(Collections.emptySet());
    }

    public static void sync(NBKafkaServiceKey key) {
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
