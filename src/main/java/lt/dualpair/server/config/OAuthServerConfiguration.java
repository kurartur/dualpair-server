package lt.dualpair.server.config;

import lt.dualpair.server.infrastructure.authentication.SocialTokenGranter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.social.security.SocialAuthenticationServiceLocator;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
public class OAuthServerConfiguration {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        private static final String RESOURCE_ID = "dualpair_rest";

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(RESOURCE_ID);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers().antMatchers("/api/**").antMatchers("/connect/**").and()
                    .authorizeRequests().antMatchers("/**").authenticated();
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Configuration
        protected static class TokenStoreConfiguration {

            @Autowired
            protected DataSource dataSource;

            @Bean(name = "tokenStore")
            @Profile("!it")
            public TokenStore jdbcTokenStore(DataSource dataSource) {
                return new JdbcTokenStore(dataSource);
            }

            @Bean(name = "tokenStore")
            @Profile("it")
            public TokenStore inMemoryTokenStore() {
                return new InMemoryTokenStore();
            }

        }

        @Autowired
        protected TokenStore tokenStore;

        @Autowired
        @Qualifier("authenticationManagerBean")
        protected AuthenticationManager authenticationManager;

        @Autowired
        protected SocialAuthenticationServiceLocator authServiceLocator;

        @Autowired
        protected UserDetailsService userService;

        @Autowired
        protected ClientDetailsService clientDetailsService;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            CompositeTokenGranter tokenGranter = new CompositeTokenGranter(Arrays.asList(endpoints.getTokenGranter()));
            tokenGranter.addTokenGranter(new SocialTokenGranter(authenticationManager, authServiceLocator,
                    tokenServices(), clientDetailsService, new DefaultOAuth2RequestFactory(clientDetailsService)));

            endpoints
                    .tokenGranter(tokenGranter)
                    .tokenStore(tokenStore)
                    .tokenServices(tokenServices())
                    .authenticationManager(authenticationManager)
                    .userDetailsService(userService);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .inMemory()
                        .withClient("dualpairandroid")
                            .authorizedGrantTypes("social", "refresh_token")
                            .secret("secret")
                            .scopes("trust")
                            .autoApprove(true);
        }

        @Bean
        @Primary
        @Qualifier("defaultTokenServices")
        public AuthorizationServerTokenServices tokenServices() {
            DefaultTokenServices tokenServices = new DefaultTokenServices();
            tokenServices.setSupportRefreshToken(true);
            tokenServices.setTokenStore(tokenStore);
            return tokenServices;
        }

    }

}
