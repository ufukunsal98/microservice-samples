package com.ufuk.accountprovider.Domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.util.Assert;

public class AbstractEndpoint implements InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private WebResponseExceptionTranslator providerExceptionHandler = new DefaultWebResponseExceptionTranslator();
    private TokenGranter tokenGranter;
    private ClientDetailsService clientDetailsService;
    private OAuth2RequestFactory oAuth2RequestFactory;
    private OAuth2RequestFactory defaultOAuth2RequestFactory;

    public AbstractEndpoint() {
    }

    public void afterPropertiesSet() throws Exception {
        //Assert.state(this.tokenGranter != null, "TokenGranter must be provided");
        //Assert.state(this.clientDetailsService != null, "ClientDetailsService must be provided");
        this.defaultOAuth2RequestFactory = new DefaultOAuth2RequestFactory(this.getClientDetailsService());
        if (this.oAuth2RequestFactory == null) {
            this.oAuth2RequestFactory = this.defaultOAuth2RequestFactory;
        }

    }

    public void setProviderExceptionHandler(WebResponseExceptionTranslator providerExceptionHandler) {
        this.providerExceptionHandler = providerExceptionHandler;
    }

    public void setTokenGranter(TokenGranter tokenGranter) {
        this.tokenGranter = tokenGranter;
    }

    protected TokenGranter getTokenGranter() {
        return this.tokenGranter;
    }

    protected WebResponseExceptionTranslator getExceptionTranslator() {
        return this.providerExceptionHandler;
    }

    protected OAuth2RequestFactory getOAuth2RequestFactory() {
        return this.oAuth2RequestFactory;
    }

    protected OAuth2RequestFactory getDefaultOAuth2RequestFactory() {
        return this.defaultOAuth2RequestFactory;
    }

    public void setOAuth2RequestFactory(OAuth2RequestFactory oAuth2RequestFactory) {
        this.oAuth2RequestFactory = oAuth2RequestFactory;
    }

    protected ClientDetailsService getClientDetailsService() {
        return this.clientDetailsService;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }
}
