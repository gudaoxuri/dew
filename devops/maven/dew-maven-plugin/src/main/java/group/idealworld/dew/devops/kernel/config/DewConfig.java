package group.idealworld.dew.devops.kernel.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Dew config.
 * <p>
 * Dew的配置，对应于 .dew 文件
 *
 * @author gudaoxuri
 */
public class DewConfig extends DewProfile {

    private Map<String, DewProfile> profiles = new HashMap<>();

    /**
     * Gets profiles.
     *
     * @return the profiles
     */
    public Map<String, DewProfile> getProfiles() {
        return profiles;
    }

    /**
     * Sets profiles.
     *
     * @param profiles the profiles
     */
    public void setProfiles(Map<String, DewProfile> profiles) {
        this.profiles = profiles;
    }
}
