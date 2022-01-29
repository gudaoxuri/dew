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

import java.util.Set;

/**
 * 通知通道.
 *
 * @author gudaoxuri
 */
public interface Channel {

    /**
     * 初始化通道.
     *
     * @param notifyConfig the notify config
     */
    void init(NotifyConfig notifyConfig);

    /**
     * 销毁通道.
     */
    void destroy();

    /**
     * 发送消息.
     *
     * @param content   消息内容
     * @param title     消息标题
     * @param receivers 接收人列表
     * @return 是否成功
     */
    boolean send(String content, String title, Set<String> receivers);

}
