package com.ufuk.accountprovider.Domain;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.List;
import java.util.Map;

public class CustomTokenGranter extends AbstractTokenGranter {

    public CustomTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                              OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> params = tokenRequest.getRequestParameters();
        String username = params.containsKey("username") ? params.get("username") : "guest";
        List<GrantedAuthority> authorities = params.containsKey("authorities") ? AuthorityUtils
                .createAuthorityList(OAuth2Utils.parseParameterList(params.get("authorities")).toArray(new String[0]))
                : AuthorityUtils.NO_AUTHORITIES;
        Authentication user = new UsernamePasswordAuthenticationToken(username, "N/A", authorities);
        OAuth2Authentication authentication = new OAuth2Authentication(tokenRequest.createOAuth2Request(client), user);
        return authentication;
    }
}
