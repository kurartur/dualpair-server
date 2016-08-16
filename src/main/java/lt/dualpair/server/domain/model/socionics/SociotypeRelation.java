package lt.dualpair.server.domain.model.socionics;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sociotype_relations")
public class SociotypeRelation implements Serializable {

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

}
