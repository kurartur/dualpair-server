package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.socionics.Sociotype;

import java.util.Map;

public class CombinationRepositoryImpl implements CustomCombinationRepository {

    @Override
    public void saveCombination(Map<String, String> choices, Sociotype sociotype) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
