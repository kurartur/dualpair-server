package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.Optional;

public interface UserReportRepository extends CrudRepository<UserReport, Long> {

    @Query("select count(ur) from UserReport ur where ur.reportedBy = ?1 and ur.reportDate > ?2")
    Integer getReportCountByUser(User user, Date startDate);

    @Query("select ur from UserReport ur where ur.user = ?1 and ur.reportedBy = ?2")
    Optional<UserReport> findUserReportByUser(User user, User reportedBy);

}