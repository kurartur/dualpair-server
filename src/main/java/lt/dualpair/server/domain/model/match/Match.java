package lt.dualpair.server.domain.model.match;

import lt.dualpair.server.domain.model.socionics.RelationType;
import org.springframework.hateoas.Identifiable;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table(name = "matches")
public class Match implements Serializable, Identifiable<Long> {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "relation_type_id")
    private RelationType relationType;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<MatchParty> matchParties = new HashSet<>();

    private Integer distance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date dateCreated = new Date();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public Set<MatchParty> getMatchParties() {
        return matchParties;
    }

    public void setMatchParties(MatchParty firstParty, MatchParty secondParty) {
        Assert.notNull(firstParty);
        Assert.notNull(secondParty);
        matchParties.clear();
        matchParties.add(firstParty);
        matchParties.add(secondParty);
    }

    public MatchParty getMatchParty(Long userId) {
        Assert.notNull(userId);
        Iterator<MatchParty> matchPartyIterator = matchParties.iterator();
        while (matchPartyIterator.hasNext()) {
            MatchParty matchParty = matchPartyIterator.next();
            if (matchParty.getUser().getId().equals(userId))
                return matchParty;
        }
        return null;
    }

    public MatchParty getOppositeMatchParty(Long userId) {
        Assert.notNull(userId);
        Iterator<MatchParty> matchPartyIterator = matchParties.iterator();
        while (matchPartyIterator.hasNext()) {
            MatchParty matchParty = matchPartyIterator.next();
            if (!matchParty.getUser().getId().equals(userId))
                return matchParty;
        }
        return null;
    }

    public boolean isMutual() {
        Iterator<MatchParty> matchPartyIterator = matchParties.iterator();
        MatchParty first = matchPartyIterator.next();
        MatchParty second = matchPartyIterator.next();
        return first.getResponse() == Response.YES && second.getResponse() == Response.YES;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
