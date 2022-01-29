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

import java.util.Set;

/**
 * 通知通道.
 *
 * @author gudaoxuri
 */
public abstract class AbsChannel implements Channel {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean isWork = false;

    private NotifyConfig notifyConfig;

    /**
     * 初始化通道.
     *
     * @param notifyConfig the notify config
     */
    @Override
    public void init(NotifyConfig notifyConfig) {
        isWork = innerInit(notifyConfig);
        if (isWork) {
            this.notifyConfig = notifyConfig;
            logger.debug("Init Notify channel:" + this.getClass().getSimpleName());
        }
    }

    /**
     * 初始化通道.
     *
     * @param notifyConfig the notify config
     */
    protected abstract boolean innerInit(NotifyConfig notifyConfig);

    /**
     * 销毁通道.
     */
    @Override
    public void destroy() {
        if (isWork) {
            logger.debug("Destroy Notify channel:" + this.getClass().getSimpleName());
            innerDestroy(notifyConfig);
        }
    }

    /**
     * 销毁通道.
     *
     * @param notifyConfig the notify config
     */
    protected abstract void innerDestroy(NotifyConfig notifyConfig);

    /**
     * 发送消息.
     *
     * @param content   消息内容
     * @param title     消息标题
     * @param receivers 接收人列表
     * @return 是否成功
     */
    @Override
    public boolean send(String content, String title, Set<String> receivers) {
        if (isWork) {
            logger.trace("Send Notify message [" + this.getClass().getSimpleName() + "]" + title);
            try {
                Resp<String> result = innerSend(content, title, receivers);
                if (result.ok()) {
                    return true;
                } else {
                    logger.error("Send Notify error [" + result.getCode() + "]" + result.getMessage());
                    return false;
                }
            } catch (Exception e) {
                logger.error("Send Notify error [" + this.getClass().getSimpleName() + "]" + title, e);
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 发送消息.
     *
     * @param content   消息内容
     * @param title     消息标题
     * @param receivers 接收人列表
     * @return 是否成功
     */
    protected abstract Resp<String> innerSend(String content, String title, Set<String> receivers) throws Exception;

}
