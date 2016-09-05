package lt.dualpair.server.interfaces.resource.user;

import org.springframework.hateoas.ResourceSupport;

public class UserAccountResource extends ResourceSupport {

    private String accountType;
    private String accountId;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
