package lt.dualpair.server.service.socionics.test;

import lt.dualpair.core.socionics.Sociotype;

import java.util.Map;

public interface TestEvaluator {

    Sociotype evaluate(Map<String, String> choices) throws SocionicsTestException;

}
