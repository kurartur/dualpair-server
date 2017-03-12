package lt.dualpair.server.interfaces.resource.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResource;
import org.springframework.hateoas.ResourceSupport;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserResource extends ResourceSupport {

    @JsonProperty("id")
    private Long userId;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date dateOfBirth;
    private Integer age;
    private String description;
    private Set<SociotypeResource> sociotypes;
    private Set<LocationResource> locations;
    private List<PhotoResource> photos;
    private List<UserAccountResource> accounts;
    private String relationshipStatus;
    private Set<String> purposesOfBeing;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<SociotypeResource> getSociotypes() {
        return sociotypes;
    }

    public void setSociotypes(Set<SociotypeResource> sociotypes) {
        this.sociotypes = sociotypes;
    }

    public Set<LocationResource> getLocations() {
        return locations;
    }

    public void setLocations(Set<LocationResource> locations) {
        this.locations = locations;
    }

    public List<PhotoResource> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoResource> photos) {
        this.photos = photos;
    }

    public List<UserAccountResource> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<UserAccountResource> accounts) {
        this.accounts = accounts;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public Set<String> getPurposesOfBeing() {
        return purposesOfBeing;
    }

    public void setPurposesOfBeing(Set<String> purposesOfBeing) {
        this.purposesOfBeing = purposesOfBeing;
    }
}
