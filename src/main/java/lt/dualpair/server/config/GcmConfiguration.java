package lt.dualpair.server.config;

import com.google.android.gcm.server.Sender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GcmConfiguration {

    @Bean
    public Sender sender(@Value("${googleApiKey}") String googleApiKey) {
        return new Sender(googleApiKey);
    }

}
