package com.ecfront.dew.wsgateway.auth;

import com.ecfront.dew.common.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ExchangeService {

    @RabbitListener(queues = "#{mqResourceAdd.name}")
    public void receiveResourceAdd(String message) throws InterruptedException {
        JsonNode data = JsonHelper.toJson(message);
        if (data.get("categroy").asText().isEmpty()) {
            LocalCacheContainer.addResource(data.get("code").asText(), data.get("method").asText(), data.get("url").asText());
        }
    }

    @RabbitListener(queues = "#{mqResourceRemove.name}")
    public void receiveResourceRemove(String message) throws InterruptedException {
        LocalCacheContainer.removeResource(message);
    }

    @RabbitListener(queues = "#{mqRoleAdd.name}")
    public void receiveRoleAdd(String message) throws InterruptedException {
        JsonNode data = JsonHelper.toJson(message);
        LocalCacheContainer.addRole(data.get("code").asText(), new HashSet<>(data.get("resources").findValuesAsText("code")));
    }

    @RabbitListener(queues = "#{mqRoleRemove.name}")
    public void receiveRoleRemove(String message) throws InterruptedException {
        LocalCacheContainer.removeRole(message);
    }

}
