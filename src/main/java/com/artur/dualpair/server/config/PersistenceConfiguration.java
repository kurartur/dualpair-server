package com.artur.dualpair.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.artur.dualpair.server.persistence.repository"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.artur.dualpair.server.persistence.repository.ReadOnlyRepository"})
)
public class PersistenceConfiguration extends JpaRepositoryConfigExtension {
}
