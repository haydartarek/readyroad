package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.AuthIdentity;
import com.readyroad.readyroadbackend.domain.enums.AuthProvider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, Long> {

    Optional<AuthIdentity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    Optional<AuthIdentity> findByUserIdAndProvider(Long userId, AuthProvider provider);

    List<AuthIdentity> findByUserId(Long userId);

    boolean existsByUserIdAndProvider(Long userId, AuthProvider provider);
}
