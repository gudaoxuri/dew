/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.notification;

import com.ecfront.dew.common.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通知处理核心.
 *
 * @author gudaoxuri
 */
public class Notify {

    private static final Logger logger = LoggerFactory.getLogger(Notify.class);

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static final Map<String, Channel> NOTIFY_CHANNELS = new HashMap<>();
    private static final Map<String, Context> NOTIFY_CONTEXT = new HashMap<>();
    private static final DelayQueue<NotifyDelayed> DELAY_QUEUE = new DelayQueue<>();

    private static Map<String, NotifyConfig> notifyConfigMap;
    private static Function<String, String> instanceFetchFun;

    /**
     * Init.
     *
     * @param notifyConfigMap 配置
     */
    public static void init(Map<String, NotifyConfig> notifyConfigMap) {
        Notify.init(notifyConfigMap, flag -> "");
    }

    /**
     * Init.
     *
     * @param notifyConfigMap  配置
     * @param instanceFetchFun 此函数结果会附到title后面用于区分来自哪个实例
     */
    public static void init(Map<String, NotifyConfig> notifyConfigMap, Function<String, String> instanceFetchFun) {
        Notify.notifyConfigMap = notifyConfigMap;
        Notify.instanceFetchFun = instanceFetchFun;
        notifyConfigMap.forEach((key, value) -> {
            Channel channel = null;
            try {
                channel = (Channel) Class.forName(Notify.class.getPackage().getName() + "." + value.getType() + "Channel")
                        .getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Not exist notify type:" + value.getType());
                throw new RuntimeException(e);
            }
            channel.init(value);
            NOTIFY_CHANNELS.put(key, channel);
            NOTIFY_CONTEXT.put(key, new Context());
        });
        delaySend();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!NOTIFY_CHANNELS.isEmpty()) {
                NOTIFY_CHANNELS.values().forEach(Channel::destroy);
            }
        }));
    }

    /**
     * 判断flag 是否存在.
     *
     * @param flag flag
     * @return 是否存在
     */
    public static boolean contains(String flag) {
        return NOTIFY_CHANNELS.containsKey(flag);
    }

    /**
     * Send async.
     *
     * @param flag    the flag
     * @param content the content
     */
    public static void sendAsync(String flag, String content) {
        sendAsync(flag, content, "");
    }

    /**
     * Send async.
     *
     * @param flag    the flag
     * @param content the content
     * @param title   the title
     */
    public static void sendAsync(String flag, String content, String title) {
        sendAsync(flag, content, title, new HashSet<>());
    }

    /**
     * Send async.
     *
     * @param flag             the flag
     * @param content          the content
     * @param title            the title
     * @param specialReceivers the special receivers
     */
    public static void sendAsync(String flag, String content, String title, Set<String> specialReceivers) {
        EXECUTOR_SERVICE.execute(() -> send(flag, content, title, specialReceivers));
    }

    /**
     * Send async.
     *
     * @param flag    the flag
     * @param content the content
     */
    public static void sendAsync(String flag, Throwable content) {
        sendAsync(flag, content, "");
    }

    /**
     * Send async.
     *
     * @param flag    the flag
     * @param content the content
     * @param title   the title
     */
    public static void sendAsync(String flag, Throwable content, String title) {
        sendAsync(flag, content, title, new HashSet<>());
    }

    /**
     * Send async.
     *
     * @param flag             the flag
     * @param content          the content
     * @param title            the title
     * @param specialReceivers the special receivers
     */
    public static void sendAsync(String flag, Throwable content, String title, Set<String> specialReceivers) {
        EXECUTOR_SERVICE.execute(() -> send(flag, content, title, specialReceivers));
    }

    /**
     * Send resp.
     *
     * @param flag    the flag
     * @param content the content
     * @return the resp
     */
    public static Resp<Void> send(String flag, String content) {
        return send(flag, content, "");
    }

    /**
     * Send resp.
     *
     * @param flag    the flag
     * @param content the content
     * @param title   the title
     * @return the resp
     */
    public static Resp<Void> send(String flag, String content, String title) {
        return send(flag, content, title, new HashSet<>());
    }

    /**
     * Send resp.
     *
     * @param flag    the flag
     * @param content the content
     * @return the resp
     */
    public static Resp<Void> send(String flag, Throwable content) {
        return send(flag, content, "");
    }

    /**
     * Send resp.
     *
     * @param flag    the flag
     * @param content the content
     * @param title   the title
     * @return the resp
     */
    public static Resp<Void> send(String flag, Throwable content, String title) {
        return send(flag, content, title, new HashSet<>());
    }

    /**
     * Send resp.
     *
     * @param flag             the flag
     * @param content          the content
     * @param title            the title
     * @param specialReceivers the special receivers
     * @return the resp
     */
    public static Resp<Void> send(String flag, Throwable content, String title, Set<String> specialReceivers) {
        return send(flag, content.toString()
                        + "\n" + Arrays.stream(content.getStackTrace()).map(StackTraceElement::toString).limit(10).collect(Collectors.joining("\n")),
                title, specialReceivers);
    }

    /**
     * Send resp.
     *
     * @param flag             the flag
     * @param content          the content
     * @param title            the title
     * @param specialReceivers the special receivers
     * @return the resp
     */
    public static Resp<Void> send(String flag, String content, String title, Set<String> specialReceivers) {
        return send(flag, content, title, specialReceivers, false);
    }

    private static Resp<Void> send(String flag, String content, String title, Set<String> specialReceivers, boolean delayed) {
        Channel channel = NOTIFY_CHANNELS.getOrDefault(flag, null);
        if (channel == null) {
            logger.trace("Not found notify flag[" + flag + "] in dew config.");
            return Resp.badRequest("Not found notify flag[" + flag + "] in dew config.");
        }
        NotifyConfig notifyConfig = notifyConfigMap.get(flag);
        Context notifyContext = NOTIFY_CONTEXT.get(flag);
        // 策略处理
        if (!delayed && notifyConfig.getStrategy().getMinIntervalSec() != 0) {
            if (notifyContext.intervalNotifyCounter.getAndIncrement() > 0) {
                logger.trace("Notify frequency must be > " + notifyConfig.getStrategy().getMinIntervalSec() + "s");
                return Resp.locked("Notify frequency must be > " + notifyConfig.getStrategy().getMinIntervalSec() + "s");
            } else {
                DELAY_QUEUE.offer(new NotifyDelayed(flag, specialReceivers, notifyConfig.getStrategy().getMinIntervalSec() * 1000));
            }
        }
        boolean isDNDTime = false;
        if (!notifyConfig.getStrategy().getDndTime().isEmpty()) {
            String[] dndTime = notifyConfig.getStrategy().getDndTime().split("-");
            Calendar time = Calendar.getInstance();
            long currentTime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(time.getTime()));
            int currentShortTime = Integer.parseInt(new SimpleDateFormat("HHmm").format(time.getTime()));
            int dndStartShortTime = Integer.parseInt(dndTime[0].replace(":", ""));
            int dndEndShortTime = Integer.parseInt(dndTime[1].replace(":", ""));
            if (dndEndShortTime == dndStartShortTime) {
                // 全天
                isDNDTime = true;
            } else if (dndEndShortTime - dndStartShortTime >= 0) {
                // 没有跨天
                isDNDTime = currentShortTime >= dndStartShortTime && currentShortTime <= dndEndShortTime;
            } else {
                // 跨天
                isDNDTime = currentShortTime >= dndStartShortTime || currentShortTime <= dndEndShortTime;
                time.add(Calendar.DATE, 1);
            }
            if (currentTime > notifyContext.lastDNDEndTime.getAndSet(
                    Long.parseLong(new SimpleDateFormat("yyyyMMdd")
                            .format(time.getTime()) + dndTime[1].replace(":", "")))) {
                // 已在后几个免扰周期
                notifyContext.currentForceSendTimes.set(0);
            }
            if (isDNDTime
                    && notifyConfig.getStrategy().getForceSendTimes() > notifyContext.currentForceSendTimes.incrementAndGet()) {
                if (delayed) {
                    DELAY_QUEUE.offer(new NotifyDelayed(flag, specialReceivers, notifyConfig.getStrategy().getMinIntervalSec() * 1000));
                }
                logger.trace("Do Not Disturb time and try notify times <=" + notifyConfig.getStrategy().getForceSendTimes());
                return Resp.locked("Do Not Disturb time and try notify times <=" + notifyConfig.getStrategy().getForceSendTimes());
            }
        }
        if (delayed) {
            // 发送延迟通知，清空积累的计数
            notifyContext.totalDelayMs.set(0);
            notifyContext.totalNotifyCounter.set(0);
        }
        Set<String> receivers = new HashSet<>();
        if (isDNDTime) {
            receivers.addAll(notifyConfig.getDndTimeReceivers());
        } else {
            receivers.addAll(notifyConfig.getDefaultReceivers());
            receivers.addAll(specialReceivers);
        }
        // 格式化
        title = title != null ? title : "";
        // 添加实例信息
        title += instanceFetchFun.apply(flag);
        title = title.replaceAll("\"", "'");
        content = content.replaceAll("\"", "'");
        boolean result = channel.send(content, title, receivers);
        if (result) {
            return Resp.success(null);
        } else {
            logger.warn("Notify send error.");
            return Resp.serverError("Notify send error.");
        }
    }

    private static void delaySend() {
        EXECUTOR_SERVICE.execute(() -> {
            while (true) {
                try {
                    NotifyDelayed notifyDelayed = DELAY_QUEUE.take();
                    String flag = notifyDelayed.getFlag();
                    Context notifyContext = NOTIFY_CONTEXT.get(flag);
                    long notifyCounter = notifyContext.intervalNotifyCounter.getAndSet(0);
                    Set<String> specialReceivers = notifyDelayed.getSpecialReceivers();
                    EXECUTOR_SERVICE.execute(() ->
                            send(flag, "在最近的["
                                            + notifyContext.totalDelayMs.addAndGet(notifyDelayed.getDelayMs() / 1000)
                                            + "]秒内发生了[" + notifyContext.totalNotifyCounter.addAndGet(notifyCounter) + "]次通知请求。",
                                    "延时通知", specialReceivers, true));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Send delay notify error.", e);
                }
            }
        });
    }

    /**
     * Context.
     */
    static class Context {
        /**
         * The Interval notify counter.
         */
        AtomicLong intervalNotifyCounter = new AtomicLong(0);
        /**
         * The Last dnd end time.
         */
        AtomicLong lastDNDEndTime = new AtomicLong(0);
        /**
         * The Current force send times.
         */
        AtomicInteger currentForceSendTimes = new AtomicInteger(0);
        /**
         * The Total delay ms.
         */
        AtomicLong totalDelayMs = new AtomicLong(0);
        /**
         * The Total notify counter.
         */
        AtomicLong totalNotifyCounter = new AtomicLong(0);
    }

    /**
     * Notify delayed.
     */
    static class NotifyDelayed implements Delayed {

        private String flag;
        private Set<String> specialReceivers;
        private int delayMs;
        private long expireMs;

        /**
         * Instantiates a new Notify delayed.
         *
         * @param flag             the flag
         * @param specialReceivers the special receivers
         * @param delayMs          the delay ms
         */
        public NotifyDelayed(String flag, Set<String> specialReceivers, int delayMs) {
            this.flag = flag;
            this.specialReceivers = specialReceivers;
            this.delayMs = delayMs;
            this.expireMs = delayMs + System.currentTimeMillis();
        }

        /**
         * Gets flag.
         *
         * @return the flag
         */
        public String getFlag() {
            return flag;
        }

        /**
         * Gets special receivers.
         *
         * @return the special receivers
         */
        public Set<String> getSpecialReceivers() {
            return specialReceivers;
        }

        /**
         * Gets delay ms.
         *
         * @return the delay ms
         */
        public int getDelayMs() {
            return delayMs;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expireMs - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }

    }


}
