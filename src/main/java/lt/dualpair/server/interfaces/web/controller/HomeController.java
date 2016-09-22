package lt.dualpair.server.interfaces.web.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Profile("!prod")
public class HomeController {

    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Hello";
    }

}
