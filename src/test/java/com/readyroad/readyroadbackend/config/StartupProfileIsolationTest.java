package com.readyroad.readyroadbackend.config;

import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.service.CanonicalRoadSignSyncService;
import com.readyroad.readyroadbackend.service.CanonicalSignCatalogService;
import com.readyroad.readyroadbackend.service.SignQuizDataInitializer;
import com.readyroad.readyroadbackend.service.SignQuizImportService;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class StartupProfileIsolationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(DataInitializer.class, DefaultAdminInitializer.class,
                    SignQuizDataInitializer.class, SchedulingConfiguration.class)
            .withBean(CanonicalRoadSignSyncService.class, () -> mock(CanonicalRoadSignSyncService.class))
            .withBean(UserRepository.class, () -> mock(UserRepository.class))
            .withBean(PasswordEncoder.class, () -> mock(PasswordEncoder.class))
            .withBean(RoadSignRepository.class, () -> mock(RoadSignRepository.class))
            .withBean(SignQuizImportService.class, () -> mock(SignQuizImportService.class))
            .withBean(CanonicalSignCatalogService.class, () -> mock(CanonicalSignCatalogService.class))
            .withBean(EntityManagerFactory.class, () -> mock(EntityManagerFactory.class))
            .withBean(JdbcTemplate.class, () -> mock(JdbcTemplate.class));

    @ParameterizedTest
    @ValueSource(strings = {
            "secure,postgresql,local-cms",
            "secure,postgresql,production-mirror",
            "secure,postgresql,local-cms,production-mirror",
            "local-cms"
    })
    void isolatedProfilesDoNotRegisterStartupWritersOrSchedulers(String profiles) {
        runner.withInitializer(context -> context.getEnvironment().setActiveProfiles(profiles.split(",")))
                .run(context -> {
                    assertThat(context).hasNotFailed()
                            .doesNotHaveBean(DataInitializer.class)
                            .doesNotHaveBean(DefaultAdminInitializer.class)
                            .doesNotHaveBean(SignQuizDataInitializer.class)
                            .doesNotHaveBean(SchedulingConfiguration.class)
                            .doesNotHaveBean("createDefaultAdmin")
                            .doesNotHaveBean("org.springframework.context.annotation.internalScheduledAnnotationProcessor");
                });
    }

    @Test
    void normalPostgresqlModeStillRegistersExistingStartupBehavior() {
        runner.withInitializer(context -> context.getEnvironment().setActiveProfiles("secure", "postgresql"))
                .run(context -> {
                    assertThat(context).hasNotFailed()
                            .hasSingleBean(DataInitializer.class)
                            .hasSingleBean(DefaultAdminInitializer.class)
                            .hasSingleBean(SignQuizDataInitializer.class)
                            .hasSingleBean(SchedulingConfiguration.class)
                            .hasBean("createDefaultAdmin")
                            .doesNotHaveBean("org.springframework.context.annotation.internalScheduledAnnotationProcessor");
                });
    }
}
