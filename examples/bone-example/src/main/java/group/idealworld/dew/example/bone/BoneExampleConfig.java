package group.idealworld.dew.example.bone;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 在根路径创建配置隐射类.
 *
 * @author gudaoxuri
 */
@Component
@ConfigurationProperties(prefix = "bone-example")
public class BoneExampleConfig {

    private String someProp;

    /**
     * Gets some prop.
     *
     * @return the some prop
     */
    public String getSomeProp() {
        return someProp;
    }

    /**
     * Sets some prop.
     *
     * @param someProp the some prop
     */
    public void setSomeProp(String someProp) {
        this.someProp = someProp;
    }

}
