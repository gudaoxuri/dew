package group.idealworld.dew.idempotent;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Idempotent application.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class IdempotentApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(IdempotentApplication.class).run(args);
    }
}
