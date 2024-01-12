package group.idealworld.dew.devops.kernel.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Final config.
 * <p>
 * 最终生成的配置
 *
 * @author gudaoxuri
 */
public class FinalConfig {

    private Map<String, FinalProjectConfig> projects = new LinkedHashMap<>();

    /**
     * Gets projects.
     *
     * @return the projects
     */
    public Map<String, FinalProjectConfig> getProjects() {
        return projects;
    }

}
