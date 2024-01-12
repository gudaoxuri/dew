package group.idealworld.dew.devops.kernel.config;

/**
 * Dew kubernetes.
 *
 * @author gudaoxuri
 */
public class DewKube {

    // Kubernetes Base64 后的配置，使用 ``echo $(cat ~/.kube/config | base64) | tr -d " "``
    // 获取
    private String base64Config = "";

    /**
     * Gets base 64 config.
     *
     * @return the base 64 config
     */
    public String getBase64Config() {
        return base64Config;
    }

    /**
     * Sets base 64 config.
     *
     * @param base64Config the base 64 config
     */
    public void setBase64Config(String base64Config) {
        this.base64Config = base64Config;
    }
}
