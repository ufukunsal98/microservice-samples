package com.ufuk.accountprovider.Domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

import static org.springframework.security.oauth2.common.OAuth2AccessToken.SCOPE;
import static org.springframework.security.oauth2.common.util.OAuth2Utils.CLIENT_ID;

public class DefaultAuthorizationRequest extends AuthorizationRequest implements Serializable {

    private Set<String> scope = new LinkedHashSet<String>();

    private Set<String> resourceIds = new HashSet<String>();

    private boolean approved = false;

    private Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

    private Map<String, String> authorizationParameters = new HashMap<String, String>();

    private Map<String, String> approvalParameters = new HashMap<String, String>();

    private String resolvedRedirectUri;

    public DefaultAuthorizationRequest(Map<String, String> authorizationParameters) {
        this(authorizationParameters, Collections.<String, String> emptyMap(), authorizationParameters.get(CLIENT_ID),
                OAuth2Utils.parseParameterList(authorizationParameters.get("scope")), null, null, false);
    }

    public DefaultAuthorizationRequest(Map<String, String> authorizationParameters,
                                       Map<String, String> approvalParameters, String clientId, Collection<String> scope) {
        this(authorizationParameters, approvalParameters, clientId, scope, null, null, false);
    }

    public DefaultAuthorizationRequest(String clientId, Collection<String> scope) {
        this(null, null, clientId, scope, null, null, false);
    }



    private DefaultAuthorizationRequest(Map<String, String> authorizationParameters,
                                        Map<String, String> approvalParameters, String clientId, Collection<String> scope,
                                        Collection<GrantedAuthority> authorities, Collection<String> resourceIds, boolean approved) {
        if (authorizationParameters != null) {
            this.authorizationParameters.putAll(authorizationParameters);
        }
        if (approvalParameters != null) {
            this.approvalParameters.putAll(approvalParameters);
        }
        if (resourceIds != null) {
            this.resourceIds = new HashSet<String>(resourceIds);
        }
        if (scope != null) {
            this.scope = new LinkedHashSet<String>(scope);
        }
        if (authorities != null) {
            this.authorities = new HashSet<GrantedAuthority>(authorities);
        }
        this.authorizationParameters.put(CLIENT_ID, clientId);
        this.authorizationParameters.put(SCOPE, OAuth2Utils.formatParameterList(scope));
        this.approved = approved;
    }

    public Map<String, String> getAuthorizationParameters() {
        return Collections.unmodifiableMap(authorizationParameters);
    }

    public Map<String, String> getApprovalParameters() {
        return Collections.unmodifiableMap(approvalParameters);
    }

    public String getClientId() {
        return authorizationParameters.get(CLIENT_ID);
    }

    public Set<String> getScope() {
        return Collections.unmodifiableSet(this.scope);
    }

    public Set<String> getResourceIds() {
        return Collections.unmodifiableSet(resourceIds);
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableSet((Set<? extends GrantedAuthority>) authorities);
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isDenied() {
        return !approved;
    }

    public String getState() {
        return "";
    }

    public String getRedirectUri() {
        return "";
    }

    public Set<String> getResponseTypes() {
        return null;
    }

    public void setRedirectUri(String redirectUri) {
        this.resolvedRedirectUri = redirectUri;
    }

    public void addClientDetails(ClientDetails clientDetails) {
        resourceIds.addAll(clientDetails.getResourceIds());
        authorities.addAll(clientDetails.getAuthorities());
    }

    public void setScope(Set<String> scope) {
        if (scope != null && scope.size() == 1) {
            String value = scope.iterator().next();
            /*
             * This is really an error, but it can catch out unsuspecting users and it's easy to fix. It happens when an
             * AuthorizationRequest gets bound accidentally from request parameters using @ModelAttribute.
             */
            if (value.contains(" ") || scope.contains(",")) {
                scope = OAuth2Utils.parseParameterList(value);
            }
        }
        this.scope = scope == null ? new LinkedHashSet<String>() : new LinkedHashSet<String>(scope);
        authorizationParameters.put(SCOPE, OAuth2Utils.formatParameterList(scope));
    }

    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds == null ? new HashSet<String>() : new HashSet<String>(resourceIds);
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities == null ? new HashSet<GrantedAuthority>() : new HashSet<GrantedAuthority>(
                authorities);
    }

    public void setAuthorizationParameters(Map<String, String> authorizationParameters) {
        this.authorizationParameters = authorizationParameters == null ? new HashMap<String, String>()
                : new HashMap<String, String>(authorizationParameters);
        if (authorizationParameters.containsKey(SCOPE) && StringUtils.hasText(authorizationParameters.get(SCOPE))) {
            String scope = authorizationParameters.get(SCOPE);
            setScope(OAuth2Utils.parseParameterList(scope));
        }
    }

    public void setApprovalParameters(Map<String, String> approvalParameters) {
        this.approvalParameters = approvalParameters == null ? new HashMap<String, String>()
                : new HashMap<String, String>(approvalParameters);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
        result = prime * result + (approved ? 1231 : 1237);
        result = prime * result + ((authorizationParameters == null) ? 0 : authorizationParameters.hashCode());
        result = prime * result + ((approvalParameters == null) ? 0 : approvalParameters.hashCode());
        result = prime * result + ((resourceIds == null) ? 0 : resourceIds.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultAuthorizationRequest other = (DefaultAuthorizationRequest) obj;
        if (authorities == null) {
            if (other.authorities != null)
                return false;
        }
        else if (!authorities.equals(other.authorities))
            return false;
        if (approved != other.approved)
            return false;
        if (authorizationParameters == null) {
            if (other.authorizationParameters != null)
                return false;
        }
        else if (!authorizationParameters.equals(other.authorizationParameters))
            return false;
        if (resourceIds == null) {
            if (other.resourceIds != null)
                return false;
        }
        else if (!resourceIds.equals(other.resourceIds))
            return false;
        if (scope == null) {
            if (other.scope != null)
                return false;
        }
        else if (!scope.equals(other.scope))
            return false;
        if (approvalParameters == null) {
            if (other.approvalParameters != null)
                return false;
        }
        else if (!approvalParameters.equals(other.approvalParameters))
            return false;
        return true;
    }

}