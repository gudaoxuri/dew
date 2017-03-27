package com.ecfront.dew.appexample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;
import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = APPExampleApplication.class, properties = {"spring.profiles.active=test"})
public class EmailTest {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Test
    public void testSendMail() throws Exception {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo("364341806@qq.com");
        message.setSubject("测试邮件");
        message.setText("测试邮件内容");
        mailSender.send(message);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(username);
        helper.setTo("364341806@qq.com");
        helper.setSubject("测试邮件：有附件");
        helper.setText("有附件的邮件");
        FileSystemResource file = new FileSystemResource(new File(this.getClass().getResource("/").getPath() + "1.png"));
        helper.addAttachment("附件-1.jpg", file);
        mailSender.send(mimeMessage);

        mimeMessage = mailSender.createMimeMessage();
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(username);
        helper.setTo("364341806@qq.com");
        helper.setSubject("测试邮件,带图片");
        helper.setText("<html><body><img src=\"cid:pic\" ></body></html>", true);
        file = new FileSystemResource(new File(this.getClass().getResource("/").getPath() + "1.png"));
        helper.addInline("pic", file);
        mailSender.send(mimeMessage);
    }

}
