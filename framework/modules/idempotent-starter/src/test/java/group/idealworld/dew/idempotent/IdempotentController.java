package group.idealworld.dew.idempotent;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.idempotent.annotations.Idempotent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Idempotent controller.
 *
 * @author gudaoxuri
 */
@RestController
@RequestMapping("/idempotent/")
public class IdempotentController {

    /**
     * Test manual confirm.
     *
     * @param str the str
     * @return the result
     */
    @GetMapping(value = "manual-confirm")
    @Idempotent(expireMs = 5000)
    public Resp<String> testManualConfirm(@RequestParam("str") String str) {
        try {
            Thread.sleep(1000);
            DewIdempotent.confirm();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return Resp.success(str);
    }

    /**
     * Test auto confirm.
     *
     * @param str the str
     * @return the result
     */
    @GetMapping(value = "auto-confirm")
    @Idempotent(needConfirm = false, expireMs = 5000)
    public Resp<String> testAutoConfirm(@RequestParam("str") String str) {
        return Resp.success(str);
    }

    /**
     * Normal.
     *
     * @param str the str
     * @return the result
     */
    @GetMapping(value = "normal")
    public Resp<String> normal(@RequestParam("str") String str) {
        return Resp.success(str);
    }

}
