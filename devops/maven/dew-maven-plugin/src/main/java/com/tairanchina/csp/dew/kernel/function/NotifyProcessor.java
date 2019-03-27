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

package com.tairanchina.csp.dew.kernel.function;

import com.tairanchina.csp.dew.kernel.Dew;
import com.tairanchina.csp.dew.notification.Notify;
import com.tairanchina.csp.dew.notification.NotifyConfig;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知处理.
 *
 * @author gudaoxuri
 */
public class NotifyProcessor {

    public static void init() {
        Map<String, NotifyConfig> configMap = Dew.Config.getProjects().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, config -> config.getValue().getNotify()));
        Notify.init(configMap, flag -> Dew.Config.getCurrentProject().getProfile());
    }

    public static void send(){

    }
}
