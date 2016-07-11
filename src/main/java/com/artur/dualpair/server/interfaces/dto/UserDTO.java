package com.artur.dualpair.server.interfaces.dto;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {

    private String name;
    private Integer age;
    private String description;
    private Set<SociotypeDTO> sociotypes = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
