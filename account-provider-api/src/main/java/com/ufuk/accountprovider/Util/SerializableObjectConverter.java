package com.ufuk.accountprovider.Util;

import com.ufuk.accountprovider.Entity.OAuthRefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public  class SerializableObjectConverter {

    public static byte [] serialize(OAuth2Authentication object) {
        try {
            byte[] bytes = SerializationUtils.serialize(object);
            //return Base64.encodeBase64String(bytes);
            return bytes;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static byte [] serializeRefreshToken(OAuth2RefreshToken object) {
        try {
            byte[] bytes = SerializationUtils.serialize(object);
            //return Base64.encodeBase64String(bytes);
            return bytes;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static OAuth2Authentication deserialize(byte[] encodedObject) {
        try {
            byte[] bytes = encodedObject;
            return (OAuth2Authentication) SerializationUtils.deserialize(bytes);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static OAuth2RefreshToken deserializeRefreshToken(byte[] encodedObject) {
        try {
            byte[] bytes = encodedObject;
            return (OAuth2RefreshToken) SerializationUtils.deserialize(bytes);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static byte[] serializeAccessToken(OAuth2AccessToken object) {
        try {
            byte[] bytes = SerializationUtils.serialize(object);
            //return Base64.encodeBase64String(bytes);
            return bytes;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static OAuth2AccessToken deserializeAccessToken(byte[] encodedObject) {
        try {
            byte[] bytes = encodedObject;
            return (OAuth2AccessToken) SerializationUtils.deserialize(bytes);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}