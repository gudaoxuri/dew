package group.idealworld.dew.core.notification;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.exception.RTUnsupportedEncodingException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 钉钉通知.
 *
 * @author gudaoxuri
 */
public class DDChannel extends AbsChannel {

    private String ddUrl = "";
    private String msgType = "";

    @Override
    protected boolean innerInit(NotifyConfig notifyConfig) {
        if (notifyConfig.getArgs().containsKey("url")) {
            ddUrl = (String) notifyConfig.getArgs().get("url");
            msgType = (String) notifyConfig.getArgs().getOrDefault("msgType", "text");
            if (!msgType.equalsIgnoreCase("text")
                    && !msgType.equalsIgnoreCase("markdown")) {
                logger.error("Notify DingDing channel init error, [msgType] only support text/markdown");
                return false;
            }
            return true;
        } else {
            logger.error("Notify DingDing channel init error,missing [url] parameter");
            return false;
        }
    }

    @Override
    protected void innerDestroy(NotifyConfig notifyConfig) {
        // Do nothing.
    }

    @Override
    protected Resp<String> innerSend(String content, String title, Set<String> receivers) {
        switch (msgType) {
            case "text":
                if (receivers.isEmpty()) {
                    content = "     \"text\": {\"content\":\"" + title + "\r\n" + content + "\"},\n";
                } else {
                    content = "     \"text\": {\"content\":\"" + title + "\r\n" + content + "\n|"
                            + receivers.stream().map(r -> "@" + r).collect(Collectors.joining(" ")) + "\"},\n";
                }
                break;
            case "markdown":
                if (receivers.isEmpty()) {
                    content = "     \"markdown\": {\n"
                            + "         \"title\":\"" + title + "\",\n"
                            + "         \"text\":\"" + content + "\"\n"
                            + "     },\n";
                } else {
                    content = "     \"markdown\": {\n"
                            + "         \"title\":\"" + title + "\",\n"
                            + "         \"text\":\"" + content + "\n"
                            + "> " + receivers.stream().map(r -> "@" + r).collect(Collectors.joining(" ")) + "\"\n"
                            + "     },\n";
                }
                break;
            default:
                throw new RTUnsupportedEncodingException("[msgType] only support text/markdown");
        }
        HttpHelper.ResponseWrap result = $.http.postWrap(ddUrl,
                "{\n"
                        + "     \"msgtype\": \"" + msgType + "\",\n"
                        + content
                        + "    \"at\": {\n"
                        + "        \"atMobiles\": ["
                        + receivers.stream().map(r -> "\"" + r + "\"").collect(Collectors.joining(",")) + "], \n"
                        + "        \"isAtAll\": false\n"
                        + "    }\n"
                        + " }");
        return result.statusCode == 200 ? Resp.success("") : Resp.badRequest(result.statusCode + "");
    }

}
