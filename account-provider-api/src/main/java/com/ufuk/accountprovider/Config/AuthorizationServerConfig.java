package com.ufuk.accountprovider.Config;

import com.ufuk.accountprovider.Service.CustomDefaultTokenServices;
import com.ufuk.accountprovider.Service.CustomUserDetailsService;
import com.ufuk.accountprovider.Util.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;


import java.util.Map;


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    static final String CLIENT_ID = "android-client";
    static final String CLIENT_SECRET = "android-secret";
    static final String GRANT_TYPE_PASSWORD = "password";
    static final String AUTHORIZATION_CODE = "authorization_code";
    static final String REFRESH_TOKEN = "refresh_token";
    static final String IMPLICIT = "implicit";
    static final String SCOPE_READ = "read";
    static final String SCOPE_WRITE = "write";
    static final String TRUST = "trust";
    static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1*60*60;
    static final int REFRESH_TOKEN_VALIDITY_SECONDS = 6*60*60;


    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    Interceptor requestInterceptor;



    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {

//        configurer
//                .inMemory()
//                .withClient(CLIENT_ID)
//                .secret(CLIENT_SECRET)
//                .authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT )
//                .scopes(SCOPE_READ, SCOPE_WRITE, TRUST)
//                .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS).
//                refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
        configurer.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .addInterceptor(requestInterceptor)
                .authenticationManager(authenticationManager);
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }



    @Bean
    public OAuth2RequestFactory requestFactory() {
        CustomOauth2RequestFactory requestFactory = new CustomOauth2RequestFactory(clientDetailsService);
        requestFactory.setCheckUserScopes(true);
        return requestFactory;
    }

    class CustomOauth2RequestFactory extends DefaultOAuth2RequestFactory {
        @Autowired
        private TokenStore tokenStore;

        public CustomOauth2RequestFactory(ClientDetailsService clientDetailsService) {
            super(clientDetailsService);
        }

        @Override
        public TokenRequest createTokenRequest(Map<String, String> requestParameters,
                                               ClientDetails authenticatedClient) {
            if (requestParameters.get("grant_type").equals("refresh_token")) {
                OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(
                        tokenStore.readRefreshToken(requestParameters.get("refresh_token")));
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(authentication.getName(), null,
                                userDetailsService.loadUserByUsername(authentication.getName()).getAuthorities()));
            }
            return super.createTokenRequest(requestParameters, authenticatedClient);
        }
    }

    @Bean
    @Primary
    public CustomDefaultTokenServices tokenServices() {
        CustomDefaultTokenServices defaultTokenServices = new CustomDefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

}
