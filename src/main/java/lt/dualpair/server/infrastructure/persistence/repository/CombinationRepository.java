package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.socionics.test.Combination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombinationRepository extends JpaRepository<Combination, Integer> {}
