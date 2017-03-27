package com.ecfront.dew.wsgateway;

import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSPushManager {

    private static final Logger logger = LoggerFactory.getLogger(WSPushManager.class);

    private static Map<String, ServerWebSocket> webSocketContainer = new ConcurrentHashMap<>();

    public static void add(String token, ServerWebSocket ws) {
        logger.info("[Websockets] [" + token + "] added.");
        webSocketContainer.put(token, ws);
    }

    public static void remove(String token) {
        logger.info("[Websockets] [" + token + "] removed.");
        webSocketContainer.remove(token);
    }

    public static void pushAll(String message) {
        webSocketContainer.values().forEach(i -> i.writeFinalTextFrame(message));
    }

    public static void push(String token, String message) {
        ServerWebSocket ws = webSocketContainer.get(token);
        if (ws != null) {
            ws.writeFinalTextFrame(message);
        } else {
            logger.warn("[Websockets] [" + token + "] not found.");
        }
    }
}
