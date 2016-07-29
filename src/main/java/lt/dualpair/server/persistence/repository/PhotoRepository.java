package lt.dualpair.server.persistence.repository;

import lt.dualpair.server.domain.model.photo.Photo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PhotoRepository extends CrudRepository<Photo, Long> {

    @Query("select p from Photo p where p.user.id = ?1 and p.id = ?2")
    Optional<Photo> findUserPhoto(Long userId, Long photoId);

}
