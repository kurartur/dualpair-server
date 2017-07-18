package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.core.socionics.Sociotype;
import lt.dualpair.core.user.Gender;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserRepositoryImpl;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository = new UserRepositoryImpl();
    private EntityManager entityManager = mock(EntityManager.class);
    private Query query = mock(Query.class);

    @Before
    public void setUp() throws Exception {
        userRepository.setEntityManager(entityManager);
        when(entityManager.createQuery(any(String.class))).thenReturn(query);
    }

    @Test
    public void testFindOpponent() throws Exception {
        User user = new User();
        user.setDateOfBirth(new Date());
        user.setGender(Gender.MALE);
        Sociotype sociotype = new Sociotype.Builder().code2(Sociotype.Code2.ISFJ).build();
        Set<Gender> genders = new HashSet<>();
        genders.add(Gender.MALE);
        genders.add(Gender.FEMALE);
        UserRepositoryImpl.FindOpponentsParams params = new UserRepositoryImpl.FindOpponentsParams(
                user,
                sociotype,
                20,
                25,
                genders,
                "LT",
                Arrays.asList(1L)
        );
        userRepository.findOpponents(params);
        verify(query, times(1)).setParameter("user", user);
        verify(query, times(1)).setParameter("minAge", 20);
        verify(query, times(1)).setParameter("maxAge", 25);
        verify(query, times(1)).setParameter("genders", genders);
        verify(query, times(1)).setParameter("sociotype", sociotype);
        verify(query, times(1)).setParameter("countryCode", "LT");
        verify(query, times(1)).setParameter("userAge", 0);
        verify(query, times(1)).setParameter("userGenderCode", "M");
        verify(query, times(1)).setParameter("exclude", Arrays.asList(1L));
    }

}