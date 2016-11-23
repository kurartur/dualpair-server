package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.socionics.RelationType;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.domain.model.user.UserLocation;
import lt.dualpair.server.infrastructure.persistence.repository.RelationTypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

@Component
public class FakeMatchFinder implements MatchFinder {

    private SociotypeRepository sociotypeRepository;
    private UserRepository userRepository;
    private RelationTypeRepository relationTypeRepository;

    @Override
    public Match findOne(MatchRequest matchRequest) {
        Random random = new Random();
        Set<User.Gender> genders = matchRequest.getGenders();
        int index = random.nextInt(genders.size());
        User.Gender gender = new ArrayList<>(genders).get(index);
        RandomUser randomUser = getRestTemplate().getForObject(buildUrl(gender), RandomUser.class);

        User user = new User();
        user.setGender(gender);
        user.setEmail(randomUser.email);
        user.setName(randomUser.name.first);
        user.setDateOfBirth(matchRequest.getUser().getDateOfBirth());

        // sociotypes
        Set<Sociotype> sociotypes = new HashSet<>();
        sociotypes.add(sociotypeRepository.findOppositeByRelationType(
                matchRequest.getUser().getRandomSociotype().getCode1(),
                matchRequest.getRelationType()));
        user.setSociotypes(sociotypes);

        // accounts
        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(UserAccount.Type.FAKE);
        userAccount.setAccountId(randomUser.id.name + randomUser.id.value);
        Set<UserAccount> userAccounts = new HashSet<>();
        user.setUserAccounts(userAccounts);

        //location
        UserLocation userLocation = new UserLocation(
                user,
                matchRequest.getLatitude(),
                matchRequest.getLongitude(),
                matchRequest.getCountryCode(),
                randomUser.location.city);
        user.addLocation(userLocation, 1);

        // photos
        Photo photo1 = new Photo();
        photo1.setAccountType(UserAccount.Type.FAKE);
        photo1.setSourceLink(randomUser.picture.medium);
        photo1.setIdOnAccount("1");
        Photo photo2 = new Photo();
        photo2.setAccountType(UserAccount.Type.FAKE);
        photo2.setSourceLink(randomUser.picture.medium);
        photo2.setIdOnAccount("2");
        List<Photo> photos = Arrays.asList(photo1, photo2);
        user.setPhotos(photos);

        userRepository.save(user);

        return createMatch(matchRequest.getUser(), user, matchRequest.getRelationType(), random.nextInt(300));
    }

    private String buildUrl(User.Gender gender) {
        return "https://randomuser.me/api/?gender=" + gender.name().toLowerCase();
    }

    private Match createMatch(User user, User opponent, RelationType.Code relationType, Integer distance) {
        Match match = new Match();
        match.setRelationType(relationTypeRepository.findByCode(relationType).get());
        match.setMatchParties(new MatchParty(match, user), new MatchParty(match, opponent));
        match.setDistance(distance);
        match.setDateCreated(new Date());
        return match;
    }

    // TODO duplicate code
    private RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
            InetSocketAddress address = new InetSocketAddress(System.getProperty("http.proxyHost"), Integer.valueOf(System.getProperty("http.proxyPort")));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
            factory.setProxy(proxy);
        }
        return new RestTemplate(factory);
    }

    @Autowired
    public void setSociotypeRepository(SociotypeRepository sociotypeRepository) {
        this.sociotypeRepository = sociotypeRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRelationTypeRepository(RelationTypeRepository relationTypeRepository) {
        this.relationTypeRepository = relationTypeRepository;
    }

    public static final class RandomUser {

        private String gender;
        private RandomUserName name;
        private String email;
        private RandomUserId id;
        private RandomUserPicture picture;
        private RandomUserLocation location;

    }

    public static final class RandomUserName {

        private String first;

    }

    public static final class RandomUserId {

        private String name;
        private String value;

    }

    public static final class RandomUserPicture {

        private String medium;

    }

    public static final class RandomUserLocation {

        private String city;
    }

    /*
    * "gender": "male",
      "name": {
        "title": "mr",
        "first": "romain",
        "last": "hoogmoed"
      },
      "location": {
        "street": "1861 jan pieterszoon coenstraat",
        "city": "maasdriel",
        "state": "zeeland",
        "postcode": 69217
      },
      "email": "romain.hoogmoed@example.com",
      "login": {
        "username": "lazyduck408",
        "password": "jokers",
        "salt": "UGtRFz4N",
        "md5": "6d83a8c084731ee73eb5f9398b923183",
        "sha1": "cb21097d8c430f2716538e365447910d90476f6e",
        "sha256": "5a9b09c86195b8d8b01ee219d7d9794e2abb6641a2351850c49c309f1fc204a0"
      },
      "dob": "1983-07-14 07:29:45",
      "registered": "2010-09-24 02:10:42",
      "phone": "(656)-976-4980",
      "cell": "(065)-247-9303",
      "id": {
        "name": "BSN",
        "value": "04242023"
      },
      "picture": {
        "large": "https://randomuser.me/api/portraits/men/83.jpg",
        "medium": "https://randomuser.me/api/portraits/med/men/83.jpg",
        "thumbnail": "https://randomuser.me/api/portraits/thumb/men/83.jpg"
      },
      "nat": "NL"
    * */
}
