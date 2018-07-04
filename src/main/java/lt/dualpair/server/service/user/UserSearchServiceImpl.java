package lt.dualpair.server.service.user;

import lt.dualpair.core.user.DefaultUserFinder;
import lt.dualpair.core.user.FakeUserFinder;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserSearchServiceImpl implements UserSearchService {

    private FakeUserFinder fakeUserFinder;
    private DefaultUserFinder userFinder;
    private boolean fakeMatches;

    @Override
    @Transactional
    public Optional<User> findOne(UserRequest userRequest) {
        Assert.notNull(userRequest);
        Optional<User> user = userFinder.findOne(userRequest);
        if (!user.isPresent() && fakeMatches) {
            user = fakeUserFinder.findOne(userRequest);
        }
        return user;
    }

    @Autowired
    public void setFakeUserFinder(FakeUserFinder fakeUserFinder) {
        this.fakeUserFinder = fakeUserFinder;
    }

    @Autowired
    public void setUserFinder(DefaultUserFinder userFinder) {
        this.userFinder = userFinder;
    }

    @Value("${fakeMatches}")
    public void setFakeMatches(boolean fakeMatches) {
        this.fakeMatches = fakeMatches;
    }
}
