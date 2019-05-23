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

package ms.dew.devops.it.todo.notifier.controller;

import ms.dew.Dew;
import ms.dew.devops.it.todo.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * Notifier controller.
 *
 * @author gudaoxuri
 */
@RestController
public class NotifierController {

    private static final Logger logger = LoggerFactory.getLogger(NotifierController.class);

    /**
     * Process to-do add event.
     */
    @PostConstruct
    public void processTodoAddEvent() {
        // 使用Dew的集群MQ功能实现消息点对点接收
        Dew.cluster.mq.response(Constants.MQ_NOTIFY_TODO_ADD, todo -> {
            logger.info("Received add todo event :" + todo);
        });
    }

    /**
     * Process to-do del event.
     */
    @PostConstruct
    public void processTodoDelEvent() {
        Dew.cluster.mq.response(Constants.MQ_NOTIFY_TODO_DEL, todoId -> {
            logger.info("Received delete todo event :" + todoId);
        });
    }

}
