package lt.dualpair.server.domain.model.socionics.test;

import javax.persistence.*;

@Entity
@Table(name = "test_combinations_choices")
public class CombinationChoice {

    @Id
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
