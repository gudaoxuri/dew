package group.idealworld.dew.idempotent.strategy;

import group.idealworld.dew.core.cluster.exception.NotImplementedException;

/**
 * Bloom filter processor.
 *
 * @author gudaoxuri
 */
public class BloomFilterProcessor implements IdempotentProcessor {

    @Override
    public StatusEnum process(String optType, String optId, StatusEnum initStatus, long expireMs) {
        throw new NotImplementedException();
    }

    @Override
    public boolean confirm(String optType, String optId) {
        throw new NotImplementedException();
    }

    @Override
    public boolean cancel(String optType, String optId) {
        throw new NotImplementedException();
    }

}
