package lt.dualpair.server.service.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPhotoServiceImpl implements UserPhotoService {

    private UserRepository userRepository;
    private PhotoFileHelper photoFileHelper;

    @Inject
    public UserPhotoServiceImpl(UserRepository userRepository, PhotoFileHelper photoFileHelper) {
        this.userRepository = userRepository;
        this.photoFileHelper = photoFileHelper;
    }

    @Override
    @Transactional
    public List<Photo> setUserPhotos(Long userId, List<PhotoModel> photoList) {
        Assert.isTrue(photoList.size() > 0, "Must have at least one photo");
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Photo> userPhotos = new ArrayList<>(user.getPhotos());

        userPhotos.removeIf(photo -> !photoList.stream().map(PhotoModel::getId).collect(Collectors.toList()).contains(photo.getId()));

        userPhotos.forEach(photo ->
                photoList.stream()
                    .filter(pm -> photo.getId().equals(pm.getId()))
                    .findFirst()
                    .ifPresent(photoModel -> photo.setPosition(photoModel.getPosition())));

        photoList.stream()
                .filter(pm -> pm.getId() == null)
                .forEach(photoModel -> {
                    Photo photo = new Photo();
                    photo.setUser(user);
                    photo.setPosition(photoModel.getPosition());
                    String path = photoFileHelper.save(photoModel.getPhoto(), user.getId(), photoModel.getPosition());
                    photo.setSourceLink(path);
                    userPhotos.add(photo);
                });

        user.setPhotos(userPhotos);
        userRepository.save(user);

        return user.getPhotos();
    }

}
