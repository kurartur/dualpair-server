package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.Gender;
import lt.dualpair.server.domain.model.user.User;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRepositoryImpl implements CustomUserRepository {

    private static final String FIND_OPPONENTS_QUERY = "" +
            "select opp from User as opp\n" +
            "where opp not in (\n" +
            "   select mp2.user from MatchParty as mp1, MatchParty as mp2" +
            "   where mp1.user = :user and mp2.user != :user\n" +
            ")\n" +
            "   and opp.ageInfo.age >= :minAge and opp.ageInfo.age <= :maxAge\n" +
            "   and opp.gender in :genders\n" +
            "   and :sociotype member of opp.sociotypes\n" +
            "   and opp <> :user\n" +
            "   and :userAge >= opp.searchParameters.minAge and :userAge <= opp.searchParameters.maxAge\n" +
            "   and ((:userGenderCode = 'M' and opp.searchParameters.searchMale = true)\n" +
            "       or (:userGenderCode = 'F' and opp.searchParameters.searchFemale = true))\n" +
            "   and exists (" +
            "       select ul from UserLocation ul where ul.user = opp and ul.location.countryCode = :countryCode" +
            "   )" +
            "   and opp.id not in :exclude";

    private EntityManager entityManager;

    @Override
    public Set<User> findOpponents(FindOpponentsParams params) {
        Assert.notNull(params);
        Query query = entityManager.createQuery(FIND_OPPONENTS_QUERY);
        query.setParameter("user", params.user);
        query.setParameter("minAge", params.minAge);
        query.setParameter("maxAge", params.maxAge);
        query.setParameter("genders", params.genders);
        query.setParameter("sociotype", params.sociotype);
        query.setParameter("countryCode", params.countryCode);
        query.setParameter("userAge", params.user.getAge());
        query.setParameter("userGenderCode", params.user.getGender().getCode());
        query.setParameter("exclude", params.excludeOpponents.isEmpty() ? Arrays.asList(-1L) : params.excludeOpponents);
        return new HashSet<>(query.getResultList());
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static class FindOpponentsParams {
        protected User user;
        protected Sociotype sociotype;
        protected int minAge;
        protected int maxAge;
        protected Set<Gender> genders;
        protected String countryCode;
        protected List<Long> excludeOpponents;

        public FindOpponentsParams(User user, Sociotype sociotype, int minAge, int maxAge, Set<Gender> genders, String countryCode, List<Long> excludeOpponents) {
            this.user = user;
            this.sociotype = sociotype;
            this.minAge = minAge;
            this.maxAge = maxAge;
            this.genders = genders;
            this.countryCode = countryCode;
            this.excludeOpponents = excludeOpponents;
        }
    }
}
