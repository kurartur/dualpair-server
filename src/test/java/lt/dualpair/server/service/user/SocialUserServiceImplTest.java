package lt.dualpair.server.service.user;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.infrastructure.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SocialUserServiceImplTest {

    private SocialUserServiceImpl socialUserService = new SocialUserServiceImpl();
    private UserRepository userRepository = mock(UserRepository.class);
    private SocialDataProviderFactory socialDataProviderFactory = mock(SocialDataProviderFactory.class);

    @Before
    public void setUp() throws Exception {
        socialUserService.setUserRepository(userRepository);
        socialUserService.setSocialDataProviderFactory(socialDataProviderFactory);
    }

    @Test
     public void testLoadOrCreate_nullParameters() throws Exception {
        try {
            socialUserService.loadOrCreate(null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Connection required", iae.getMessage());
        }
    }

    @Test
    public void testLoadOrCreate_userExists() throws Exception {
        User user = new User();
        FacebookDataProvider facebookDataProvider = mock(FacebookDataProvider.class);
        when(facebookDataProvider.getAccountId()).thenReturn("1");
        when(socialDataProviderFactory.getProvider(any(Connection.class))).thenReturn(facebookDataProvider);
        when(userRepository.findByAccountId("1", UserAccount.Type.FACEBOOK)).thenReturn(Optional.of(user));
        User resultUser = socialUserService.loadOrCreate(mock(Connection.class));
        assertEquals(user, resultUser);
    }

    @Test
    public void testLoadOrCreate() throws Exception {
        FacebookDataProvider facebookDataProvider = mock(FacebookDataProvider.class);
        Date dateOfBirth = Date.from(LocalDate.now().minus(5, ChronoUnit.YEARS).atStartOfDay(ZoneId.systemDefault()).toInstant());
        when(facebookDataProvider.enhanceUser(any(User.class))).thenAnswer(invocation -> {
            User user = (User)invocation.getArguments()[0];
            user.setDateOfBirth(dateOfBirth);
            user.setGender(User.Gender.MALE);
            return user;
        });
        when(facebookDataProvider.getAccountId()).thenReturn("111");
        when(socialDataProviderFactory.getProvider(any(Connection.class))).thenReturn(facebookDataProvider);
        when(userRepository.findByAccountId("111", UserAccount.Type.FACEBOOK)).thenReturn(Optional.<User>empty());
        User resultUser = socialUserService.loadOrCreate(mock(Connection.class));
        verify(facebookDataProvider, times(1)).enhanceUser(any(User.class));
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(1, resultUser.getUserAccounts().size());
        UserAccount userAccount = resultUser.getUserAccounts().iterator().next();
        assertEquals("111", userAccount.getAccountId());
        assertEquals(UserAccount.Type.FACEBOOK, userAccount.getAccountType());
        assertNotNull(resultUser.getUserId());
        assertNotNull(resultUser.getCreated());
        assertNotNull(resultUser.getUpdated());
        SearchParameters searchParameters = resultUser.getSearchParameters();
        assertNotNull(searchParameters);
        assertEquals((Integer)2, searchParameters.getMinAge());
        assertEquals((Integer)8, searchParameters.getMaxAge());
        assertTrue(searchParameters.getSearchFemale());
        assertFalse(searchParameters.getSearchMale());
    }
}