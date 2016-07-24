package com.artur.dualpair.server.interfaces.dto;

public class PhotoDTO {

    private Long id;
    private String sourceLink;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }
}
