package lt.dualpair.server.service.photo;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.infrastructure.persistence.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PhotoService {

    private PhotoRepository photoRepository;

    public Photo getUserPhoto(Long userId, Long photoId) throws PhotoServiceException {
        Optional<Photo> photo = photoRepository.findUserPhoto(userId, photoId);
        if (photo.isPresent())
            return photo.get();
        else
            return null;
    }

    @Autowired
    public void setPhotoRepository(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }
}
