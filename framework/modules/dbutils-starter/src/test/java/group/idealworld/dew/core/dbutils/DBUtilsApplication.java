package group.idealworld.dew.core.dbutils;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author rpf
 * @version 1.0
 * @date 2024/1/9 18:27
 */
@SpringBootApplication
public class DBUtilsApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(DBUtilsApplication.class).run(args);
    }
}
