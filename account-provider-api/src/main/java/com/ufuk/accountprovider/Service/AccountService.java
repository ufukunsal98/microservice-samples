package com.ufuk.accountprovider.Service;

import com.ufuk.accountprovider.Entity.CustomUserDetail;
import com.ufuk.accountprovider.Entity.OAuthAccessTokens;
import com.ufuk.accountprovider.Entity.Users;
import com.ufuk.accountprovider.Repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
@RestControllerAdvice
public class AccountService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private CustomDefaultTokenServices defaultTokenServices;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CustomClientDetailsService customClientDetailsService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CookieService cookieService;

    protected final Log logger = LogFactory.getLog(this.getClass());

    private WebResponseExceptionTranslator providerExceptionHandler = new DefaultWebResponseExceptionTranslator();



    public OAuth2AccessToken retriveToken(HttpServletRequest request, HttpServletResponse response,
                                          OAuthAccessTokens authAccessTokens) throws Exception {
        UserDetails userDetails = null;
        String clientId = authAccessTokens.getClientId();
        ClientDetails authenticatedClient = customClientDetailsService.loadClientByClientId(clientId);
        if (!StringUtils.hasText(authAccessTokens.getGrantType())) {
            throw new InvalidRequestException("Missing grant type");
        } else if (authAccessTokens.getGrantType().equals("implicit")) {
            throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
//                } else {
//                    if (this.isAuthCodeRequest(parameters) && !tokenRequest.getScope().isEmpty()) {
//                        this.logger.debug("Clearing scope of incoming token request");
//                        tokenRequest.setScope(Collections.emptySet());
//                    }
//
//                    if (this.isRefreshTokenRequest(parameters)) {
//                        tokenRequest.setScope(OAuth2Utils.parseParameterList((String)parameters.get("scope")));
//                    }
//
//                    OAuth2AccessToken token = this.getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
//                    if (token == null) {
//                        throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
//                    } else {
//                        return this.getResponse(token);
//                    }
//                }
        }

        ClientDetails clientDetails = customClientDetailsService.loadClientByClientId(authAccessTokens.getClientId());

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Set<String> responseType = new HashSet<String>();
        responseType.add("password");
        HashMap<String , String> authorizationParameters = setAuthorizationParameters(authAccessTokens);


        OAuth2Request authorizationRequest = new OAuth2Request(
                authorizationParameters, clientDetails.getClientId(),
                authorities, true, clientDetails.getScope(), clientDetails.getResourceIds(), "",
                responseType, null);

        try {
            userDetails = userDetailsService.loadUserByUsername(authAccessTokens.getUsername());
        } catch (UsernameNotFoundException exception) {
            throw new InvalidGrantException("Bad credentials");
        }


        User userPrincipal = new User(userDetails.getUsername(), userDetails.getPassword(), new ArrayList<>());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, authorities);

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(
                authorizationRequest, authenticationToken);
        authenticationRequest.setAuthenticated(true);
        setValiditySeconds(clientDetails);
        OAuth2AccessToken auth2AccessToken = defaultTokenServices.createAccessToken(authenticationRequest);
        cookieService.retriveTokenSetCookie(request, response, auth2AccessToken);
        return auth2AccessToken;
    }

    private void setValiditySeconds(ClientDetails clientDetails) {
        defaultTokenServices.setAccessTokenValiditySeconds(clientDetails.getAccessTokenValiditySeconds());
        defaultTokenServices.setRefreshTokenValiditySeconds(clientDetails.getRefreshTokenValiditySeconds());
    }

    public OAuth2AccessToken revokeToken(OAuthAccessTokens oAuthAccessTokens) throws HttpRequestMethodNotSupportedException {


        OAuth2AccessToken auth2AccessToken = defaultTokenServices.readAccessToken(oAuthAccessTokens.getAccessToken());

        defaultTokenServices.revokeToken(oAuthAccessTokens.getAccessToken());

        return auth2AccessToken;
    }

    public OAuth2AccessToken refreshToken(OAuthAccessTokens oAuthAccessTokens,
                                         HashMap<String, String> parameters) throws HttpRequestMethodNotSupportedException {

        HashMap<String, String> authorizationParameters = parameters;
        ClientDetails clientDetails = customClientDetailsService.loadClientByClientId(authorizationParameters.get("client_id"));
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Set<String> responseType = new HashSet<String>();
        responseType.add("password");
        UserDetails userDetails = null;
        TokenRequest tokenRequest = new TokenRequest(authorizationParameters ,
                clientDetails.getClientId() , clientDetails.getScope() ,  "refresh_token");
        setValiditySeconds(clientDetails);
        OAuth2Request authorizationRequest = new OAuth2Request(
                authorizationParameters, clientDetails.getClientId(),
                authorities, true, clientDetails.getScope(), clientDetails.getResourceIds(), "",
                responseType, null);

        try {
            userDetails = userDetailsService.loadUserByUsername(parameters.get("username"));
        } catch (UsernameNotFoundException exception) {
            throw new InvalidGrantException("Bad credentials");
        }


        User userPrincipal = new User(userDetails.getUsername(), userDetails.getPassword(), new ArrayList<>());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, authorities);

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(
                authorizationRequest, authenticationToken);
        authenticationRequest.setAuthenticated(true);
        OAuth2AccessToken oAuth2AccessToken = defaultTokenServices.refreshAccessToken(oAuthAccessTokens.getAccessToken() , tokenRequest);

        return oAuth2AccessToken;
    }

    public CustomUserDetail getUserInfo(HttpServletRequest request, HttpServletResponse response, OAuthAccessTokens authAccessTokens) {
        UserDetails userDetails = (UserDetails) defaultTokenServices.loadAuthentication(authAccessTokens.getAccessToken()).getUserAuthentication().getPrincipal();
        CustomUserDetail customUserDetail = new CustomUserDetail(userRepository.findByUsername(userDetails.getUsername()).get(0) ,  null);
        return  customUserDetail;
    }

    protected WebResponseExceptionTranslator getExceptionTranslator() {
        return this.providerExceptionHandler;
    }


    public HashMap<String , String> setAuthorizationParameters(OAuthAccessTokens authAccessTokens) {
        HashMap<String, String> authorizationParameters = new HashMap<>();
        authorizationParameters.put("grant_type" , authAccessTokens.getGrantType());
        authorizationParameters.put("client_id" ,  authAccessTokens.getClientId());
        authorizationParameters.put("client_secret" ,  authAccessTokens.getClientSecret());
        authorizationParameters.put("username" ,   authAccessTokens.getUsername());
        return authorizationParameters;
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<OAuth2Exception> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) throws Exception {
        this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return this.getExceptionTranslator().translate(e);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return this.getExceptionTranslator().translate(e);
    }

    @ExceptionHandler({ClientRegistrationException.class})
    public ResponseEntity<OAuth2Exception> handleClientRegistrationException(Exception e) throws Exception {
        this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return this.getExceptionTranslator().translate(new BadClientCredentialsException());
    }

    @ExceptionHandler({OAuth2Exception.class})
    public ResponseEntity<OAuth2Exception> handleException(OAuth2Exception e) throws Exception {
        this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return this.getExceptionTranslator().translate(e);
    }



}

