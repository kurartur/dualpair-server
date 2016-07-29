package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.socionics.Sociotype;

import java.util.Map;

public interface CustomCombinationRepository {

    void saveCombination(Map<String, String> choices, Sociotype sociotype);

}
