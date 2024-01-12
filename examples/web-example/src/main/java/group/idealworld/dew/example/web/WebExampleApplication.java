package group.idealworld.dew.example.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类.
 * <p>
 * visit : http://127.0.0.1:809/swagger-ui.html
 *
 * @author gudaoxuri
 */
@SpringBootApplication(proxyBeanMethods = false)
public class WebExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(WebExampleApplication.class).run(args);
    }

}
