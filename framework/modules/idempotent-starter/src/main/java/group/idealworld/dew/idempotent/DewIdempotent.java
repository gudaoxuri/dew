package group.idealworld.dew.idempotent;

import group.idealworld.dew.idempotent.strategy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Dew idempotent.
 *
 * @author gudaoxuri
 */
public class DewIdempotent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DewIdempotent.class);

    // optType -> IdempotentProcessor
    private static final Map<String, OptTypeInfo> CONTENT = new HashMap<>();

    // optType,optId
    private static final ThreadLocal<String[]> CONTEXT = new ThreadLocal<>();

    private static ItemProcessor itemProcessor = new ItemProcessor();
    private static BloomFilterProcessor bloomFilterProcessor = new BloomFilterProcessor();

    private DewIdempotent() {
    }

    /**
     * 初始化操作类型信息.
     *
     * @param optType     操作类型
     * @param needConfirm 是否需要显式确认
     * @param expireMs    过期时间
     * @param strategy    策略类型
     */
    public static void initOptTypeInfo(String optType, boolean needConfirm, long expireMs, StrategyEnum strategy) {
        LOGGER.info("Init OptType[{}] info: needConfirm:{} expireMs:{} strategy:{}", optType, needConfirm, expireMs,
                strategy.toString());
        IdempotentProcessor processor;
        switch (strategy) {
            case ITEM:
                processor = itemProcessor;
                break;
            case BLOOM_FLTER:
                processor = bloomFilterProcessor;
                break;
            default:
                LOGGER.error("Init OptType[{}] error: strategy:{} NOT exist.", strategy.toString());
                return;
        }
        OptTypeInfo optTypeInfo = new OptTypeInfo();
        optTypeInfo.processor = processor;
        optTypeInfo.expireMs = expireMs;
        optTypeInfo.needConfirm = needConfirm;
        CONTENT.put(optType, optTypeInfo);
    }

    /**
     * 操作类型是否存在，用于初化判断.
     *
     * @param optType 操作类型
     * @return 是否存在 boolean
     */
    public static boolean existOptTypeInfo(String optType) {
        return CONTENT.containsKey(optType);
    }

    /**
     * 处理请求，返回是否成功.
     *
     * @param optType 操作类型
     * @param optId   操作ID
     * @return the status enum
     */
    public static StatusEnum process(String optType, String optId) {
        CONTEXT.set(new String[] { optType, optId });
        OptTypeInfo optTypeInfo = CONTENT.get(optType);
        return optTypeInfo.processor.process(optType, optId,
                optTypeInfo.needConfirm ? StatusEnum.UN_CONFIRM : StatusEnum.CONFIRMED,
                optTypeInfo.expireMs);
    }

    /**
     * 操作确认.
     *
     * @param optType 操作类型
     * @param optId   操作ID
     */
    public static void confirm(String optType, String optId) {
        CONTENT.get(optType).processor.confirm(optType, optId);
    }

    /**
     * 操作确认，要求与请求入口在同一线程中.
     */
    public static void confirm() {
        String[] c = CONTEXT.get();
        if (c != null) {
            CONTENT.get(c[0]).processor.confirm(c[0], c[1]);
        }
    }

    /**
     * 操作取消.
     *
     * @param optType 操作类型
     * @param optId   操作ID
     */
    public static void cancel(String optType, String optId) {
        CONTENT.get(optType).processor.cancel(optType, optId);
    }

    /**
     * 操作取消，要求与请求入口在同一线程中.
     */
    public static void cancel() {
        String[] c = CONTEXT.get();
        if (c != null) {
            CONTENT.get(c[0]).processor.cancel(c[0], c[1]);
        }
    }

    private static class OptTypeInfo {

        /**
         * The Processor.
         */
        IdempotentProcessor processor;
        /**
         * The Expire ms.
         */
        long expireMs;
        /**
         * The Need confirm.
         */
        boolean needConfirm;

    }

}
