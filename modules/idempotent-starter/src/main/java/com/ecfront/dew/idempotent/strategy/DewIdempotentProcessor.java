package com.ecfront.dew.idempotent.strategy;

public interface DewIdempotentProcessor {

    StatusEnum process(String optType, String optId, StatusEnum initStatus, long expireMs);

    boolean confirm(String optType, String optId);

    boolean cancel(String optType, String optId);

}
