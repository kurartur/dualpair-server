package lt.dualpair.server.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

@Configuration
@EnableJpaRepositories(
        basePackages = {"lt.dualpair.core"}
)
@EntityScan({"lt.dualpair.core", "lt.dualpair.server"})
public class PersistenceConfiguration extends JpaRepositoryConfigExtension {}
