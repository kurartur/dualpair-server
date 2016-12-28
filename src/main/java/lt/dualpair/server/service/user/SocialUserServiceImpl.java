package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service("socialUserService")
public class SocialUserServiceImpl extends UserServiceImpl implements SocialUserService {

    private static final Logger logger = LoggerFactory.getLogger(SocialUserServiceImpl.class);

    private SocialDataProviderFactory socialDataProviderFactory;

    @Override
    @Transactional
    public Photo addUserPhoto(Long userId, UserAccount.Type accountType, String idOnAccount, int position) {
        User user = loadUserById(userId);
        Photo photo = socialDataProviderFactory.getProvider(accountType, user.getUsername())
                .getPhoto(idOnAccount).orElseThrow((Supplier<RuntimeException>) () -> new IllegalArgumentException("Photo doesn't exist on account or is not public"));
        photo.setUser(user);
        photo.setPosition(position);
        photoRepository.save(photo);
        return photo;
    }

    @Override
    @Transactional
    public List<Photo> setUserPhotos(Long userId, List<PhotoData> photoDataList) {
        User user = loadUserById(userId);

        List<Photo> photos = new ArrayList<>();

        photoDataList.stream()
                .collect(Collectors.groupingBy(PhotoData::getAccountType))
                .forEach((accountType, accountTypePhotoDataList) -> {

                    List<String> idsOnAccount = accountTypePhotoDataList.stream()
                            .map(photoData -> photoData.idOnAccount)
                            .distinct()
                            .collect(Collectors.toList());

                    List<Photo> photosByAccount = socialDataProviderFactory.getProvider(accountType, user.getUsername())
                            .getPhotos(idsOnAccount);

                    if (idsOnAccount.size() != photosByAccount.size()) {
                        List<String> notFound = new ArrayList<>(idsOnAccount);
                        notFound.removeAll(photosByAccount.stream().map(Photo::getIdOnAccount).collect(Collectors.toList()));
                        throw new IllegalArgumentException("Photo(s) " + Arrays.toString(notFound.toArray()) + " do(es)n't exist on account or is (are) not public");
                    }

                    photosByAccount.forEach(photo -> {
                        photo.setUser(user);
                        photo.setPosition(accountTypePhotoDataList.stream()
                                .filter(photoData -> Objects.equals(photoData.idOnAccount, photo.getIdOnAccount()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Photo not found"))
                                .position);
                    });

                    photos.addAll(photosByAccount);
                });

        user.setPhotos(photos.stream()
                .sorted((photo1, photo2) -> photo1.getPosition() > photo2.getPosition() ? 1 : -1)
                .collect(Collectors.toList()));

        userRepository.save(user);

        return photos;
    }

    @Autowired
    public void setSocialDataProviderFactory(SocialDataProviderFactory socialDataProviderFactory) {
        this.socialDataProviderFactory = socialDataProviderFactory;
    }
}
