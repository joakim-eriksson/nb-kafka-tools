package se.jocke.nb.kafka.preferences;
import org.apache.kafka.clients.admin.AdminClientConfig;

/**
 *
 * @author jocke
 */
public enum ManagedAdminClientConfig {
    BOOTSTRAP_SERVERS(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG);

    private final String key;

    private ManagedAdminClientConfig(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
