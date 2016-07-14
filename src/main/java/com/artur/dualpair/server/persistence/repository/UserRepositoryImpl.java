package com.artur.dualpair.server.persistence.repository;

import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.user.User;
import org.thymeleaf.util.Validate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRepositoryImpl implements CustomUserRepository {

    private EntityManager entityManager;

    @Override
    public Set<User> findOpponent(User user, Sociotype sociotype, SearchParameters searchParameters) {
        Validate.notNull(user, "User required");
        Validate.notNull(sociotype, "Sociotype required");
        Validate.notNull(searchParameters, "Search parameters required");
        Query query = entityManager.createQuery("select opp from User as opp\n" +
                "where opp not in (\n" +
                "   select m.opponent from Match as m where m.user = :user\n" +
                ")\n" +
                "   and opp.ageInfo.age >= :minAge and opp.ageInfo.age <= :maxAge\n" +
                "   and opp.gender in :genders\n" +
                "   and :sociotype member of opp.sociotypes\n" +
                "   and opp <> :user\n" +
                "   and :userAge >= opp.searchParameters.minAge and :userAge <= opp.searchParameters.maxAge\n" +
                "   and ((:userGenderCode = 'M' and opp.searchParameters.searchMale = true)\n" +
                "       or (:userGenderCode = 'F' and opp.searchParameters.searchFemale = true))\n" +
                "   and opp.searchParameters.location.countryCode = :countryCode");
        query.setParameter("user", user);
        query.setParameter("minAge", searchParameters.getMinAge());
        query.setParameter("maxAge", searchParameters.getMaxAge());
        query.setParameter("genders", getAllowedGenders(searchParameters.getSearchMale(), searchParameters.getSearchFemale()));
        query.setParameter("sociotype", sociotype);
        query.setParameter("countryCode", searchParameters.getLocation().getCountryCode());
        query.setParameter("userAge", user.getAge());
        query.setParameter("userGenderCode", user.getGender().getCode());
        return new HashSet<>(query.getResultList());
    }

    private List<User.Gender> getAllowedGenders(boolean searchMale, boolean searchFemale) {
        List<User.Gender> genderList = new ArrayList<>();
        if (searchMale && searchFemale) {
            genderList.add(User.Gender.MALE);
            genderList.add(User.Gender.FEMALE);
        } else if (searchMale) {
            genderList.add(User.Gender.MALE);
        } else if (searchFemale) {
            genderList.add(User.Gender.FEMALE);
        }
        return genderList;
    }

    private void assertNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new IllegalStateException(errorMessage);
        }
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
