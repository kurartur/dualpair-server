package lt.dualpair.server.interfaces.resource.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class PhotoResource extends ResourceSupport {

    @JsonProperty("id")
    private Long photoId;
    private String accountType;
    private String idOnAccount;
    private String sourceUrl;
    private Integer position;

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getIdOnAccount() {
        return idOnAccount;
    }

    public void setIdOnAccount(String idOnAccount) {
        this.idOnAccount = idOnAccount;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
