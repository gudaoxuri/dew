/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.core.hystrix;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import ms.dew.Dew;
import ms.dew.core.DewCloudConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FailureEventNotifier extends HystrixEventNotifier {

    private static final Logger logger = LoggerFactory.getLogger(FailureEventNotifier.class);

    private DewCloudConfig dewCloudConfig;

    private Set<String> notifyIncludeKeys = new HashSet<>();
    private Set<String> notifyExcludeKeys = new HashSet<>();

    // key.name -> eventType.names
    private Map<String, Map<String, String>> failureInfo = new ConcurrentReferenceHashMap<>(50, ConcurrentReferenceHashMap.ReferenceType.SOFT);

    public FailureEventNotifier(DewCloudConfig dewCloudConfig) {
        this.dewCloudConfig = dewCloudConfig;
    }

    @PostConstruct
    public void init() {
        this.notifyIncludeKeys.addAll(Arrays.asList(dewCloudConfig.getError().getNotifyIncludeKeys()));
        this.notifyExcludeKeys.addAll(Arrays.asList(dewCloudConfig.getError().getNotifyExcludeKeys()));
    }

    @Override
    public void markEvent(HystrixEventType eventType, HystrixCommandKey commandKey) {
        if (eventType == HystrixEventType.SUCCESS) {
            failureInfo.remove(commandKey.name());
            return;
        }
        if (!notifyIncludeKeys.isEmpty() && !notifyIncludeKeys.contains(commandKey.name())) {
            return;
        }
        if (!notifyExcludeKeys.isEmpty() && notifyExcludeKeys.contains(commandKey.name())) {
            return;
        }
        if (!dewCloudConfig.getError().getNotifyEventTypes().contains(eventType.name())) {
            return;
        }
        failureInfo.putIfAbsent(commandKey.name(), new HashMap<>());
        failureInfo.get(commandKey.name()).put(eventType.name(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (!failureInfo.isEmpty()) {
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
            Dew.notify.sendAsync(
                    dewCloudConfig.getError().getNotifyFlag(),
                    stringBuilder.toString(),
                    dewCloudConfig.getError().getNotifyTitle()
            );
        }
    }

}
