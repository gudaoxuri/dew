package group.idealworld.dew.example.auth;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class AuthExampleApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthExampleApplication.class).run(args);
    }

}
