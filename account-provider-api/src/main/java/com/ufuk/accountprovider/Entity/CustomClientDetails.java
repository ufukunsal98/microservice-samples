package com.ufuk.accountprovider.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
public class CustomClientDetails  {

    @Id
    private String id;

    @NotNull
    private String clientId;
    private String clientSecret;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> resourceIds = new HashSet<>();
    private boolean secretRequired;
    private boolean scoped;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scope = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> authorizedGrantTypes = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> registeredRedirectUri = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<String> authorities = new HashSet<>();
    private Integer accessTokenValiditySeconds;
    private  Integer refreshTokenValiditySeconds;
    private boolean autoApprove;

//    private Map<String, Object> additionalInformation = new HashMap<>();
}