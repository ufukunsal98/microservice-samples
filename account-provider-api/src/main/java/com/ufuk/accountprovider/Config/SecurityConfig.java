package com.ufuk.accountprovider.Config;

import com.ufuk.accountprovider.Repository.OAuthAccessTokenRepository;
import com.ufuk.accountprovider.Repository.OAuthRefreshTokenRepository;
import com.ufuk.accountprovider.SecurityContextRestorerFilter;
import com.ufuk.accountprovider.Service.CustomUserDetailsService;
import com.ufuk.accountprovider.Service.OAuthTokenStore;
import com.ufuk.accountprovider.TokenCookieCreationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private OAuthAccessTokenRepository oAuthAccessTokenRepository;

    @Autowired
    private OAuthRefreshTokenRepository oAuthRefreshTokenRepository;

    private final UserInfoRestTemplateFactory userInfoRestTemplateFactory;

    public SecurityConfig(UserInfoRestTemplateFactory userInfoRestTemplateFactory) {
        this.userInfoRestTemplateFactory = userInfoRestTemplateFactory;
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(encoder());
    }

    @Override
    public void configure( WebSecurity web ) throws Exception {
        web.ignoring().antMatchers( HttpMethod.OPTIONS, "/**" );
        web.ignoring().antMatchers( "/user/**" );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/oauth/token").permitAll()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/api-docs/**").permitAll()
                .anyRequest().authenticated()
                .and().anonymous().disable()
                .addFilterAfter(new TokenCookieCreationFilter(userInfoRestTemplateFactory), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterBefore(new SecurityContextRestorerFilter(userInfoRestTemplateFactory, tokenStore()), AnonymousAuthenticationFilter.class);;
    }

    @Bean
    public TokenStore tokenStore() {
        return new OAuthTokenStore(oAuthAccessTokenRepository, oAuthRefreshTokenRepository);
    }

    @Bean
    public PasswordEncoder encoder(){
        return  NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}