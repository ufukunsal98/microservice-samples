package com.ufuk.accountprovider;

import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenCookieCreationFilter extends OncePerRequestFilter {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "token";
    private final UserInfoRestTemplateFactory userInfoRestTemplateFactory;

    public TokenCookieCreationFilter(UserInfoRestTemplateFactory userInfoRestTemplateFactory) {
        this.userInfoRestTemplateFactory = userInfoRestTemplateFactory;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException, IOException, ServletException {
        try {
            final OAuth2ClientContext oAuth2ClientContext = userInfoRestTemplateFactory.getUserInfoRestTemplate().getOAuth2ClientContext();
            final OAuth2AccessToken authentication = oAuth2ClientContext.getAccessToken();
            if (authentication != null && authentication.getExpiresIn() > 0) {
                final Cookie cookieToken = createCookie(authentication.getValue(), authentication.getExpiresIn());
                response.addCookie(cookieToken);
            }
        } catch (final Exception e) {
        }
        filterChain.doFilter(request, response);
    }

    private Cookie createCookie(final String content, final int expirationTimeSeconds) {
        final Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, content);
        cookie.setMaxAge(expirationTimeSeconds);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}

/**
 * Adds the authentication information to the SecurityContext. Needed to allow access to restricted paths after a
 * successful authentication redirects back to the application. Without it, the filter
 * {@link org.springframework.security.web.authentication.AnonymousAuthenticationFilter} cannot find a user
 * and rejects access, redirecting to the login page again.
 */
public class SecurityContextRestorerFilter extends OncePerRequestFilter {

    private final UserInfoRestTemplateFactory userInfoRestTemplateFactory;
    private final ResourceServerTokenServices userInfoTokenServices;

    public SecurityContextRestorerFilter(UserInfoRestTemplateFactory userInfoRestTemplateFactory, ResourceServerTokenServices userInfoTokenServices) {
        this.userInfoRestTemplateFactory = userInfoRestTemplateFactory;
        this.userInfoTokenServices = userInfoTokenServices;
    }

    @Override
    public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        try {
            final OAuth2AccessToken authentication = userInfoRestTemplateFactory.getUserInfoRestTemplate().getOAuth2ClientContext().getAccessToken();
            if (authentication != null && authentication.getExpiresIn() > 0) {
                OAuth2Authentication oAuth2Authentication = userInfoTokenServices.loadAuthentication(authentication.getValue());
                SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);
            } else {
            }
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}