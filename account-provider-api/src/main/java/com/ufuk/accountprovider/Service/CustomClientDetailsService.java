package com.ufuk.accountprovider.Service;

import com.ufuk.accountprovider.Entity.CustomClientDetails;
import com.ufuk.accountprovider.Repository.CustomClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class CustomClientDetailsService implements ClientDetailsService {
    @Autowired
    private CustomClientDetailsRepository customClientDetailsRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        CustomClientDetails client = customClientDetailsRepository.findByClientId(clientId);

        if (client == null) {
            throw  new ClientRegistrationException("Client Not Found");
        }

        String resourceIds = client.getResourceIds().stream().collect(Collectors.joining(","));
        String scopes = client.getScope().stream().collect(Collectors.joining(","));
        String grantTypes = client.getAuthorizedGrantTypes().stream().collect(Collectors.joining(","));
        String authorities = client.getAuthorities().stream().collect(Collectors.joining(","));

        BaseClientDetails base = new BaseClientDetails(client.getClientId(), resourceIds, scopes, grantTypes, authorities);
        base.setClientSecret(client.getClientSecret());
        System.out.println(client.getAccessTokenValiditySeconds());
        base.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        base.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        base.setAdditionalInformation(new HashMap<>());
        base.setAutoApproveScopes(client.getScope());
        return base;
    }

    static final String GRANT_TYPE_PASSWORD = "password";
    static final String AUTHORIZATION_CODE = "authorization_code";
    static final String REFRESH_TOKEN = "refresh_token";
    static final String IMPLICIT = "implicit";
    static final String SCOPE_READ = "read";
    static final String SCOPE_WRITE = "write";
    static final String TRUST = "trust";

    @PostConstruct
    public void saveClientDetail() {

            CustomClientDetails client = new CustomClientDetails();
            client.setId("someId");

            client.setResourceIds(new HashSet<>(Arrays.asList("resource_id")) );
            client.setClientId("android-client");
            client.setClientSecret("android-secret");
            client.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT)));
            client.setScope(new HashSet<>(Arrays.asList(SCOPE_READ, SCOPE_WRITE, TRUST)));
            client.setSecretRequired(true);
            client.setAccessTokenValiditySeconds(120);
            client.setRefreshTokenValiditySeconds(120);
            client.setScoped(false);

        customClientDetailsRepository.save(client);

    }
}
