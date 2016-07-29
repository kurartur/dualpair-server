package lt.dualpair.server.client;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

//@EnableAutoConfiguration
//@Configuration
//@EnableOAuth2Sso
//@RestController
public class ClientApplication {

    @RequestMapping("/")
    public String home(Principal user) {
        return "Hello " + user.getName();

    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApplication.class)
                .properties("spring.config.name=client").run(args);
    }

}
