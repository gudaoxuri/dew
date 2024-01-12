package group.idealworld.dew.example.idempotent;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Idempotent example application.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class IdempotentExampleApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(IdempotentExampleApplication.class).run(args);
    }
}
