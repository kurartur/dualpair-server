package com.artur.dualpair.server.persistence.repository;

import com.artur.dualpair.server.domain.model.match.Location;
import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        user.setGender(User.Gender.MALE);
        Sociotype sociotype = new Sociotype.Builder().code2(Sociotype.Code2.ISFJ).build();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(20);
        searchParameters.setMaxAge(25);
        searchParameters.setSearchMale(true);
        searchParameters.setSearchFemale(true);
        searchParameters.setLocation(new Location(10.0, 10.0, "LT"));
        userRepository.findOpponent(user, sociotype, searchParameters);
        verify(query, times(1)).setParameter("user", user);
        verify(query, times(1)).setParameter("minAge", 20);
        verify(query, times(1)).setParameter("maxAge", 25);
        List<User.Gender> genders = new ArrayList<>();
        genders.add(User.Gender.MALE);
        genders.add(User.Gender.FEMALE);
        verify(query, times(1)).setParameter("genders", genders);
        verify(query, times(1)).setParameter("sociotype", sociotype);
        verify(query, times(1)).setParameter("countryCode", "LT");
        verify(query, times(1)).setParameter("userAge", 0);
        verify(query, times(1)).setParameter("userGenderCode", "M");
    }

    @Test
    public void testFindOpponent_searchForMale() throws Exception {
        User user = new User();
        user.setDateOfBirth(new Date());
        user.setGender(User.Gender.MALE);
        Sociotype sociotype = new Sociotype.Builder().code2(Sociotype.Code2.ISFJ).build();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(20);
        searchParameters.setMaxAge(25);
        searchParameters.setSearchMale(true);
        searchParameters.setSearchFemale(false);
        searchParameters.setLocation(new Location(10.0, 10.0, "LT"));
        userRepository.findOpponent(user, sociotype, searchParameters);
        verify(query, times(1)).setParameter("user", user);
        verify(query, times(1)).setParameter("minAge", 20);
        verify(query, times(1)).setParameter("maxAge", 25);
        List<User.Gender> genders = new ArrayList<>();
        genders.add(User.Gender.MALE);
        verify(query, times(1)).setParameter("genders", genders);
        verify(query, times(1)).setParameter("sociotype", sociotype);
        verify(query, times(1)).setParameter("countryCode", "LT");
        verify(query, times(1)).setParameter("userAge", 0);
        verify(query, times(1)).setParameter("userGenderCode", "M");
    }

    @Test
    public void testFindOpponent_searchForFemale() throws Exception {
        User user = new User();
        user.setDateOfBirth(new Date());
        user.setGender(User.Gender.MALE);
        Sociotype sociotype = new Sociotype.Builder().code2(Sociotype.Code2.ISFJ).build();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(20);
        searchParameters.setMaxAge(25);
        searchParameters.setSearchMale(false);
        searchParameters.setSearchFemale(true);
        searchParameters.setLocation(new Location(10.0, 10.0, "LT"));
        userRepository.findOpponent(user, sociotype, searchParameters);
        verify(query, times(1)).setParameter("user", user);
        verify(query, times(1)).setParameter("minAge", 20);
        verify(query, times(1)).setParameter("maxAge", 25);
        List<User.Gender> genders = new ArrayList<>();
        genders.add(User.Gender.FEMALE);
        verify(query, times(1)).setParameter("genders", genders);
        verify(query, times(1)).setParameter("sociotype", sociotype);
        verify(query, times(1)).setParameter("countryCode", "LT");
        verify(query, times(1)).setParameter("userAge", 0);
        verify(query, times(1)).setParameter("userGenderCode", "M");
    }

}