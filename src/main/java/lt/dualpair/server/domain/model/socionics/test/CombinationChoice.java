package lt.dualpair.server.domain.model.socionics.test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "test_combinations_choices")
public class CombinationChoice implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "combination_id")
    private Combination combination;

    @ManyToOne
    @JoinColumn(name = "choice_pair_id")
    private ChoicePair choicePair;

    @ManyToOne
    @JoinColumn(name = "choice_id")
    private Choice choice;

    private CombinationChoice() {}

    public CombinationChoice(Combination combination, ChoicePair choicePair, Choice choice) {
        this.combination = combination;
        this.choicePair = choicePair;
        this.choice = choice;
    }

    public Integer getId() {
        return id;
    }

    public Combination getCombination() {
        return combination;
    }

    public ChoicePair getChoicePair() {
        return choicePair;
    }

    public Choice getChoice() {
        return choice;
    }

}
