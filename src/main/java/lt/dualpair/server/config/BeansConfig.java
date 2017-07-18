package lt.dualpair.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
@ComponentScan("lt.dualpair.core")
public class BeansConfig {

    // TODO duplicate code
    private RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
            InetSocketAddress address = new InetSocketAddress(System.getProperty("http.proxyHost"), Integer.valueOf(System.getProperty("http.proxyPort")));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
            factory.setProxy(proxy);
        }
        return new RestTemplate(factory);
    }

}
