package group.idealworld.dew.example.cluster;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class ClusterExampleApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(ClusterExampleApplication.class).run(args);
    }

}
