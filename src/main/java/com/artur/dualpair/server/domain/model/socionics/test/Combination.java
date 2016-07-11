package com.artur.dualpair.server.domain.model.socionics.test;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "test_combinations")
public class Combination {

    @Id
    private Integer id;

    @OneToMany(mappedBy = "combination")
    private Set<CombinationChoice> combinationChoices;

    @ManyToOne
    @JoinColumn(name = "sociotype_id")
    private Sociotype sociotype;

    public Integer getId() {
        return id;
    }

    public Set<CombinationChoice> getCombinationChoices() {
        return combinationChoices;
    }

    public Sociotype getSociotype() {
        return sociotype;
    }
}
