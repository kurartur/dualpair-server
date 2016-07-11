package com.artur.dualpair.server.persistence.repository;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;

import java.util.Map;

public interface CustomCombinationRepository {

    void saveCombination(Map<String, String> choices, Sociotype sociotype);

}
