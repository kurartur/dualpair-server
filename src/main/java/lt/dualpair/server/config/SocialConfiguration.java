package lt.dualpair.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.*;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.security.SocialAuthenticationProvider;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.connect.VKontakteConnectionFactory;

import javax.sql.DataSource;

@Configuration
@EnableSocial
public class SocialConfiguration {

    @Bean
    @Autowired
    public SocialAuthenticationProvider socialAuthenticationProvider(SocialUserDetailsService socialUserDetailsService,
                                                                     UsersConnectionRepository usersConnectionRepository) {
        return new SocialAuthenticationProvider(usersConnectionRepository, socialUserDetailsService);
    }

    @Configuration
    @Profile("!it")
    protected static class JdbcUsersConnectionRepositoryConfigurerAdapter extends SocialConfigurerAdapter {

        @Autowired
        protected DataSource dataSource;

        @Autowired
        protected ConnectionSignUp connectionSignUp;

        @Override
        public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
            JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText()); // TODO text encryptor
            repository.setConnectionSignUp(connectionSignUp);
            return repository;
        }

    }

    @Configuration
    @Profile("it")
    protected static class InMemoryUsersConnectionRepositoryConfigurerAdapter extends SocialConfigurerAdapter {

        @Override
        public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
            return new InMemoryUsersConnectionRepository(connectionFactoryLocator);
        }
    }


    @Configuration
    @EnableConfigurationProperties(VKontakteConfigurerAdapter.VKontakteProperties.class)
    protected static class VKontakteConfigurerAdapter extends SocialConfigurerAdapter {

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

}
