package com.artur.dualpair.server.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UserDTO {

    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date dateOfBirth;
    private Integer age;
    private String description;
    private Set<SociotypeDTO> sociotypes = new HashSet<>();
    private SearchParametersDTO searchParameters;

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

    public Set<SociotypeDTO> getSociotypes() {
        return sociotypes;
    }

    public void setSociotypes(Set<SociotypeDTO> sociotypes) {
        this.sociotypes = sociotypes;
    }

    public SearchParametersDTO getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(SearchParametersDTO searchParameters) {
        this.searchParameters = searchParameters;
    }
}
