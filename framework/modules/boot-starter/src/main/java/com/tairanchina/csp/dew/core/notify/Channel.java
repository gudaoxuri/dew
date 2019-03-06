package com.tairanchina.csp.dew.core.notify;

import com.tairanchina.csp.dew.core.DewConfig;

import java.util.Set;

public interface Channel {

    void init(DewConfig.Notify notifyConfig);

    void destroy();

    boolean send(String content, String title, Set<String> receivers);

}
