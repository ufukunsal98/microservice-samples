package com.ufuk.accountprovider.Controller;

import com.ufuk.accountprovider.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@FrameworkEndpoint
@EnableResourceServer
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(
            value = {"user/me"},
            method = {RequestMethod.POST}
    )
    public ResponseEntity<OAuth2AccessToken> userInfo(HttpServletRequest request, HttpServletResponse response , @RequestParam
            Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
         accountService.authenticate(request , response , parameters);
         return null;
    }


}
