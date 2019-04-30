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

package ms.dew.core.auth;

import com.ecfront.dew.common.$;
import ms.dew.Dew;
import ms.dew.core.DewConfig;
import ms.dew.core.DewContext;
import ms.dew.core.auth.dto.OptInfo;
import ms.dew.core.cluster.ClusterCache;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 基础登录鉴权适配器.
 *
 * @author gudaoxuri
 * @author gjason
 */
public class BasicAuthAdapter implements AuthAdapter {

    // token存储key : <token>:<opt info>
    private static final String TOKEN_INFO_FLAG = "dew:auth:token:info:";
    // AccountCode 关联 Tokens : <account code>:<token kind##current time>:<token>
    private static final String TOKEN_ID_REL_FLAG = "dew:auth:token:id:rel:";

    private static final String AUTH_DS_FLAG = "__auth__";

    private static ClusterCache cache;

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        if (Dew.cluster.caches.exist(AUTH_DS_FLAG)) {
            cache = Dew.cluster.caches.instance(AUTH_DS_FLAG);
        } else {
            cache = Dew.cluster.cache;
        }
        if (!Dew.dewConfig.getSecurity().getTokenKinds().containsKey(OptInfo.DEFAULT_TOKEN_KIND_FLAG)) {
            // 赋值一个默认的kind
            Dew.dewConfig.getSecurity().getTokenKinds().put(OptInfo.DEFAULT_TOKEN_KIND_FLAG, new DewConfig.Security.TokenKind());
        }
    }

    @Override
    public <E extends OptInfo> Optional<E> getOptInfo(String token) {
        String optInfoStr = cache.get(TOKEN_INFO_FLAG + token);
        if (optInfoStr != null && !optInfoStr.isEmpty()) {
            return Optional.of($.json.toObject(optInfoStr, DewContext.getOptInfoClazz()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void removeOptInfo(String token) {
        Optional<OptInfo> tokenInfoOpt = getOptInfo(token);
        if (tokenInfoOpt.isPresent()) {
            cache.del(TOKEN_INFO_FLAG + token);
            removeOldToken(tokenInfoOpt.get().getAccountCode(), tokenInfoOpt.get().getTokenKind());
        }
    }

    @Override
    public <E extends OptInfo> void setOptInfo(E optInfo) {
        if (!cache.setnx(TOKEN_INFO_FLAG + optInfo.getToken(), $.json.toJsonString(optInfo),
                Dew.dewConfig.getSecurity().getTokenKinds().get(optInfo.getTokenKind()).getExpireSec())) {
            return;
        }
        cache.hset(TOKEN_ID_REL_FLAG + optInfo.getAccountCode(),
                optInfo.getTokenKind() + "##" + System.currentTimeMillis(), optInfo.getToken());

        removeOldToken(optInfo.getAccountCode(), optInfo.getTokenKind());
    }

    private void removeOldToken(Object accountCode, String tokenKind) {
        // 当前 token kind 要求保留的历史版本数
        int revisionHistoryLimit = Dew.dewConfig.getSecurity().getTokenKinds().get(tokenKind).getRevisionHistoryLimit();
        // 当前 account code 关联的所有 token
        Map<String, String> tokenKinds = cache.hgetAll(TOKEN_ID_REL_FLAG + accountCode);
        tokenKinds.keySet().stream()
                // 按创建时间倒序
                .sorted((i1, i2) ->
                        Long.compare(Long.valueOf(i2.split("##")[1]), Long.valueOf(i1.split("##")[1])))
                // 按 token kind 分组
                .collect(Collectors.groupingBy(i -> i.split("##")[0]))
                .forEach((key, value) -> {
                    value.stream()
                            .filter(tokenInfo ->
                                    // 当前token已过期
                                    !cache.exists(TOKEN_INFO_FLAG + tokenKinds.get(tokenInfo)))
                            .forEach(tokenInfo ->
                                    // 删除过期的关联token
                                    cache.hdel(TOKEN_ID_REL_FLAG + accountCode, tokenInfo));
                    if (key.equals(tokenKind) && value.size() > revisionHistoryLimit + 1) {
                        // 版本处理
                        value.subList(revisionHistoryLimit + 1, value.size())
                                .forEach(tokenInfo -> {
                                    // 删除多余版本
                                    cache.del(TOKEN_INFO_FLAG + tokenKinds.get(tokenInfo));
                                    cache.hdel(TOKEN_ID_REL_FLAG + accountCode, tokenInfo);
                                });
                    }
                });
    }

}
