package se.jocke.nb.kafka.config.gcp;

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
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;
import se.jocke.nb.kafka.config.ClientConnectionConfig;
import se.jocke.nb.kafka.config.ClientConnectionConfigMapper;
import se.jocke.nb.kafka.nodes.root.KafkaServiceKey;

@ServiceProvider(service = ClientConnectionConfigMapper.class)
public class GCPClientConnectionConfigMapper implements ClientConnectionConfigMapper {

    @Override
    public Map<String, Object> map(KafkaServiceKey key, Map<String, Object> config) {
        if (isEnabled(key)) {
            Map<String, Object> newConfig = new LinkedHashMap<>(config);
            try {
                SecretManagerServiceSettings.Builder builder = SecretManagerServiceSettings.newBuilder();
                ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(NBKafkaPreferences.getString(key, ClientConnectionConfig.GCP_SECRET_ENCODED_KEY)));
                GoogleCredentials credentials = GoogleCredentials.fromStream(bais);
                builder.setCredentialsProvider(FixedCredentialsProvider.create(credentials));
                try ( SecretManagerServiceClient client = SecretManagerServiceClient.create(builder.build())) {
                    AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                            .setName(NBKafkaPreferences.getString(key, ClientConnectionConfig.GCP_SECRET_VERSION_REQUEST_NAME))
                            .build();
                    AccessSecretVersionResponse response = client.accessSecretVersion(request);
                    String[] secrets = response.getPayload().getData().toStringUtf8().split("\n");
                    String jasConfig = String.format(NBKafkaPreferences.getString(key, ClientConnectionConfig.SASL_JAAS_CONFIG_TEMPLATE), secrets[0], secrets[1]);
                    newConfig.put(ClientConnectionConfig.SASL_JAAS_CONFIG.getKey(), jasConfig);
                }

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            return newConfig;
        }

        return config;
    }

    public boolean isEnabled(KafkaServiceKey key) {
        return NBKafkaPreferences.getBoolean(key, ClientConnectionConfig.GCP_SECRET_ENABLED);
    }
}
