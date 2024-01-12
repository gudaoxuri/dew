package group.idealworld.dew.devops.it.todo.compute.service;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.ScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Compute service.
 *
 * @author gudaoxuri
 */
@Service
public class ComputeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeService.class);

    /**
     * Compute.
     *
     * @param jsCode the js code
     * @return result
     */
    public String compute(String jsCode) {
        LOGGER.info("Compute : " + jsCode);
        return $.eval(ScriptHelper.ScriptKind.JS, Integer.class, jsCode).toString();
    }

}
