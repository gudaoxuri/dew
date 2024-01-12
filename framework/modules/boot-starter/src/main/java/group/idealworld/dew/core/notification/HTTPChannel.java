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
