package com.ecfront.dew.core;

import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.core.dto.OptInfo;

import java.util.Optional;

public class DewContext {

    private String id;
    private String sourceIP;
    private String requestUri;
    private String token;
    private Optional<OptInfo> innerOptInfo = Optional.empty();

    public Optional<OptInfo> optInfo() {
        if (innerOptInfo.isPresent()) {
            return innerOptInfo;
        }
        if (token != null && !token.isEmpty()) {
            String result = Dew.cluster.cache.get(Dew.Constant.TOKEN_INFO_FLAG + token);
            if (result != null && !result.isEmpty()) {
                innerOptInfo = Optional.of(JsonHelper.toObject(result, OptInfo.class));
            } else {
                innerOptInfo = Optional.empty();
            }
        } else {
            innerOptInfo = Optional.empty();
        }
        return innerOptInfo;
    }

    private static ThreadLocal<DewContext> context = new ThreadLocal<>();

    public static DewContext getContext() {
        DewContext cxt = context.get();
        if (cxt == null) {
            cxt = new DewContext();
            cxt.id = Dew.Util.createUUID();
            cxt.sourceIP = Dew.Info.ip;
            cxt.requestUri = "";
            cxt.token = "";
            setContext(cxt);
        }
        return cxt;
    }

    public static void setContext(DewContext _context) {
        if (_context.token == null) {
            _context.token = "";
        }
        context.set(_context);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public static void setContext(ThreadLocal<DewContext> context) {
        DewContext.context = context;
    }
}
