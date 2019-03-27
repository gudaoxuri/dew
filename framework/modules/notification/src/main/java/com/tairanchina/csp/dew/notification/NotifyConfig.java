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

package com.tairanchina.csp.dew.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 通知配置.
 *
 * @author gudaoxuri
 */
public class NotifyConfig {

    /**
     * 钉钉通知.
     */
    public static final String TYPE_DD = "DD";
    /**
     * 邮件通知.
     */
    public static final String TYPE_MAIL = "MAIL";
    /**
     * HTTP通知.
     */
    public static final String TYPE_HTTP = "HTTP";

    private String type = TYPE_DD; // DD->钉钉 MAIL->邮件 HTTP->自定义HTTP Hook
    private Set<String> defaultReceivers = new HashSet<>();
    private Set<String> dndTimeReceivers = new HashSet<>();
    private Map<String, Object> args = new HashMap<>();
    private Strategy strategy = new Strategy();

    /**
     * 通知策略.
     */
    public static class Strategy {

        private int minIntervalSec = 0;
        // HH:mm-HH:mm，如果两个时间相等表示全天免扰，如果后者大于前者表示跨天免扰
        private String dndTime = "";
        private int forceSendTimes = 3;

        /**
         * 获取最少通知间隔时间.
         *
         * @return the min interval sec
         */
        public int getMinIntervalSec() {
            return minIntervalSec;
        }

        /**
         * 设置最少通知间隔时间.
         * <p>
         * 在此间隔内通知不会被发送而是转化成延时通知，延时发送
         *
         * @param minIntervalSec the min interval sec
         */
        public void setMinIntervalSec(int minIntervalSec) {
            this.minIntervalSec = minIntervalSec;
        }

        /**
         * 获取免扰时间.
         * <p>
         * 格式 HH:mm-HHmm
         *
         * @return the dnd time
         */
        public String getDndTime() {
            return dndTime;
        }

        /**
         * 设置免扰时间.
         * <p>
         * 格式 HH:mm-HHmm
         * <p>
         * e.g.
         * <p>
         * 00:00-00:00 全天免扰
         * 20:30-06:00 夜间免扰
         * 09:00-18:00 工作时间免扰
         *
         * @param dndTime the dnd time
         */
        public void setDndTime(String dndTime) {
            this.dndTime = dndTime;
        }

        /**
         * 获取在免扰时间强制通知要达到的通知次数.
         * <p>
         * 在达到此通知后即便在免扰时间也会发送通知
         *
         * @return the force send times
         */
        public int getForceSendTimes() {
            return forceSendTimes;
        }

        /**
         * 设置免扰时间强制通知要达到的通知次数.
         * <p>
         * 在达到此通知后即便在免扰时间也会发送通知
         *
         * @param forceSendTimes the force send times
         */
        public void setForceSendTimes(int forceSendTimes) {
            this.forceSendTimes = forceSendTimes;
        }

    }

    /**
     * 获取通知类型（通道）.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * 设置通知类型（通道）.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取默认通知人列表.
     *
     * @return the default receivers
     */
    public Set<String> getDefaultReceivers() {
        return defaultReceivers;
    }

    /**
     * 设置默认通知人列表.
     *
     * @param defaultReceivers the default receivers
     */
    public void setDefaultReceivers(Set<String> defaultReceivers) {
        this.defaultReceivers = defaultReceivers;
    }

    /**
     * 获取免扰时间通知人列表.
     * <p>
     * 免扰时间强制通知的接收人列表
     *
     * @return the dnd time receivers
     */
    public Set<String> getDndTimeReceivers() {
        return dndTimeReceivers;
    }

    /**
     * 设置免扰时间通知人列表.
     * <p>
     * 免扰时间强制通知的接收人列表
     *
     * @param dndTimeReceivers the dnd time receivers
     */
    public void setDndTimeReceivers(Set<String> dndTimeReceivers) {
        this.dndTimeReceivers = dndTimeReceivers;
    }

    /**
     * 获取不同类型的配置参数.
     *
     * @return the args
     */
    public Map<String, Object> getArgs() {
        return args;
    }

    /**
     * 设置不同类型的配置参数.
     *
     * @param args the args
     */
    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    /**
     * 获取策略.
     *
     * @return the strategy
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * 设置策略.
     *
     * @param strategy the strategy
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
