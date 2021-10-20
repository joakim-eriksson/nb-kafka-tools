package se.jocke.nb.kafka.gcp;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.openide.util.Exceptions;
import se.jocke.nb.kafka.preferences.KafkaPreferences;
import se.jocke.nb.kafka.preferences.ManagedAdminClientConfig;

public class GCPConnectionConfig {

    private static final String SECURITY_PROTOCOL = "security.protocol";
    private static final String SASL_TEMPLATE = "org.apache.kafka.common.security.scram.ScramLoginModule required username='%s' password='%s';";

    public static boolean isEnabled() {
        return KafkaPreferences.getBoolean(ManagedAdminClientConfig.GCP_ENABLED.getKey(), false);
    }

    public static Map<String, String> getConfig() {
        if (isEnabled()) {
            Map<String, String> configProps = new LinkedHashMap<>();
            configProps.put(SECURITY_PROTOCOL, "SASL_SSL");
            configProps.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "https");
            configProps.put("sasl.mechanism", "PLAIN");

            try {
                SecretManagerServiceSettings.Builder builder = SecretManagerServiceSettings.newBuilder();
                GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(Base64.getDecoder().decode(KafkaPreferences.get(ManagedAdminClientConfig.GCP_ENCODED_KEY.getKey()))));
                builder.setCredentialsProvider(FixedCredentialsProvider.create(credentials));
                try (SecretManagerServiceClient client = SecretManagerServiceClient.create(builder.build())) {
                    AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder().setName(KafkaPreferences.get(ManagedAdminClientConfig.GCP_SECRET_VERSION_REQUEST_NAME.getKey())).build();
                    AccessSecretVersionResponse response = client.accessSecretVersion(request);
                    String[] secrets = response.getPayload().getData().toStringUtf8().split("\n");
                    String namePass = String.format(SASL_TEMPLATE, secrets[0], secrets[1]);
                    configProps.put(SaslConfigs.SASL_JAAS_CONFIG, namePass);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                throw new RuntimeException(ex);
            }
            return configProps;
        } else {
            throw new IllegalStateException("GCP is not enabled");
        }
    }
}
