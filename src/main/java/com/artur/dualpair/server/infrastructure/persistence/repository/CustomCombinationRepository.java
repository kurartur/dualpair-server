package com.artur.dualpair.server.infrastructure.persistence.repository;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;

import java.util.Map;

public interface CustomCombinationRepository {

    void saveCombination(Map<String, String> choices, Sociotype sociotype);

}
