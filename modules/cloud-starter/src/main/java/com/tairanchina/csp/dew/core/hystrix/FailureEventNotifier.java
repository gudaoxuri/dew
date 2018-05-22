package com.tairanchina.csp.dew.core.hystrix;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewCloudConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FailureEventNotifier extends HystrixEventNotifier {

    private static final Logger logger = LoggerFactory.getLogger(FailureEventNotifier.class);

    private DewCloudConfig dewCloudConfig;

    private JavaMailSender mailSender;

    private String emailFrom;

    private String applicationName;

    private String profile;

    private Set<String> notifyIncludeKeys = new HashSet<>();
    private Set<String> notifyExcludeKeys = new HashSet<>();

    private long notifiedTime;
    // key.name -> eventType.names
    private Map<String, Map<String, String>> failureInfo = new ConcurrentReferenceHashMap<>(50, ConcurrentReferenceHashMap.ReferenceType.SOFT);

    private Executor executor = Executors.newSingleThreadExecutor();

    public FailureEventNotifier(DewCloudConfig dewCloudConfig, JavaMailSender mailSender, String emailFrom, String applicationName, String profile) {
        this.dewCloudConfig = dewCloudConfig;
        this.mailSender = mailSender;
        this.emailFrom = emailFrom;
        this.applicationName = applicationName;
        this.profile = profile;
    }

    @PostConstruct
    public void init() {
        this.notifiedTime = Instant.now().toEpochMilli() - dewCloudConfig.getError().getNotifyIntervalSec() * 1000;
        this.notifyIncludeKeys.addAll(Arrays.asList(dewCloudConfig.getError().getNotifyIncludeKeys()));
        this.notifyExcludeKeys.addAll(Arrays.asList(dewCloudConfig.getError().getNotifyExcludeKeys()));
    }

    @Override
    public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
        if (eventType == HystrixEventType.SUCCESS) {
            if (failureInfo.containsKey(key.name())) {
                failureInfo.remove(key.name());
            }
            return;
        }
        if (!notifyIncludeKeys.isEmpty() && !notifyIncludeKeys.contains(key.name())) {
            return;
        }
        if (!notifyExcludeKeys.isEmpty() && notifyExcludeKeys.contains(key.name())) {
            return;
        }
        if (!dewCloudConfig.getError().getNotifyEventTypes().contains(eventType.name())) {
            return;
        }
        failureInfo.putIfAbsent(key.name(), new HashMap<>());
        failureInfo.get(key.name()).put(eventType.name(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (Instant.now().toEpochMilli() - notifiedTime < dewCloudConfig.getError().getNotifyIntervalSec() * 1000) {
            return;
        }
        notifiedTime = Instant.now().toEpochMilli();
        if (!failureInfo.isEmpty()) {
            executor.execute(this::sendEmail);
        }
    }

    private void sendEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(dewCloudConfig.getError().getNotifyEmails().toArray(new String[]{}));
            message.setSubject(Dew.dewConfig.getBasic().getName() + dewCloudConfig.getError().getNotifyTitle() + "\t\tserviceId=" + applicationName+"\t\tprofile="+profile);
            StringBuilder stringBuilder = new StringBuilder();
            failureInfo.forEach((key, value) -> {
                stringBuilder.append("\r\n").append(
                        "异常方法:").append(key).append("\t类型:\r\n");
                value.forEach((k, v) -> stringBuilder.append("\t\t\t").append(k).append("\t\t").append(v).append(",\r\n"));
            });
            stringBuilder.append("\r\n");
            if (stringBuilder.capacity() < 8) {
                return;
            }
            message.setText("以下服务发生异常:" + stringBuilder.toString());
            mailSender.send(message);
            logger.info("邮件通知成功\t\t\tdetail:\t" + stringBuilder.toString());
        } catch (Exception e) {
            logger.error("邮件通知失败\t\t\tdetail：" + e.getMessage());
        }
    }
}
