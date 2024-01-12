package group.idealworld.dew.example.bone;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类.
 *
 * @author gudaoxuri
 */
@SpringBootApplication(proxyBeanMethods = false)
public class BoneExampleApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(BoneExampleApplication.class).run(args);
    }

}
