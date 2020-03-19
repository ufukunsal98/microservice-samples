package com.ufuk.accountprovider.Repository;

import com.ufuk.accountprovider.Entity.OAuthRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRefreshTokenRepository extends JpaRepository<OAuthRefreshToken , String> {
    Optional<OAuthRefreshToken> findByTokenId(String extractTokenKey);
}
