package lt.dualpair.server.infrastructure.persistence.repository;

import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.Optional;

public interface ReadOnlyRepository<T, ID extends Serializable> extends Repository<T, ID> {

    Optional<T> findOne(ID id);

    Iterable<T> findAll();

}
