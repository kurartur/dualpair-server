package com.artur.dualpair.server.domain.model.socionics;

import javax.persistence.*;

@Entity
@Table(name = "socionic_relation_types")
public class RelationType {

    public enum Code { DUAL }

    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Code code;

}
