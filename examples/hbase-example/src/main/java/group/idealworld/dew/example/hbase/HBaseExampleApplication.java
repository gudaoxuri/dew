package group.idealworld.dew.example.hbase;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * The class of project start.
 *
 * @author 迹_Jason
 */
@SpringBootApplication
public class HBaseExampleApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(HBaseExampleApplication.class).run(args);
    }

}
