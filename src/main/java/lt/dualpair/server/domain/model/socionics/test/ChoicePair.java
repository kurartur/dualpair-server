package lt.dualpair.server.domain.model.socionics.test;

import javax.persistence.*;

@Entity
@Table(name = "test_choice_pairs")
public class ChoicePair {

    @Id
    private Integer id;

    @Column(name = "remote_id")
    private String remoteId;

    @ManyToOne
    @JoinColumn(name = "choice1_id")
    private Choice choice1;

    @ManyToOne
    @JoinColumn(name = "choice2_id")
    private Choice choice2;

    private ChoicePair() {}

    protected ChoicePair(Integer id, String remoteId, Choice choice1, Choice choice2) {
        this.id = id;
        this.remoteId = remoteId;
        this.choice1 = choice1;
        this.choice2 = choice2;
    }

    public Integer getId() {
        return id;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public Choice getChoice1() {
        return choice1;
    }

    public Choice getChoice2() {
        return choice2;
    }

    public static class Builder {

        private Integer id;

        private String remoteId;

        private Choice choice1;

        private Choice choice2;

        public Builder() {}

        public Builder id(Integer id) { this.id = id; return this; }

        public Builder remoteId(String remoteId) { this.remoteId = remoteId; return this; }

        public Builder choice1(Choice choice) { this.choice1 = choice; return this; }

        public Builder choice2(Choice choice) { this.choice2 = choice; return this; }

        public ChoicePair build() {
            return new ChoicePair(id, remoteId, choice1, choice2);
        }
    }
}
