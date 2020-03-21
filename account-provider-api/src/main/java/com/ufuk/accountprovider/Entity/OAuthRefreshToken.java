package com.ufuk.accountprovider.Entity;

import com.ufuk.accountprovider.Util.SerializableObjectConverter;
import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
@Data
public class OAuthRefreshToken {

    @Id
    private String id;
    private String tokenId;

    private byte[] token; //OAuthRefreshToken
    private byte[] authentication;

    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }

    public OAuth2RefreshToken getRefreshToken() {
        return SerializableObjectConverter.deserializeRefreshToken(token);
    }


    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }

    public void setRefreshToken(OAuth2RefreshToken token) {
        this.token = SerializableObjectConverter.serializeRefreshToken(token);
    }

}