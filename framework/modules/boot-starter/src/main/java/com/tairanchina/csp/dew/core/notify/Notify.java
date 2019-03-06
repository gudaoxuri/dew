package com.tairanchina.csp.dew.core.notify;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Notify {

    private static final Logger logger = LoggerFactory.getLogger(Notify.class);

    private static final Map<String, Channel> NOTIFY_CHANNELS = new HashMap<>();
    private static final Map<String, Context> NOTIFY_CONTEXT = new HashMap<>();
    private static final DelayQueue<NotifyDelayed> DELAY_QUEUE = new DelayQueue<>();

    public Notify() {
        Dew.dewConfig.getNotifies().forEach((key, value) -> {
            Channel channel = null;
            try {
                channel = (Channel) Class.forName("com.tairanchina.csp.dew.core.notify." + value.getType() + "Channel").newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error("Not exist notify type:" + value.getType());
                System.exit(1);
            }
            channel.init(value);
            NOTIFY_CHANNELS.put(key, channel);
            NOTIFY_CONTEXT.put(key, new Context());
        });
        delaySend();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                NOTIFY_CHANNELS.values().forEach(Channel::destroy)));
    }

    public void sendAsync(String flag, String content) {
        sendAsync(flag, content, "");
    }

    public void sendAsync(String flag, String content, String title) {
        sendAsync(flag, content, title, new HashSet<>());
    }

    public void sendAsync(String flag, String content, String title, Set<String> specialReceivers) {
        Dew.Util.newThread(() -> send(flag, content, title, specialReceivers));
    }

    public void sendAsync(String flag, Throwable content) {
        sendAsync(flag, content, "");
    }

    public void sendAsync(String flag, Throwable content, String title) {
        sendAsync(flag, content, title, new HashSet<>());
    }

    public void sendAsync(String flag, Throwable content, String title, Set<String> specialReceivers) {
        Dew.Util.newThread(() -> send(flag, content, title, specialReceivers));
    }

    public Resp<Void> send(String flag, String content) {
        return send(flag, content, "");
    }

    public Resp<Void> send(String flag, String content, String title) {
        return send(flag, content, title, new HashSet<>());
    }

    public Resp<Void> send(String flag, Throwable content) {
        return send(flag, content, "");
    }

    public Resp<Void> send(String flag, Throwable content, String title) {
        return send(flag, content, title, new HashSet<>());
    }

    public Resp<Void> send(String flag, Throwable content, String title, Set<String> specialReceivers) {
        return send(flag, Arrays.stream(content.getStackTrace()).map(StackTraceElement::toString).limit(10).collect(Collectors.joining("\n")), title, specialReceivers);
    }

    public Resp<Void> send(String flag, String content, String title, Set<String> specialReceivers) {
        return send(flag, content, title, specialReceivers, false);
    }

    private Resp<Void> send(String flag, String content, String title, Set<String> specialReceivers, boolean force) {
        Channel channel = NOTIFY_CHANNELS.getOrDefault(flag, null);
        if (channel == null) {
            logger.trace("Not found notify flag[" + flag + "] in dew config.");
            return Resp.badRequest("Not found notify flag[" + flag + "] in dew config.");
        }
        DewConfig.Notify notifyConfig = Dew.dewConfig.getNotifies().get(flag);
        Context notifyContext = NOTIFY_CONTEXT.get(flag);
        // 策略处理
        if (!force && notifyConfig.getStrategy().getMinIntervalSec() != 0) {
            if (notifyContext.intervalNotifyCounter.getAndIncrement() > -1) {
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
            long currentTime = Long.valueOf(new SimpleDateFormat("yyyyMMddHHmm").format(time.getTime()));
            int currentShortTime = Integer.valueOf(new SimpleDateFormat("HHmm").format(time.getTime()));
            int dndStartShortTime = Integer.valueOf(dndTime[0].replace(":", ""));
            int dndEndShortTime = Integer.valueOf(dndTime[1].replace(":", ""));
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
                    Long.valueOf(new SimpleDateFormat("yyyyMMdd")
                            .format(time.getTime()) + dndTime[1].replace(":", "")))) {
                // 已在后几个免扰周期
                notifyContext.currentForceSendTimes.set(0);
            }
            if (!force && isDNDTime
                    && notifyConfig.getStrategy().getForceSendTimes() > notifyContext.currentForceSendTimes.incrementAndGet()) {
                logger.trace("Do Not Disturb time and try notify times <=" + notifyConfig.getStrategy().getForceSendTimes());
                return Resp.locked("Do Not Disturb time and try notify times <=" + notifyConfig.getStrategy().getForceSendTimes());
            }
        }
        Set<String> receivers = new HashSet<>();
        if (isDNDTime) {
            receivers.addAll(notifyConfig.getDndTimeReceivers());
        } else {
            receivers.addAll(notifyConfig.getDefaultReceivers());
            receivers.addAll(specialReceivers);
        }
        // 格式化
        title = (title != null ? title : "") + " FROM " + Dew.Info.instance + " BY " + flag;
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

    private void delaySend() {
        Dew.Util.newThread(() -> {
            while (true) {
                try {
                    NotifyDelayed notifyDelayed = DELAY_QUEUE.take();
                    String flag = notifyDelayed.getFlag();
                    Context notifyContext = NOTIFY_CONTEXT.get(flag);
                    long notifyCounter = notifyContext.intervalNotifyCounter.getAndSet(-1);
                    int delayMs = notifyDelayed.getDelayMs();
                    Set<String> specialReceivers = notifyDelayed.getSpecialReceivers();
                    Dew.Util.newThread(() ->
                            send(flag, "在最近的[" + delayMs / 1000 + "]秒内发生了[" + notifyCounter + "]次通知请求。", "延时通知", specialReceivers, true));
                } catch (InterruptedException e) {
                    logger.error("Send delay notify error.", e);
                }
            }
        });
    }

    static class Context {

        AtomicLong intervalNotifyCounter = new AtomicLong(-1);
        AtomicLong lastDNDEndTime = new AtomicLong(0);
        AtomicInteger currentForceSendTimes = new AtomicInteger(0);

    }

    static class NotifyDelayed implements Delayed {

        private String flag;
        private Set<String> specialReceivers;
        private int delayMs;
        private long expireMs;

        public String getFlag() {
            return flag;
        }

        public Set<String> getSpecialReceivers() {
            return specialReceivers;
        }

        public int getDelayMs() {
            return delayMs;
        }

        public NotifyDelayed(String flag, Set<String> specialReceivers, int delayMs) {
            this.flag = flag;
            this.specialReceivers = specialReceivers;
            this.delayMs = delayMs;
            this.expireMs = delayMs + System.currentTimeMillis();
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
