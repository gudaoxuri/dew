package group.idealworld.dew.devops.it;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Application.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class Application {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).run(args);
    }

}
