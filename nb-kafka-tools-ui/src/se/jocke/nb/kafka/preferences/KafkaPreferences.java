package se.jocke.nb.kafka.preferences;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import static java.util.stream.Collectors.toMap;
import org.openide.util.NbPreferences;

/**
 *
 * @author jocke
 */
public final class KafkaPreferences {

    private static final Preferences PREFS_FOR_MODULE = NbPreferences.forModule(KafkaPreferences.class);
    
    private KafkaPreferences() {
    }

    public static Map<String, String> read() {
        try {
            return Arrays.stream(PREFS_FOR_MODULE.keys())
                    .map(k -> new AbstractMap.SimpleEntry<>(k, PREFS_FOR_MODULE.get(k, "")))
                    .collect(toMap(Entry::getKey, Entry::getValue));
        } catch (BackingStoreException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static void store(Map<String, String> config) {
        config.forEach(PREFS_FOR_MODULE::put);
    }

    public static boolean isValid() {
        return !Arrays.stream(ManagedAdminClientConfig.values())
                .filter(ManagedAdminClientConfig::isRequired)
                .anyMatch(c -> getOrEmpty(c.getKey()).trim().isEmpty());
    }

    public static void sync() throws BackingStoreException {
        PREFS_FOR_MODULE.sync();
    }

    private static String getOrEmpty(String prop) {
        return PREFS_FOR_MODULE.get(prop, "");
    }

    public static boolean getBoolean(String string, boolean bln) {
        return PREFS_FOR_MODULE.getBoolean(string, bln);
    }

    public static String get(String key) {
        String toGet = PREFS_FOR_MODULE.get(key, null);
        Objects.requireNonNull(toGet, key + " required");
        return toGet;
    }   
}
