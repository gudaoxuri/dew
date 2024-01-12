package group.idealworld.dew.core.notification;

import java.util.Set;

/**
 * 通知通道.
 *
 * @author gudaoxuri
 */
public interface Channel {

    /**
     * 初始化通道.
     *
     * @param notifyConfig the notify config
     */
    void init(NotifyConfig notifyConfig);

    /**
     * 销毁通道.
     */
    void destroy();

    /**
     * 发送消息.
     *
     * @param content   消息内容
     * @param title     消息标题
     * @param receivers 接收人列表
     * @return 是否成功
     */
    boolean send(String content, String title, Set<String> receivers);

}
