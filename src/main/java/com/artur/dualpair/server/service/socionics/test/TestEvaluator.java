package com.artur.dualpair.server.service.socionics.test;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;

import java.util.Map;

public interface TestEvaluator {

    Sociotype evaluate(Map<String, String> choices) throws SocionicsTestException;

}
