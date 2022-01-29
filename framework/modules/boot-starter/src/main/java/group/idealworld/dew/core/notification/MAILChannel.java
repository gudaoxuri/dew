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

import com.ecfront.dew.common.Resp;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.MailerRegularBuilder;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.util.Set;

/**
 * 邮件通知.
 *
 * @author gudaoxuri
 */
public class MAILChannel extends AbsChannel {

    private String emailFrom;
    private Mailer mailSender;

    @Override
    protected boolean innerInit(NotifyConfig notifyConfig) {
        try {
            emailFrom = (String) notifyConfig.getArgs().get("from");
            String host = (String) notifyConfig.getArgs().get("host");
            int port = (Integer) notifyConfig.getArgs().get("port");
            String username = (String) notifyConfig.getArgs().get("username");
            String password = (String) notifyConfig.getArgs().get("password");
            String secure = (String) notifyConfig.getArgs().getOrDefault("secure", "");
            MailerRegularBuilder builder = MailerBuilder
                    .withSMTPServer(host, port, username, password);
            switch (secure.toLowerCase()) {
                case "tls":
                    builder.withTransportStrategy(TransportStrategy.SMTP_TLS);
                    break;
                case "ssl":
                    builder.withTransportStrategy(TransportStrategy.SMTPS);
                    break;
                default:
                    builder.withTransportStrategy(TransportStrategy.SMTP);
            }
            mailSender = builder.buildMailer();
            mailSender.testConnection();
            return true;
        } catch (NullPointerException ex) {
            logger.error("Notify Mail channel init error,missing [from] [host] [port] [username] [password] parameters", ex);
            throw ex;
        }
    }

    @Override
    protected void innerDestroy(NotifyConfig notifyConfig) {
        // Do nothing.
    }

    @Override
    public Resp<String> innerSend(String content, String title, Set<String> receivers) throws Exception {
        Email email = EmailBuilder.startingBlank()
                .from(emailFrom)
                .toMultiple(receivers)
                .withSubject(title)
                .withPlainText(content)
                .buildEmail();
        mailSender.sendMail(email);
        return Resp.success("");
    }

}
