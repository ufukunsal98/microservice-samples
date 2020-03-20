package com.ufuk.accountprovider.Controller;

import com.ufuk.accountprovider.Entity.OAuthAccessTokens;
import com.ufuk.accountprovider.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@EnableResourceServer
@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("user/me")
    public ResponseEntity<OAuth2AccessToken> userInfo(HttpServletRequest request, HttpServletResponse response ,
                                                      @RequestParam HashMap<String, String> parameters) throws Exception {
         return ResponseEntity.ok(accountService.authenticate(request , response , parameters));
    }
//
//    @PostMapping("/revoke-token")
//    public ResponseEntity<OAuth2AccessToken> revokeToken(@RequestBody  OAuthAccessTokens oAuthAccessTokens) {
//        return ResponseEntity.ok(accountService.revokeToken(oAuthAccessTokens));
//    }


}
