package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.interfaces.dto.assembler.SociotypeDTOAssembler;
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

    @RequestMapping(value = "/test/evaluate", method = RequestMethod.POST)
    public ResponseEntity evaluateTest(@RequestParam Map<String, String> choices) throws SocionicsTestException {
        Sociotype sociotype = socionicsTestService.evaluate(choices);
        return ResponseEntity.ok(new SociotypeDTOAssembler().toDTO(sociotype));
    }

    @Autowired
    public void setSocionicsTestService(SocionicsTestService socionicsTestService) {
        this.socionicsTestService = socionicsTestService;
    }
}
