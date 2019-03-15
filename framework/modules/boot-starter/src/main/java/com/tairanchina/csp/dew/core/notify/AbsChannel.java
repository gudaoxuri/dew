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

package com.tairanchina.csp.dew.core.notify;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbsChannel implements Channel {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean isWork = false;

    private DewConfig.Notify notifyConfig;

    @Override
    public void init(DewConfig.Notify notifyConfig) {
        isWork = innerInit(notifyConfig);
        if (isWork) {
            this.notifyConfig = notifyConfig;
            logger.info("Init Notify channel:" + this.getClass().getSimpleName());
        }
    }

    abstract boolean innerInit(DewConfig.Notify notifyConfig);

    @Override
    public void destroy() {
        if (isWork) {
            logger.info("Destroy Notify channel:" + this.getClass().getSimpleName());
            innerDestroy(notifyConfig);
        }
    }

    abstract void innerDestroy(DewConfig.Notify notifyConfig);

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

    abstract Resp<String> innerSend(String content, String title, Set<String> receivers) throws Exception;

}
