package com.ecfront.dew.wsgateway;

import com.ecfront.dew.core.Dew;
import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class VertxServer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(VertxServer.class);

    @Autowired
    private WSConfig wsConfig;
    @Autowired
    private WSClient wsClient;
    @Autowired
    private DiscoveryClient discoveryClient;

    public VertxOptions getOpt() {
        VertxOptions opt = new VertxOptions();
        if (wsConfig.getEventLoopPoolSize() != 0) {
            opt.setEventLoopPoolSize(wsConfig.getEventLoopPoolSize());
        } else {
            opt.setEventLoopPoolSize(20);
        }
        if (wsConfig.getWorkerPoolSize() != 0) {
            opt.setWorkerPoolSize(wsConfig.getWorkerPoolSize());
        } else {
            opt.setWorkerPoolSize(200);
        }
        if (wsConfig.getInternalBlockingPoolSize() != 0) {
            opt.setInternalBlockingPoolSize(wsConfig.getInternalBlockingPoolSize());
        } else {
            opt.setInternalBlockingPoolSize(200);
        }
        if (wsConfig.getMaxEventLoopExecuteTime() != 0) {
            opt.setMaxEventLoopExecuteTime(wsConfig.getMaxEventLoopExecuteTime() * 1000000L);
        }
        if (wsConfig.getMaxWorkerExecuteTime() != 0) {
            opt.setMaxWorkerExecuteTime(wsConfig.getMaxWorkerExecuteTime() * 1000000L);
        }
        if (wsConfig.getWarningExceptionTime() != 0) {
            opt.setWarningExceptionTime(wsConfig.getWarningExceptionTime() * 1000000L);
        }
        return opt;
    }

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().websocketHandler(ws -> {
            Map<String, String> requestHeader = new HashMap<>();
            for (Map.Entry<String, String> entry : ws.headers()) {
                requestHeader.put(entry.getKey().toLowerCase(), entry.getValue());
            }
            logger.info(String.format("[Websockets] request [%s] from %s", ws.path(), Dew.Util.getRealIP(requestHeader, ws.remoteAddress().host())));
            if (ws.path().equals("/ws")) {
                String query;
                try {
                    query = URLDecoder.decode(ws.query(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("[Websockets] url parse error", e);
                    ws.reject();
                    return;
                }
                Map<String, String> params = new HashMap<>();
                Arrays.stream(query.split("&")).forEach(
                        item -> {
                            String[] i = item.split("=");
                            params.put(i[0], i[1]);
                        }
                );
                String token = params.get(Dew.Constant.TOKEN_VIEW_FLAG);
                if (token != null && !token.isEmpty()) {
                    WSPushManager.add(token, ws);
                    ws.frameHandler(wf ->
                            vertx.executeBlocking(
                                    (Handler<Future<String>>) event ->
                                            event.complete(wsClient.ws(token, wf.textData())),
                                    false,
                                    event ->
                                            ws.writeFinalTextFrame(event.result())));
                    ws.closeHandler(i -> WSPushManager.remove(token));
                    ws.exceptionHandler(i -> WSPushManager.remove(token));
                } else {
                    ws.reject();
                }
            } else {
                ws.reject();
            }
        }).requestHandler(http -> {
            if ((http.path().equals("/push") || http.path().equals("/push/")) && http.method() == HttpMethod.POST && http.query() != null) {
                String query;
                try {
                    query = URLDecoder.decode(http.query(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("[Http] url parse error", e);
                    return;
                }
                Map<String, String> params = new HashMap<>();
                Arrays.stream(query.split("&")).forEach(
                        item -> {
                            String[] i = item.split("=");
                            params.put(i[0], i[1]);
                        }
                );
                String token = params.containsKey(Dew.Constant.TOKEN_VIEW_FLAG) ? params.get(Dew.Constant.TOKEN_VIEW_FLAG) : null;
                http.bodyHandler(data -> {
                    String result = data.getString(0, data.length());
                    if (token != null && !token.isEmpty()) {
                        WSPushManager.push(token, result);
                    } else {
                        WSPushManager.pushAll(result);
                    }
                });
            }
            http.response().end("");
        }).listen(discoveryClient.getLocalServiceInstance().getPort(), "0.0.0.0", event -> {
            if(event.failed()){
                logger.error("[Vertx] Start error.",event.cause());
            }
        });
    }

}
