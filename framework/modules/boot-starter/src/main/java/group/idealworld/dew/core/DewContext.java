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

package group.idealworld.dew.core;

import com.ecfront.dew.common.$;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.auth.dto.OptInfo;

import java.util.Optional;

/**
 * Dew 上下文处理.
 *
 * @author gudaoxuri
 */
public class DewContext {

    private static final ThreadLocal<DewContext> CONTEXT = new ThreadLocal<>();

    private static Class optInfoClazz = OptInfo.class;

    /**
     * 当次请求的ID.
     */
    private String id;
    /**
     * 请求来源IP.
     */
    private String sourceIP;
    /**
     * 请求最初的URL.
     */
    private String requestUri;
    /**
     * 请求对应的token.
     */
    private String token;
    /**
     * 请求对应的token kind.
     */
    private String tokenKind;

    private Optional innerOptInfo = Optional.empty();

    /**
     * Gets opt info clazz.
     *
     * @param <E> the type parameter
     * @return the opt info clazz
     */
    public static <E extends OptInfo> Class<E> getOptInfoClazz() {
        return optInfoClazz;
    }

    /**
     * 设置自定义的OptInfo.
     *
     * @param <E>          the type parameter
     * @param optInfoClazz the opt info clazz
     */
    public static <E extends OptInfo> void setOptInfoClazz(Class<E> optInfoClazz) {
        DewContext.optInfoClazz = optInfoClazz;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public static DewContext getContext() {
        DewContext cxt = CONTEXT.get();
        if (cxt == null) {
            cxt = new DewContext();
            cxt.id = $.field.createUUID();
            cxt.sourceIP = Dew.Info.ip;
            cxt.requestUri = "";
            cxt.token = "";
            setContext(cxt);
        }
        return cxt;
    }

    /**
     * Sets context.
     *
     * @param context the context
     */
    public static void setContext(DewContext context) {
        if (context.token == null) {
            context.token = "";
        }
        CONTEXT.set(context);
    }

    /**
     * Exist.
     *
     * @return <b>true</b> if existed
     */
    public static boolean exist() {
        return CONTEXT.get() != null;
    }

    /**
     * Opt info optional.
     *
     * @param <E> the type parameter
     * @return the optional
     */
    public <E extends OptInfo> Optional<E> optInfo() {
        if (innerOptInfo.isPresent()) {
            return innerOptInfo;
        }
        if (token != null && !token.isEmpty()) {
            innerOptInfo = Dew.auth.getOptInfo(token);
        } else {
            innerOptInfo = Optional.empty();
        }
        return innerOptInfo;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets source ip.
     *
     * @return the source ip
     */
    public String getSourceIP() {
        return sourceIP;
    }

    /**
     * Sets source ip.
     *
     * @param sourceIP the source ip
     */
    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param token the token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets token kind.
     *
     * @return the token kind
     */
    public String getTokenKind() {
        return tokenKind;
    }

    /**
     * Sets token kind.
     *
     * @param tokenKind the token kind
     */
    public void setTokenKind(String tokenKind) {
        this.tokenKind = tokenKind;
    }

    /**
     * Gets request uri.
     *
     * @return the request uri
     */
    public String getRequestUri() {
        return requestUri;
    }

    /**
     * Sets request uri.
     *
     * @param requestUri the request uri
     */
    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    /**
     * Sets inner opt info.
     *
     * @param innerOptInfo the inner opt info
     */
    public void setInnerOptInfo(Optional innerOptInfo) {
        this.innerOptInfo = innerOptInfo;
    }

}
