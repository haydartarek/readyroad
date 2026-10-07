package com.readyroad.readyroadbackend.util;

import com.readyroad.readyroadbackend.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationUtilPaidAccessTest {
    @Test
    void paidAccessNeverUsesTheDevelopmentFallback() {
        AuthenticationUtil util = new AuthenticationUtil();
        ReflectionTestUtils.setField(util, "securityMode", "dev");
        assertThat(util.extractUserId(null)).isEqualTo(1L);
        assertThat(util.extractVerifiedUserId(null)).isNull();
        assertThat(util.extractVerifiedUserId(new AnonymousAuthenticationToken("test", "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))))).isNull();
        User user = new User(); user.setId(42L);
        assertThat(util.extractVerifiedUserId(new UsernamePasswordAuthenticationToken(user, null))).isNull();
        assertThat(util.extractVerifiedUserId(new UsernamePasswordAuthenticationToken(user, null, List.of())))
                .isEqualTo(42L);
    }
}
