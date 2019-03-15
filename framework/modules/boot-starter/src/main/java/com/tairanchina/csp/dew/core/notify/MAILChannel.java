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

package com.tairanchina.csp.dew.core.notify;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewConfig;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;

import java.util.Set;

public class MAILChannel extends AbsChannel {

    private String emailFrom;
    private JavaMailSender mailSender;

    @Override
    public boolean innerInit(DewConfig.Notify notifyConfig) {
        if (Dew.applicationContext.containsBean("mailSender")) {
            mailSender = (JavaMailSender) Dew.applicationContext.getBean("mailSender");
        }
        emailFrom = Dew.applicationContext.getEnvironment().getProperty("spring.mail.username", "");
        return mailSender != null && !StringUtils.isEmpty(emailFrom);
    }

    @Override
    public void innerDestroy(DewConfig.Notify notifyConfig) {

    }

    @Override
    public Resp<String> innerSend(String content, String title, Set<String> receivers) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(receivers.toArray(new String[]{}));
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
        return Resp.success("");
    }

}
