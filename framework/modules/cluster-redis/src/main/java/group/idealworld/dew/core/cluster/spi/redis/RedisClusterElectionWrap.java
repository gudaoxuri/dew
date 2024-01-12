package group.idealworld.dew.core.cluster.spi.redis;

import group.idealworld.dew.core.cluster.ClusterElection;
import group.idealworld.dew.core.cluster.ClusterElectionWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 领导者选举服务多实例封装 Redis 实现.
 *
 * @author gudaoxuri
 */
public class RedisClusterElectionWrap implements ClusterElectionWrap {

    private static final ConcurrentHashMap<String, ClusterElection> ELECTION_CONTAINER = new ConcurrentHashMap<>();

    private RedisTemplate<String, String> redisTemplate;
    private int electionPeriodSec;

    /**
     * Instantiates a new Redis cluster election wrap.
     *
     * @param redisTemplate     the redis template
     * @param electionPeriodSec the election period sec
     */
    public RedisClusterElectionWrap(RedisTemplate<String, String> redisTemplate, int electionPeriodSec) {
        this.redisTemplate = redisTemplate;
        this.electionPeriodSec = electionPeriodSec;
    }

    @Override
    public ClusterElection instance() {
        ELECTION_CONTAINER.putIfAbsent("", new RedisClusterElection(electionPeriodSec, redisTemplate));
        return ELECTION_CONTAINER.get("");
    }

    @Override
    public ClusterElection instance(String key) {
        ELECTION_CONTAINER.putIfAbsent(key, new RedisClusterElection(key, electionPeriodSec, redisTemplate));
        return ELECTION_CONTAINER.get(key);
    }

}
