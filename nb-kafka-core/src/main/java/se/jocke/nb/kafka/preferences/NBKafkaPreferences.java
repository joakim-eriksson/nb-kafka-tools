package se.jocke.nb.kafka.preferences;

import com.google.common.base.Predicates;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
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
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;

import java.util.stream.Stream;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import se.jocke.nb.kafka.config.ClientConnectionConfig;
import se.jocke.nb.kafka.config.ClientConnectionConfigMapper;
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;
import se.jocke.nb.kafka.nodes.topics.NBKafkaTopic;

public final class NBKafkaPreferences {

    private static final Preferences PREFS_FOR_MODULE = NbPreferences.forModule(NBKafkaPreferences.class);

    private NBKafkaPreferences() {
    }

    public static Map<String, Object> readConsumerConfigs(NBKafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isConsumerConfig(), true);
    }

    public static Map<String, Object> readProducerConfigs(NBKafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isProducerConfig(), true);
    }

    public static Map<String, Object> readAdminConfigs(NBKafkaServiceKey key) {
        return readConfigsByType(key, c -> c.isAdminConfig(), true);
    }

    public static Map<ClientConnectionConfig, Object> readAll(NBKafkaServiceKey key) {
        return readConfigsByType(key, (k) -> true, false).entrySet()
                .stream().map(e -> new SimpleEntry<>(ClientConnectionConfig.ofKey(e.getKey()), e.getValue()))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    public static Predicate<NBKafkaTopic> topicFilter(NBKafkaServiceKey key) {
      final Optional<String> expression = getString(key, ClientConnectionConfig.LIST_TOPICS_FILTER_EXPESSION);
        if(!expression.isEmpty()) {
          if(getBoolean(key, ClientConnectionConfig.LIST_TOPICS_FILTER_IS_REGEX)) {
            return topic -> Pattern.compile(expression.get()).matcher(topic.getName()).matches();
          } else {
            return topic -> topic.getName().contains(expression.get());
          }
        }
        return Predicates.alwaysTrue();
    }

    public static Map<String, Object> readConfigsByType(NBKafkaServiceKey kafkaServiceKey, Predicate<ClientConnectionConfig> predicate, boolean useMapper) {
        try {
            Map<String, Object> conf = Arrays.stream(PREFS_FOR_MODULE.node(kafkaServiceKey.getName()).keys())
                    .filter(key -> predicate.test(ClientConnectionConfig.ofKey(key)))
                    .map(key -> new AbstractMap.SimpleEntry<>(key, get(kafkaServiceKey, ClientConnectionConfig.ofKey(key))))
                    .filter(entry -> entry.getValue().isPresent())
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get()))
                    .collect(toMap(Entry::getKey, Entry::getValue));

            if (useMapper) {
                Lookup.getDefault()
                        .lookupAll(ClientConnectionConfigMapper.class)
                        .stream()
                        .forEach(mapper -> mapper.map(kafkaServiceKey, new LinkedHashMap<>(conf)).forEach(conf::putIfAbsent));
            }
            //No overrides allowed
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
            } else {
                config.putPeference(preferences, value);
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
                    return Optional.of(config.getFromPeferences(preferences));
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

    public static void exportProperties(NBKafkaServiceKey key, OutputStream out) {
        try {
            if (PREFS_FOR_MODULE.nodeExists(key.getName())) {
                Preferences preferences = PREFS_FOR_MODULE.node(key.getName());
                Stream.of(preferences.keys())
                        .map(name -> (name + "=" + preferences.get(name, "") + "\n").getBytes())
                        .forEachOrdered(bytes -> {
                            try {
                                out.write(bytes);
                            } catch (IOException ex) {
                                throw new UncheckedIOException(ex);
                            }
                        });
            }
        } catch (BackingStoreException e) {
            throw new IllegalStateException(e);
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
