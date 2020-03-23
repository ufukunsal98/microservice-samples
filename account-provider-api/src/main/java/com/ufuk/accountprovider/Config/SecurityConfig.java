package com.ufuk.accountprovider.Config;

import com.ufuk.accountprovider.Repository.OAuthAccessTokenRepository;
import com.ufuk.accountprovider.Repository.OAuthRefreshTokenRepository;
import com.ufuk.accountprovider.Service.CustomUserDetailsService;
import com.ufuk.accountprovider.Service.OAuthTokenStore;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Primary
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

    private final OAuthAccessTokenRepository oAuthAccessTokenRepository;

    private final OAuthRefreshTokenRepository oAuthRefreshTokenRepository;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, OAuthAccessTokenRepository oAuthAccessTokenRepository, OAuthRefreshTokenRepository oAuthRefreshTokenRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.oAuthAccessTokenRepository = oAuthAccessTokenRepository;
        this.oAuthRefreshTokenRepository = oAuthRefreshTokenRepository;
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
        //web.ignoring().antMatchers( HttpMethod.OPTIONS, "/**" );
        web.ignoring().antMatchers( "/user/**" );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/api-docs/**").permitAll()
                .anyRequest().authenticated()
                .and().anonymous().disable();
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//We don't need session.
        ;
    }

    @Bean
    public TokenStore tokenStore() {
        return new OAuthTokenStore(oAuthAccessTokenRepository , oAuthRefreshTokenRepository);
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

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
