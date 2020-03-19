package com.ufuk.accountprovider.Util;

import org.apache.commons.codec.binary.Base64;
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

    public static OAuth2Authentication deserialize(byte[] encodedObject) {
        try {
            byte[] bytes = encodedObject;
            return (OAuth2Authentication) SerializationUtils.deserialize(bytes);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}