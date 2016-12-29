package lt.dualpair.server.domain.model.socionics.test;

import lt.dualpair.server.domain.model.socionics.Sociotype;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "test_combinations")
public class Combination {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "combination")
    private Set<CombinationChoice> combinationChoices;

    @ManyToOne
    @JoinColumn(name = "sociotype_id")
    private Sociotype sociotype;

    private Combination() {}

    public Combination(Set<CombinationChoice> combinationChoices, Sociotype sociotype) {
        this.combinationChoices = combinationChoices;
        this.sociotype = sociotype;
    }

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
