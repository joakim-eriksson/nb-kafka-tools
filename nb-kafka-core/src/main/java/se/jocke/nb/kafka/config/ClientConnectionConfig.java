package se.jocke.nb.kafka.config;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;
import static java.util.stream.Collectors.toMap;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import static se.jocke.nb.kafka.config.ConfigSupport.BOOLEAN;
import static se.jocke.nb.kafka.config.ConfigSupport.LONG;
import static se.jocke.nb.kafka.config.ConfigSupport.SET;
import static se.jocke.nb.kafka.config.ConfigSupport.STRING;

public enum ClientConnectionConfig {
    BOOTSTRAP_SERVERS(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, ClientConnectionConfig.BOOTSTRAP_SERVERS_DOC),
    SSL_KEY_PASSWORD(SslConfigs.SSL_KEY_PASSWORD_CONFIG, SslConfigs.SSL_KEY_PASSWORD_DOC),
    SSL_KEYSTORE_CERTIFICATE_CHAIN(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG, SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_DOC),
    SSL_KEYSTORE_KEY(SslConfigs.SSL_KEYSTORE_KEY_CONFIG, SslConfigs.SSL_KEYSTORE_KEY_DOC),
    SSL_KEYSTORE_LOCATION(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, SslConfigs.SSL_KEYSTORE_LOCATION_DOC),
    SSL_KEYSTORE_PASSWORD(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, SslConfigs.SSL_KEYSTORE_PASSWORD_DOC),
    SSL_TRUSTSTORE_CERTIFICATES(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_DOC),
    SSL_TRUSTSTORE_LOCATION(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, SslConfigs.SSL_TRUSTSTORE_LOCATION_DOC),
    SSL_TRUSTSTORE_PASSWORD(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, SslConfigs.SSL_TRUSTSTORE_PASSWORD_DOC),
    SSL_ENDPOINT_IDENTIFICATION_ALGORITHM(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_DOC),
    CLIENT_DNS_LOOKUP(AdminClientConfig.CLIENT_DNS_LOOKUP_CONFIG, ClientConnectionConfig.CLIENT_DNS_LOOKUP_DOC),
    CLIENT_ID(AdminClientConfig.CLIENT_ID_CONFIG, ClientConnectionConfig.CLIENT_ID_DOC),
    CONNECTIONS_MAX_IDLE_MS(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, ClientConnectionConfig.CONNECTIONS_MAX_IDLE_MS_DOC, LONG, ConfigFor.ALL),
    DEFAULT_API_TIMEOUT_MS(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, ClientConnectionConfig.DEFAULT_API_TIMEOUT_MS_DOC, LONG, ConfigFor.ALL),
    SASL_CLIENT_CALLBACK_HANDLER_CLASS(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS_DOC),
    SASL_JAAS_CONFIG(SaslConfigs.SASL_JAAS_CONFIG, SaslConfigs.SASL_JAAS_CONFIG_DOC),
    SASL_JAAS_CONFIG_TEMPLATE(SaslConfigs.SASL_JAAS_CONFIG + ".template", ClientConnectionConfig.SASL_JAAS_CONFIG_TEMPLATE_DOC),
    SASL_KERBEROS_SERVICE_NAME(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, SaslConfigs.SASL_KERBEROS_SERVICE_NAME_DOC),
    SASL_LOGIN_CALLBACK_HANDLER_CLASS(SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS, SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS_DOC),
    SASL_LOGIN_CLASS(SaslConfigs.SASL_LOGIN_CLASS, SaslConfigs.SASL_LOGIN_CLASS_DOC),
    SASL_MECHANISM(SaslConfigs.SASL_MECHANISM, SaslConfigs.SASL_MECHANISM_DOC),
    SECURITY_PROTOCOL(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, ClientConnectionConfig.SECURITY_PROTOCOL_DOC),
    GCP_SECRET_ENABLED("gcp.secret.enabled", ClientConnectionConfig.GCP_SECRET_ENABLED_DOC, BOOLEAN, ConfigFor.NONE),
    GCP_SECRET_VERSION_REQUEST_NAME("gcp.secret.version.request.name", "Secret name", STRING, ConfigFor.NONE),
    GCP_SECRET_ENCODED_KEY("gcp.encoded.key", "Secret key", STRING, ConfigFor.NONE),
    SAVED_TOPICS("saved.topics", "Topics saved", SET, ConfigFor.NONE),
    LIST_TOPICS_DISABLED("list.topics.disabled", ClientConnectionConfig.LIST_TOPICS_DISABLED_DOC, BOOLEAN, ConfigFor.NONE);
    
