package com.ufuk.accountprovider.Entity;

import com.ufuk.accountprovider.Util.SerializableObjectConverter;
import lombok.Data;
import lombok.Setter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Data
@Table(name = "TOKEN")
public class OAuthAccessTokens {

    @Id
    private String id;
    private String tokenId;

    private byte[]  token; //OAuth2AccessToken

    @Transient
    private String grantType;

    private String accessToken;

    private String authenticationId;

    private String username;

    private String clientId;

    private String clientSecret;

    private byte [] authentication;

    private String refreshToken;

    @Transient
    private String password;


    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }

    public void setToken(OAuth2AccessToken token) {
        this.token = SerializableObjectConverter.serializeAccessToken(token);
    }

    public OAuth2AccessToken getOAuthAccessToken() {
        return SerializableObjectConverter.deserializeAccessToken(this.token);
    }

}