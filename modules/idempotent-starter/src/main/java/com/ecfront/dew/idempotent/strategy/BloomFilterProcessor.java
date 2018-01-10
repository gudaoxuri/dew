package com.ecfront.dew.idempotent.strategy;


import com.ecfront.dew.Dew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BloomFilterProcessor implements DewIdempotentProcessor {

    private static final String NAME_SPACE_EXIT = "dew:idempotent:bloom:exit:";

    private static final String NAME_SPACE_CONFIRM = "dew:idempotent:bloom:confirm";

    @Autowired
    private BloomFilter bloomFilter;

    @Override
    public StatusEnum process(String optType, String optId, StatusEnum initStatus, long expireMs) {
        String key = optType + "-" + optId;
        if (!bloomFilter.setExitRecord(key,true)) {
            if (initStatus.equals(StatusEnum.CONFIRMED)){
                bloomFilter.setConfirmRecord(key,true);
            }
            return StatusEnum.NOT_EXIST;
        }
        if (bloomFilter.getConfirmRecord(key)){
            return StatusEnum.CONFIRMED;
        }
        return StatusEnum.UN_CONFIRM;
    }

    @Override
    public boolean confirm(String optType, String optId) {
        String key = optType + "-" + optId;
        return bloomFilter.setConfirmRecord(key,true);
    }

    @Override
    public boolean cancel(String optType, String optId) {
        String key = optType + "-" + optId;
        bloomFilter.setExitRecord(key,false);
        bloomFilter.setConfirmRecord(key,false);
        return false;
    }

    @Component
    private class BloomFilter {

        static final int ROW_LENGTH = 10000000;

        private long getHash(String key) {
            return key.hashCode();
        }

        private long getBiteKey(long hash) {
            return hash / ROW_LENGTH;
        }

        private long getOffset(long hash) {
            return hash % ROW_LENGTH;
        }

        private boolean setExitRecord(String key,boolean value) {
            long hash = getHash(key);
            return Dew.cluster.cache.setBit(NAME_SPACE_EXIT + getBiteKey(hash), getOffset(hash), true);
        }

        private boolean getExitRecord(String key) {
            long hash = getHash(key);
            return Dew.cluster.cache.getBit(NAME_SPACE_EXIT + getBiteKey(hash), getOffset(hash));
        }

        private boolean setConfirmRecord(String key,boolean value) {
            long hash = getHash(key);
            return Dew.cluster.cache.setBit(NAME_SPACE_CONFIRM + getBiteKey(hash), getOffset(hash), true);
        }

        private boolean getConfirmRecord(String key) {
            long hash = getHash(key);
            return Dew.cluster.cache.getBit(NAME_SPACE_CONFIRM + getBiteKey(hash), getOffset(hash));
        }

    }
}
