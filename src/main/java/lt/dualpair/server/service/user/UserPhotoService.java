package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;

import java.util.List;

public interface UserPhotoService {

    List<Photo> setUserPhotos(Long userId, List<PhotoModel> photoList);

}
