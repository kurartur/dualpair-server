package lt.dualpair.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

@Configuration
@EnableJpaRepositories(
        basePackages = {"lt.dualpair.server"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"lt.dualpair.server.infrastructure.persistence.repository.ReadOnlyRepository"})
)
public class PersistenceConfiguration extends JpaRepositoryConfigExtension {
}
