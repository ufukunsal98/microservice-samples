package com.ufuk.accountprovider.Repository;

import com.ufuk.accountprovider.Entity.OAuthAccessTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthAccessTokenRepository extends JpaRepository<OAuthAccessTokens, String> {

    List<OAuthAccessTokens> findByClientId(String clientId);

    List<OAuthAccessTokens> findByClientIdAndUsername(String clientId, String username);

    Optional<OAuthAccessTokens> findByTokenId(String tokenId);

    Optional<OAuthAccessTokens> findByRefreshToken(String refreshToken);

    Optional<OAuthAccessTokens> findByAuthenticationId(String authenticationId);

}
