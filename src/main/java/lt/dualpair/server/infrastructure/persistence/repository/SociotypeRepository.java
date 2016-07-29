package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.socionics.RelationType;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface SociotypeRepository extends ReadOnlyRepository<Sociotype, Integer> {

    @Query("select s from Sociotype s where s.code1 = ?1")
    Sociotype findByCode1(Sociotype.Code1 code);

    @Query("select s from Sociotype s where s.code1 in ?1")
    Set<Sociotype> findByCode1List(List<Sociotype.Code1> codeList);

    @Query("select s from Sociotype s where s.code2 in ?1")
    Set<Sociotype> findByCode2List(List<Sociotype.Code2> codeList);

    @Query("select sr.opposite from SociotypeRelation sr where sr.sociotype.code1 = ?1 and sr.relationType.code = ?2")
    Sociotype findOppositeByRelationType(Sociotype.Code1 sociotypeCode, RelationType.Code relTypeCode);

}
