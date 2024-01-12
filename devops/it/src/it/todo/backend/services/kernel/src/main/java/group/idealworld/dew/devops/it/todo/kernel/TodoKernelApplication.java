package group.idealworld.dew.devops.it.todo.kernel;

import group.idealworld.dew.devops.it.todo.common.TodoParentApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * To-do kernel application.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class TodoKernelApplication extends TodoParentApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(TodoKernelApplication.class).run(args);
    }

}
