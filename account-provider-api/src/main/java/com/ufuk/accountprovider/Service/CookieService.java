package com.ufuk.accountprovider.Service;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class CookieService {

    String BEARER_TYPE = "Bearer";
    String OAUTH2_TYPE = "OAuth2";
    String ACCESS_TOKEN = "access_token";
    String TOKEN_TYPE = "token_type";
    String EXPIRES_IN = "expires_in";
    String REFRESH_TOKEN = "refresh_token";
    String SCOPE = "scope";


    public HttpServletResponse retriveTokenSetCookie(HttpServletRequest request, HttpServletResponse response, OAuth2AccessToken auth2AccessToken) {
        setAccessToken(request, response, auth2AccessToken);
        setRefreshToken(request, response, auth2AccessToken);
        setExpiredDate(request, response, auth2AccessToken);
        return response;
    }


    private void setExpiredDate(HttpServletRequest request, HttpServletResponse response, OAuth2AccessToken auth2AccessToken) {
        Cookie cookie = new Cookie(ACCESS_TOKEN, auth2AccessToken.getValue());
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    private void setRefreshToken(HttpServletRequest request, HttpServletResponse response, OAuth2AccessToken auth2AccessToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, auth2AccessToken.getRefreshToken().getValue());
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }


    public void setAccessToken(HttpServletRequest request, HttpServletResponse response, OAuth2AccessToken auth2AccessToken) {
        Cookie cookie = new Cookie(EXPIRES_IN, "asdasd");
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
