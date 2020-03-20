package com.ufuk.accountprovider.Entity;

import com.ufuk.accountprovider.Util.SerializableObjectConverter;
import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.persistence.*;

@Entity
@Data
@Table(name = "TOKEN")
public class OAuthAccessTokens {

    @Id
    private String id;
    private String tokenId;
    @Transient
    private OAuth2AccessToken token;
    private String accessToken;
    private String authenticationId;
    private String username;
    private String clientId;
    private byte [] authentication;
    private String refreshToken;


    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }

}