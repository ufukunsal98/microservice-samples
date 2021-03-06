package com.ufuk.accountprovider.Controller;

import com.ufuk.accountprovider.Entity.CustomUserDetail;
import com.ufuk.accountprovider.Entity.OAuthAccessTokens;
import com.ufuk.accountprovider.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@EnableResourceServer
@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;



    @PostMapping("user/retrive-token")
    public ResponseEntity<OAuth2AccessToken> retriveToken(HttpServletRequest request, HttpServletResponse response , @RequestBody OAuthAccessTokens authAccessTokens) throws Exception {
         return ResponseEntity.ok(accountService.retriveToken(request , response , authAccessTokens));
    }

    @PostMapping("user/me")
    public ResponseEntity<CustomUserDetail> getUserInfo(HttpServletRequest request, HttpServletResponse response , @RequestBody OAuthAccessTokens authAccessTokens) throws Exception {
        return ResponseEntity.ok(accountService.getUserInfo(request , response , authAccessTokens));
    }

    @PostMapping("/revoke-token")
    public ResponseEntity<OAuth2AccessToken> revokeToken(@RequestBody  OAuthAccessTokens oAuthAccessTokens) throws HttpRequestMethodNotSupportedException {
        return ResponseEntity.ok(accountService.revokeToken(oAuthAccessTokens));
    }


}
