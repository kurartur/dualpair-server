package com.artur.dualpair.server.domain.model.user;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_accounts")
public class UserAccount implements Serializable {

    public enum Type {FACEBOOK}

    public static final int TYPE_FACEBOOK = 0;
    public static final int TYPE_VKONTAKTE = 1;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "account_type")
    @Enumerated
    private Type accountType;

    private UserAccount() {}

    public UserAccount(User user) {
        this.user = user;
    }

    public Type getAccountType() {
        return accountType;
    }

    public void setAccountType(Type type) {
        this.accountType = type;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
