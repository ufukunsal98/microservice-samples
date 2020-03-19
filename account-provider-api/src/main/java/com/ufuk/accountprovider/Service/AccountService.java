package com.ufuk.accountprovider.Service;

import com.ufuk.accountprovider.Entity.OAuthAccessTokens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final TokenStore tokenStore;

    @Autowired
    public AccountService(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
//
//    getUserInformation(OAuthAccessTokens authAccessTokens) {
//        tokenStore.readAuthentication(authAccessTokens);,
//
//    }
}
