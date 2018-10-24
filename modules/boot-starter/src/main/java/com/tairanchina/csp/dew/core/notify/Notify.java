package com.tairanchina.csp.dew.core.notify;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Notify {

    private static final Logger logger = LoggerFactory.getLogger(Notify.class);

    private static final Map<String, Channel> NOTIFY_CHANNELS = new HashMap<>();
    private static final Map<String, Context> NOTIFY_CONTEXT = new HashMap<>();

    {
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

    public Resp<Void> send(String flag, String content) {
        return send(flag, content, "");
    }

    public Resp<Void> send(String flag, String content, String title) {
        return send(flag, content, title, new HashSet<>());
    }

    public Resp<Void> send(String flag, String content, String title, Set<String> specialReceivers) {
        Channel channel = NOTIFY_CHANNELS.getOrDefault(flag, null);
        if (channel == null) {
            logger.trace("Not found notify flag[" + flag + "] in dew config.");
            return Resp.badRequest("Not found notify flag[" + flag + "] in dew config.");
        }
        DewConfig.Notify notifyConfig = Dew.dewConfig.getNotifies().get(flag);
        Context notifyContext = NOTIFY_CONTEXT.get(flag);
        // 策略处理
        if (notifyConfig.getStrategy().getMinIntervalSec() != 0
                && (System.currentTimeMillis() - notifyContext.getLastNotifyTime()) / 1000 < notifyConfig.getStrategy().getMinIntervalSec()) {
            logger.trace("Notify frequency must be > " + notifyConfig.getStrategy().getMinIntervalSec() + "s");
            return Resp.locked("Notify frequency must be > " + notifyConfig.getStrategy().getMinIntervalSec() + "s");
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
            if (currentTime > notifyContext.getLastDNDEndTime().getAndSet(
                    Long.valueOf(new SimpleDateFormat("yyyyMMdd")
                            .format(time.getTime()) + dndTime[1].replace(":", "")))) {
                // 已在后几个免扰周期
                notifyContext.getCurrentForceSendTimes().set(0);
            }
            if (isDNDTime
                    && notifyConfig.getStrategy().getForceSendTimes() > notifyContext.getCurrentForceSendTimes().incrementAndGet()) {
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
        title = (title != null ? title : "") + "[" + Dew.Info.instance + "]";
        content = content.replaceAll("\"","'");
        boolean result = channel.send(content, title, receivers);
        if (result) {
            notifyContext.setLastNotifyTime(System.currentTimeMillis());
            return Resp.success(null);
        } else {
            logger.warn("Notify send error.");
            return Resp.serverError("Notify send error.");
        }
    }

    static class Context {

        private long lastNotifyTime = 0;
        private AtomicLong lastDNDEndTime = new AtomicLong(0);
        private AtomicInteger currentForceSendTimes = new AtomicInteger(0);

        public long getLastNotifyTime() {
            return lastNotifyTime;
        }

        public void setLastNotifyTime(long lastNotifyTime) {
            this.lastNotifyTime = lastNotifyTime;
        }

        public AtomicLong getLastDNDEndTime() {
            return lastDNDEndTime;
        }

        public void setLastDNDEndTime(AtomicLong lastDNDEndTime) {
            this.lastDNDEndTime = lastDNDEndTime;
        }

        public AtomicInteger getCurrentForceSendTimes() {
            return currentForceSendTimes;
        }

        public void setCurrentForceSendTimes(AtomicInteger currentForceSendTimes) {
            this.currentForceSendTimes = currentForceSendTimes;
        }
    }


}
