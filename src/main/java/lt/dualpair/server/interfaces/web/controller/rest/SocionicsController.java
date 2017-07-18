package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.core.socionics.Sociotype;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResource;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResourceAssembler;
import lt.dualpair.server.service.socionics.test.SocionicsTestException;
import lt.dualpair.server.service.socionics.test.SocionicsTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/socionics")
public class SocionicsController {

    private SocionicsTestService socionicsTestService;
    private SociotypeResourceAssembler sociotypeResourceAssembler;

    @RequestMapping(value = "/test/evaluate", method = RequestMethod.POST)
    public ResponseEntity<SociotypeResource> evaluateTest(@RequestParam Map<String, String> choices) throws SocionicsTestException {
        Sociotype sociotype = socionicsTestService.evaluate(choices);
        return ResponseEntity.ok(sociotypeResourceAssembler.toResource(sociotype));
    }

    @Autowired
    public void setSocionicsTestService(SocionicsTestService socionicsTestService) {
        this.socionicsTestService = socionicsTestService;
    }

    @Autowired
    public void setSociotypeResourceAssembler(SociotypeResourceAssembler sociotypeResourceAssembler) {
        this.sociotypeResourceAssembler = sociotypeResourceAssembler;
    }
}
