package se.jocke.nb.kafka.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import static java.util.stream.Collectors.toMap;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

public enum ClientConnectionConfig {
    BOOTSTRAP_SERVERS(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "A list of host/port pairs to use for establishing the initial connection to the Kafka cluster"),
    SSL_KEY_PASSWORD(SslConfigs.SSL_KEY_PASSWORD_CONFIG, SslConfigs.SSL_KEY_PASSWORD_DOC),
    SSL_KEYSTORE_CERTIFICATE_CHAIN(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG, SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_DOC),
    SSL_KEYSTORE_KEY(SslConfigs.SSL_KEYSTORE_KEY_CONFIG, SslConfigs.SSL_KEYSTORE_KEY_DOC),
    SSL_KEYSTORE_LOCATION(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, SslConfigs.SSL_KEYSTORE_LOCATION_DOC),
    SSL_KEYSTORE_PASSWORD(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, SslConfigs.SSL_KEYSTORE_PASSWORD_DOC),
    SSL_TRUSTSTORE_CERTIFICATES(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_DOC),
    SSL_TRUSTSTORE_LOCATION(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, SslConfigs.SSL_TRUSTSTORE_LOCATION_DOC),
    SSL_TRUSTSTORE_PASSWORD(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, SslConfigs.SSL_TRUSTSTORE_PASSWORD_DOC),
    SSL_ENDPOINT_IDENTIFICATION_ALGORITHM(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_DOC),
    CLIENT_DNS_LOOKUP(AdminClientConfig.CLIENT_DNS_LOOKUP_CONFIG, "Controls how the client uses DNS lookups."),
    CLIENT_ID(AdminClientConfig.CLIENT_ID_CONFIG, "An id string to pass to the server when making requests."),
    CONNECTIONS_MAX_IDLE_MS(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, "Close idle connections after the number of milliseconds specified by this config.", Long.class, true, true, true),
    DEFAULT_API_TIMEOUT_MS(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "Specifies the timeout (in milliseconds)", Long.class, true, true, true),
    SASL_CLIENT_CALLBACK_HANDLER_CLASS(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS_DOC),
    SASL_JAAS_CONFIG(SaslConfigs.SASL_JAAS_CONFIG, SaslConfigs.SASL_JAAS_CONFIG_DOC),
    SASL_JAAS_CONFIG_TEMPLATE(SaslConfigs.SASL_JAAS_CONFIG + ".template", "Template to use if credentials are fetched externaly for JAAS Config ex, org.apache.kafka.common.security.scram.ScramLoginModule required username='%s' password='%s';"),
    SASL_KERBEROS_SERVICE_NAME(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, SaslConfigs.SASL_KERBEROS_SERVICE_NAME_DOC),
    SASL_LOGIN_CALLBACK_HANDLER_CLASS(SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS, SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS_DOC),
    SASL_LOGIN_CLASS(SaslConfigs.SASL_LOGIN_CLASS, SaslConfigs.SASL_LOGIN_CLASS_DOC),
    SASL_MECHANISM(SaslConfigs.SASL_MECHANISM, SaslConfigs.SASL_MECHANISM_DOC),
    SECURITY_PROTOCOL(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "Protocol used to communicate with brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL."),
    GCP_SECRET_ENABLED("gcp.secret.enabled", "If name and password is a GCP secret", Boolean.class, false, false, false),
    GCP_SECRET_VERSION_REQUEST_NAME("gcp.secret.version.request.name", "Secret name", String.class, false, false, false),
    GCP_SECRET_ENCODED_KEY("gcp.encoded.key", "Secret key", String.class, false, false, false),
    SAVED_TOPICS("saved.topics", "Topics saved", Set.class, false, false, false),
    LIST_TOPICS_DISABLED("list.topics.disabled", "Is connecting to kafka and get a list of topics possible, all users may not be authorized to do so", Boolean.class, false, false, false);

    private final String key;

    private final boolean adminConfig;

    private final boolean consumerConfig;

    private final boolean producerConfig;

    private final String desc;

    private final Class<?> propertyType;

    private static final Map<String, ClientConnectionConfig> KEY_CONFIG_MAP = Map.copyOf(
            Arrays.stream(values())
                    .map(c -> new AbstractMap.SimpleEntry<>(c.getKey(), c))
                    .collect(toMap(Entry::getKey, Entry::getValue))
    );

    private static final Map<Class<?>, Function<String, Object>> CONVERTERS = Map.of(
            Boolean.class, Boolean::parseBoolean,
            String.class, String::toString,
            Double.class, Double::valueOf,
            Long.class, Long::parseLong,
            Integer.class, Integer::parseInt,
            Set.class, s -> Sets.newLinkedHashSet(Splitter.on(",").omitEmptyStrings().split(s))
    );

    private ClientConnectionConfig(String key, String desc, Class<?> propertyType, boolean adminConfig, boolean consumerConfig, boolean producerConfig) {
        this.key = key;
        this.adminConfig = adminConfig;
        this.consumerConfig = consumerConfig;
        this.producerConfig = producerConfig;
        this.desc = desc;
        this.propertyType = propertyType;
    }

    private ClientConnectionConfig(String key, String desc) {
        this(key, desc, String.class, true, true, true);
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
        return propertyType;
    }

    public static ClientConnectionConfig ofKey(String key) {
        return KEY_CONFIG_MAP.get(key);
    }

    public Object valueFromString(String value) {
        if (!CONVERTERS.containsKey(this.propertyType)) {
            throw new IllegalArgumentException("No converter found for " + propertyType);
        }
        return CONVERTERS.get(this.propertyType).apply(value);
    }
}
