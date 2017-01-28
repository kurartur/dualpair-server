package lt.dualpair.server.domain.model.user;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_reports")
public class UserReport {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @Column(name = "report_date")
    private Date reportDate = new Date();

    private UserReport() {}

    public UserReport(User user, User reportedBy) {
        this.user = user;
        this.reportedBy = reportedBy;
    }

    public User getUser() {
        return user;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public Date getReportDate() {
        return reportDate;
    }
}
