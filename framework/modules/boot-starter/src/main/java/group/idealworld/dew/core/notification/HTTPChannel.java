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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.ecfront.dew.common.Resp;

import java.util.Set;

/**
 * HTTP通知.
 *
 * @author gudaoxuri
 */
public class HTTPChannel extends AbsChannel {

    private String httpUrl = "";

    @Override
    protected boolean innerInit(NotifyConfig notifyConfig) {
        if (notifyConfig.getArgs().containsKey("url")) {
            httpUrl = (String) notifyConfig.getArgs().get("url");
            return true;
        } else {
            logger.error("Notify HTTP channel init error,missing [url] parameter");
            return false;
        }
    }

    @Override
    protected void innerDestroy(NotifyConfig notifyConfig) {
        // Do nothing.
    }

    @Override
    protected Resp<String> innerSend(String content, String title, Set<String> receivers) {
        var jsonReceivers = $.json.createArrayNode();
        receivers.forEach(jsonReceivers::add);
        HttpHelper.ResponseWrap result = $.http.postWrap(httpUrl,
                $.json.createObjectNode()
                        .put("title", title)
                        .put("content", content)
                        .set("receivers", jsonReceivers));
        return result.statusCode == 200 ? Resp.success("") : Resp.badRequest(result.statusCode + "");
    }

}
