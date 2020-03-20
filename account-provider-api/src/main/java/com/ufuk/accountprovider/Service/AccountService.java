package com.ufuk.accountprovider.Service;

import com.ufuk.accountprovider.Entity.CustomUserDetail;
import com.ufuk.accountprovider.Entity.OAuthAccessTokens;
import com.ufuk.accountprovider.Repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
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
import java.util.*;

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
    private DefaultTokenServices defaultTokenServices;

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



    public OAuth2AccessToken authenticate(HttpServletRequest request, HttpServletResponse response,
                                          HashMap<String, String> parameters) throws Exception {
        UserDetails userDetails = null;
        String clientId = parameters.get("client_id");
        ClientDetails authenticatedClient = customClientDetailsService.loadClientByClientId(clientId);


        if (!StringUtils.hasText(parameters.get("grant_type"))) {
            throw new InvalidRequestException("Missing grant type");
        } else if (parameters.get("grant_type").equals("implicit")) {
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

        HashMap<String, String> authorizationParameters = parameters;
        ClientDetails clientDetails = customClientDetailsService.loadClientByClientId(authorizationParameters.get("client_id"));
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Set<String> responseType = new HashSet<String>();
        responseType.add("password");


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

        OAuth2AccessToken auth2AccessToken = defaultTokenServices.createAccessToken(authenticationRequest);
        cookieService.retriveTokenSetCookie(request, response, auth2AccessToken);
        return auth2AccessToken;
    }

    public OAuth2AccessToken revokeToken(OAuthAccessTokens oAuthAccessTokens,
                                         HashMap<String, String> parameters) throws HttpRequestMethodNotSupportedException {


        HashMap<String, String> authorizationParameters = parameters;
        ClientDetails clientDetails = customClientDetailsService.loadClientByClientId(authorizationParameters.get("client_id"));

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Set<String> responseType = new HashSet<String>();
        responseType.add("password");


        OAuth2Request authorizationRequest = new OAuth2Request(
                authorizationParameters, clientDetails.getClientId(),
                authorities, true, clientDetails.getScope(), clientDetails.getResourceIds(), "",
                responseType, null);

        UserDetails userDetails = userDetailsService.loadUserByUsername("asdasd");
        User userPrincipal = new User(userDetails.getUsername(), userDetails.getPassword(), new ArrayList<>());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, authorities);

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(
                authorizationRequest, authenticationToken);
        authenticationRequest.setAuthenticated(true);

        defaultTokenServices.revokeToken(oAuthAccessTokens.getAccessToken());
        return null;
    }

    protected WebResponseExceptionTranslator getExceptionTranslator() {
        return this.providerExceptionHandler;
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

