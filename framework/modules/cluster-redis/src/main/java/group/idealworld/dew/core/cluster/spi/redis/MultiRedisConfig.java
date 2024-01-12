package group.idealworld.dew.core.cluster.spi.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Multi redis config.
 *
 * @author gudaoxuri
 */
@ConfigurationProperties(prefix = "spring.data.redis")
public class MultiRedisConfig {

    private Map<String, RedisProperties> multi = new HashMap<>();

    /**
     * Gets multi.
     *
     * @return the multi
     */
    public Map<String, RedisProperties> getMulti() {
        return multi;
    }

    /**
     * Sets multi.
     *
     * @param multi the multi
     */
    public void setMulti(Map<String, RedisProperties> multi) {
        this.multi = multi;
    }
}
