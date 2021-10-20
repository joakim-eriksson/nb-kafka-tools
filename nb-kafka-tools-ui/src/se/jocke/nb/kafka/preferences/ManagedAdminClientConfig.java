package se.jocke.nb.kafka.preferences;
import org.apache.kafka.clients.admin.AdminClientConfig;

/**
 *
 * @author jocke
 */
public enum ManagedAdminClientConfig {
    BOOTSTRAP_SERVERS(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, true),
    GCP_ENABLED("gcp.enabled", false),
    GCP_SECRET_VERSION_REQUEST_NAME("gcp.secretVersionRequestName", false),
    GCP_ENCODED_KEY("gcp.encoded.key", false);

    private final String key;
    private final boolean required;

    private ManagedAdminClientConfig(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return required;
    }
}
