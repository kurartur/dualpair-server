package lt.dualpair.server.domain.model.user;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_accounts")
public class UserAccount implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "account_type")
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

    public enum Type {
        FACEBOOK("FB"),
        VKONTAKTE("VK"),
        FAKE("FK");
        private String code;
        Type(String code) {
            this.code = code;
        }
        private static Map<String, Type> typesByCode = new HashMap<>();
        static {
            for (Type gender: Type.values()) {
                typesByCode.put(gender.code, gender);
            }
        }
        public String getCode() {
            return code;
        }
        public static Type fromCode(String code) {
            return typesByCode.get(code);
        }
    }

}
