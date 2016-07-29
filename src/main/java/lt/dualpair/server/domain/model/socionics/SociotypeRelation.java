package lt.dualpair.server.domain.model.socionics;

import javax.persistence.*;

@Entity
@Table(name = "sociotype_relations")
public class SociotypeRelation {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name="sociotype")
    private Sociotype sociotype;

    @ManyToOne
    @JoinColumn(name="opposite")
    private Sociotype opposite;

    @ManyToOne
    @JoinColumn(name = "relation_type_id")
    private RelationType relationType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sociotype getSociotype() {
        return sociotype;
    }

    public void setSociotype(Sociotype sociotype) {
        this.sociotype = sociotype;
    }

    public Sociotype getOpposite() {
        return opposite;
    }

    public void setOpposite(Sociotype opposite) {
        this.opposite = opposite;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }
}
