package lt.dualpair.server.domain.model.photo;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_photos")
public class Photo implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "account_type")
    @Enumerated
    private UserAccount.Type accountType;

    @Column(name = "id_on_account")
    private String idOnAccount;

    @Column(name = "source_link")
    private String sourceLink;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccount.Type getAccountType() {
        return accountType;
    }

    public void setAccountType(UserAccount.Type accountType) {
        this.accountType = accountType;
    }

    public String getIdOnAccount() {
        return idOnAccount;
    }

    public void setIdOnAccount(String idOnAccount) {
        this.idOnAccount = idOnAccount;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }
}
