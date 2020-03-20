package com.ufuk.accountprovider.Service;
import com.ufuk.accountprovider.Entity.OAuthAccessTokens;
import com.ufuk.accountprovider.Entity.OAuthRefreshToken;
import com.ufuk.accountprovider.Repository.OAuthAccessTokenRepository;
import com.ufuk.accountprovider.Repository.OAuthRefreshTokenRepository;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class OAuthTokenStore implements TokenStore {

    private OAuthAccessTokenRepository oAuthAccessTokenRepository;

    private OAuthRefreshTokenRepository oAuthRefreshTokenRepository;

    public OAuthTokenStore(OAuthAccessTokenRepository oAuthAccessTokenRepository, OAuthRefreshTokenRepository oAuthRefreshTokenRepository){
        this.oAuthAccessTokenRepository = oAuthAccessTokenRepository;
        this.oAuthRefreshTokenRepository = oAuthRefreshTokenRepository;
    }

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken accessToken) {
        return readAuthentication(accessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        Optional<OAuthAccessTokens> accessToken = oAuthAccessTokenRepository.findByTokenId(extractTokenKey(token));
        if (accessToken.isPresent()) {
            return accessToken.get().getAuthentication();
        }
        return null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (accessToken.getRefreshToken() != null) {
            refreshToken = accessToken.getRefreshToken().getValue();
        }

        if (readAccessToken(accessToken.getValue()) != null) {
            this.removeAccessToken(accessToken);
        }

        OAuthAccessTokens cat =  new OAuthAccessTokens();
        cat.setId(UUID.randomUUID().toString()+UUID.randomUUID().toString());
        cat.setTokenId(extractTokenKey(accessToken.getValue()));
        cat.setToken(accessToken);
        cat.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        cat.setUsername(authentication.isClientOnly() ? null : authentication.getName());
        cat.setClientId(authentication.getOAuth2Request().getClientId());
        cat.setAuthentication(authentication);
        cat.setRefreshToken(refreshToken);
        cat.setAccessToken(accessToken.getValue());

        oAuthAccessTokenRepository.save(cat);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        Optional<OAuthAccessTokens> accessToken = oAuthAccessTokenRepository.findByTokenId(extractTokenKey(tokenValue));
        if (accessToken.isPresent()) {
            return accessToken.get().getToken();
        }
        return null;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        Optional<OAuthAccessTokens> accessToken = oAuthAccessTokenRepository.findByTokenId(extractTokenKey(oAuth2AccessToken.getValue()));
        if (accessToken.isPresent()) {
            oAuthAccessTokenRepository.delete(accessToken.get());
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        OAuthRefreshToken crt = new OAuthRefreshToken();
        crt.setId(UUID.randomUUID().toString()+UUID.randomUUID().toString());
        crt.setTokenId(refreshToken.getValue());
        crt.setToken(refreshToken);
        crt.setAuthentication(authentication);
        oAuthRefreshTokenRepository.save(crt);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        Optional<OAuthRefreshToken> refreshToken = oAuthRefreshTokenRepository.findByTokenId(extractTokenKey(tokenValue));
        return refreshToken.isPresent()? refreshToken.get().getToken() :null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken refreshToken) {
        Optional<OAuthRefreshToken> rtk = oAuthRefreshTokenRepository.findByTokenId(extractTokenKey(refreshToken.getValue()));
        return rtk.isPresent()? rtk.get().getAuthentication() :null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        Optional<OAuthRefreshToken> rtk = oAuthRefreshTokenRepository.findByTokenId(extractTokenKey(refreshToken.getValue()));
        if (rtk.isPresent()) {
            oAuthRefreshTokenRepository.delete(rtk.get());
        }
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        Optional<OAuthAccessTokens> token = oAuthAccessTokenRepository.findByRefreshToken(extractTokenKey(refreshToken.getValue()));
        if(token.isPresent()){
            oAuthAccessTokenRepository.delete(token.get());
        }
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;
        String authenticationId = authenticationKeyGenerator.extractKey(authentication);
        Optional<OAuthAccessTokens> token = oAuthAccessTokenRepository.findByAuthenticationId(authenticationId);

        if(token.isPresent()) {
            accessToken = token.get().getToken();
            if(accessToken != null && !authenticationId.equals(this.authenticationKeyGenerator.extractKey(this.readAuthentication(accessToken)))) {
                this.removeAccessToken(accessToken);
                this.storeAccessToken(accessToken, authentication);
            }
        }
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        Collection<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        List<OAuthAccessTokens> result = oAuthAccessTokenRepository.findByClientIdAndUsername(clientId, userName);
        result.forEach(e-> tokens.add(e.getToken()));
        return tokens;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        Collection<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        List<OAuthAccessTokens> result = oAuthAccessTokenRepository.findByClientId(clientId);
        result.forEach(e-> tokens.add(e.getToken()));
        return tokens;
    }

    private String extractTokenKey(String value) {
        if(value == null) {
            return null;
        } else {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
            }

            try {
                byte[] e = digest.digest(value.getBytes("UTF-8"));
                return String.format("%032x", new Object[]{new BigInteger(1, e)});
            } catch (UnsupportedEncodingException var4) {
                throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
            }
        }
    }
}