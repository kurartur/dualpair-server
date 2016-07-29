package lt.dualpair.server.infrastructure.persistence.repository;

import org.springframework.data.repository.Repository;

import java.io.Serializable;

public interface ReadOnlyRepository<T, ID extends Serializable> extends Repository<T, ID> {

    T findOne(ID id);

    Iterable<T> findAll();

}
