package group.idealworld.dew.devops.it.todo.notifier.controller;

import group.idealworld.dew.Dew;
import group.idealworld.dew.devops.it.todo.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

/**
 * Notifier controller.
 *
 * @author gudaoxuri
 */
@RestController
public class NotifierController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifierController.class);

    /**
     * Process to-do add event.
     */
    @PostConstruct
    public void processTodoAddEvent() {
        // 使用Dew的集群MQ功能实现消息点对点接收
        Dew.cluster.mq.response(Constants.MQ_NOTIFY_TODO_ADD, todo -> LOGGER.info("Received add todo event :" + todo));
    }

    /**
     * Process to-do del event.
     */
    @PostConstruct
    public void processTodoDelEvent() {
        Dew.cluster.mq.response(Constants.MQ_NOTIFY_TODO_DEL,
                todoId -> LOGGER.info("Received delete todo event :" + todoId));
    }

}
