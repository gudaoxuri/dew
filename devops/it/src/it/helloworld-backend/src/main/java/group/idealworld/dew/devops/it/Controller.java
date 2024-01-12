package group.idealworld.dew.devops.it;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller.
 *
 * @author gudaoxuri
 */
@RestController
public class Controller {

    /**
     * Hi.
     *
     * @return the message
     */
    @GetMapping("/")
    public String hi() {
        return "hello world";
    }

}