    private static final String SASL_JAAS_CONFIG_TEMPLATE_DOC = "Template to use if credentials are fetched externaly for JAAS Config ex, org.apache.kafka.common.security.scram.ScramLoginModule required username='%s' password='%s';";
    
    private static final String DEFAULT_API_TIMEOUT_MS_DOC = "Specifies the timeout (in milliseconds)";
    
    private static final String GCP_SECRET_ENABLED_DOC = "If name and password is a GCP secret";
    
    private static final String LIST_TOPICS_DISABLED_DOC = "Is connecting to kafka and get a list of topics possible, all users may not be authorized to do so";
    
    private static final String SECURITY_PROTOCOL_DOC = "Protocol used to communicate with brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL.";

    private static final String CONNECTIONS_MAX_IDLE_MS_DOC = "Close idle connections after the number of milliseconds specified by this config.";

    private static final String CLIENT_ID_DOC = "An id string to pass to the server when making requests.";

    private static final String BOOTSTRAP_SERVERS_DOC = "A list of host/port pairs to use for establishing the initial connection to the Kafka cluster";

    private static final String CLIENT_DNS_LOOKUP_DOC = "Controls how the client uses DNS lookups.";

    private final String key;

    private final ConfigSupport<Object> configSupport;

    private final boolean adminConfig;

    private final boolean consumerConfig;

    private final boolean producerConfig;

    private final String desc;

    private static final Map<String, ClientConnectionConfig> KEY_CONFIG_MAP = Map.copyOf(
            Arrays.stream(values())
                    .map(c -> new AbstractMap.SimpleEntry<>(c.getKey(), c))
                    .collect(toMap(Entry::getKey, Entry::getValue))
    );

    private <T> ClientConnectionConfig(String key, String desc, ConfigSupport<T> configSupport, ConfigFor configFor) {
        this.key = key;
        this.configSupport = (ConfigSupport<Object>) configSupport;
        this.adminConfig = configFor.isAdminConfig();
        this.consumerConfig = configFor.isConsumerConfig();
        this.producerConfig = configFor.isProducerConfig();
        this.desc = desc;
    }

    private ClientConnectionConfig(String key, String desc) {
        this(key, desc, STRING, ConfigFor.ALL);
    }

    public boolean isAdminConfig() {
        return adminConfig;
    }

    public boolean isConsumerConfig() {
        return consumerConfig;
    }

    public boolean isProducerConfig() {
        return producerConfig;
    }

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    public Class<? extends Object> getPropertyType() {
        return configSupport.getSupportType();
    }

    public static ClientConnectionConfig ofKey(String key) {
        return KEY_CONFIG_MAP.get(key);
    }

    public Object getFromPeferences(Preferences preferences) {
        String value = preferences.get(key, "");
        return value.isBlank() ? null : valueFromString(value);
    }

    public void putPeference(Preferences preferences, Object value) {
        preferences.put(key, valueToString(value));
    }

    public Object valueFromString(String value) {
        return configSupport.valueFromString(value);
    }

    public String valueToString(Object value) {
        return configSupport.valueToString(value);
    }

    private static final class ConfigFor {

        private final boolean adminConfig;

        private final boolean consumerConfig;

        private final boolean producerConfig;

        private static final ConfigFor ALL = new ConfigFor(true, true, true);

        private static final ConfigFor NONE = new ConfigFor(false, false, false);

        public ConfigFor(boolean adminConfig, boolean consumerConfig, boolean producerConfig) {
            this.adminConfig = adminConfig;
            this.consumerConfig = consumerConfig;
            this.producerConfig = producerConfig;
        }

        public boolean isAdminConfig() {
            return adminConfig;
        }

        public boolean isConsumerConfig() {
            return consumerConfig;
        }

        public boolean isProducerConfig() {
            return producerConfig;
        }
    }
}
