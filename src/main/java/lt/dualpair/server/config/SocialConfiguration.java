package lt.dualpair.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.connect.VKontakteConnectionFactory;

@Configuration
public class SocialConfiguration {

    @Configuration
    @EnableSocial
    @EnableConfigurationProperties(VKontakteProperties.class)
    protected static class VKontakteConfigurerAdapter extends SocialConfigurerAdapter {

        @Autowired
        private VKontakteProperties properties;

        @Bean
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public VKontakte vKontakte(ConnectionRepository repository) {
            Connection<VKontakte> connection = repository
                    .findPrimaryConnection(VKontakte.class);
            return connection != null ? connection.getApi() : null;
        }

        @Bean(name = { "connect/vkontakteConnect", "connect/vkontakteConnected" })
        public GenericConnectionStatusView vkontakteConnectView() {
            return new GenericConnectionStatusView("vkontakte", "VKontakte");
        }

        @Override
        public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
            connectionFactoryConfigurer.addConnectionFactory(new VKontakteConnectionFactory(this.properties.getAppId(),
                    this.properties.getAppSecret()));
        }

    }

    @ConfigurationProperties("spring.social.vkontakte")
    public static class VKontakteProperties {

        private String appId;

        private String appSecret;

        public String getAppId() {
            return this.appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return this.appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

    }
}
