package group.idealworld.dew.devops.it.todo.compute;

import group.idealworld.dew.devops.it.todo.common.TodoParentApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * To-do compute application.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class TodoComputeApplication extends TodoParentApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(TodoComputeApplication.class).run(args);
    }

}
