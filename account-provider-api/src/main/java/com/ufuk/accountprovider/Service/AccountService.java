package com.ufuk.accountprovider.Service;

import com.ufuk.accountprovider.Domain.AbstractEndpoint;
import com.ufuk.accountprovider.Domain.CustomTokenGranter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Service
public class AccountService  extends AbstractEndpoint {

    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();
    private Set<HttpMethod> allowedRequestMethods;

    public AccountService() {
        this.allowedRequestMethods = new HashSet(Arrays.asList(HttpMethod.POST));
        setTokenGranter(new ClientCredentialsTokenGranter());
        setClientDetailsService(new ClientDetailsService() {
            @Override
            public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
                return null;
            }
        });
    }


    public ResponseEntity<OAuth2AccessToken> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        if (!this.allowedRequestMethods.contains(HttpMethod.GET)) {
            throw new HttpRequestMethodNotSupportedException("GET");
        } else {
            return this.postAccessToken(principal, parameters);
        }
    }

    public ResponseEntity<OAuth2AccessToken> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        if (!(principal instanceof Authentication)) {
            throw new InsufficientAuthenticationException("There is no client authentication. Try adding an appropriate authentication filter.");
        } else {
            String clientId = this.getClientId(principal);
            ClientDetails authenticatedClient = this.getClientDetailsService().loadClientByClientId(clientId);
            TokenRequest tokenRequest = this.getOAuth2RequestFactory().createTokenRequest(parameters, authenticatedClient);
            if (clientId != null && !clientId.equals("") && !clientId.equals(tokenRequest.getClientId())) {
                throw new InvalidClientException("Given client ID does not match authenticated client");
            } else {
                if (authenticatedClient != null) {
                    this.oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
                }

                if (!StringUtils.hasText(tokenRequest.getGrantType())) {
                    throw new InvalidRequestException("Missing grant type");
                } else if (tokenRequest.getGrantType().equals("implicit")) {
                    throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
                } else {
                    if (this.isAuthCodeRequest(parameters) && !tokenRequest.getScope().isEmpty()) {
                        this.logger.debug("Clearing scope of incoming token request");
                        tokenRequest.setScope(Collections.emptySet());
                    }

                    if (this.isRefreshTokenRequest(parameters)) {
                        tokenRequest.setScope(OAuth2Utils.parseParameterList((String)parameters.get("scope")));
                    }

                    OAuth2AccessToken token = this.getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
                    if (token == null) {
                        throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
                    } else {
                        return this.getResponse(token);
                    }
                }
            }
        }
    }

    protected String getClientId(Principal principal) {
        Authentication client = (Authentication)principal;
        if (!client.isAuthenticated()) {
            throw new InsufficientAuthenticationException("The client is not authenticated.");
        } else {
            String clientId = client.getName();
            if (client instanceof OAuth2Authentication) {
                clientId = ((OAuth2Authentication)client).getOAuth2Request().getClientId();
            }

            return clientId;
        }
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

    private ResponseEntity<OAuth2AccessToken> getResponse(OAuth2AccessToken accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        return new ResponseEntity(accessToken, headers, HttpStatus.OK);
    }

    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
    }

    private boolean isAuthCodeRequest(Map<String, String> parameters) {
        return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
    }

    public void setOAuth2RequestValidator(OAuth2RequestValidator oAuth2RequestValidator) {
        this.oAuth2RequestValidator = oAuth2RequestValidator;
    }

    public void setAllowedRequestMethods(Set<HttpMethod> allowedRequestMethods) {
        this.allowedRequestMethods = allowedRequestMethods;
    }
}
