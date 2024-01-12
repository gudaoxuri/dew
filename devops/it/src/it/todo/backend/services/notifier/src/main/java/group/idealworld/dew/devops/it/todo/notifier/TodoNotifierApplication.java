package group.idealworld.dew.devops.it.todo.notifier;

import group.idealworld.dew.devops.it.todo.common.TodoParentApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * To-do notifier application.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class TodoNotifierApplication extends TodoParentApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(TodoNotifierApplication.class).run(args);
    }

}
