package com.tairanchina.csp.dew.example.mybatisplus;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.example.mybatisplus.entity.User;
import com.tairanchina.csp.dew.example.mybatisplus.service.UserService;
import com.tairanchina.csp.dew.jdbc.DewDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by panshuai on 17/6/26.
 */
@Component
@ConditionalOnClass(Dew.class)
public class MybatisplusExampleInitiator {

    private static final Logger logger = LoggerFactory.getLogger(MybatisplusExampleInitiator.class);

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        ((DewDS) Dew.ds()).jdbc().execute("CREATE TABLE user\n" +
                "(\n" +
                "test_id int primary key,\n" +
                "name varchar(50),\n" +
                "age INT ,\n" +
                "test_type INT ,\n" +
                "test_date datetime,\n" +
                "role long,\n" +
                "phone varchar(50)\n" +
                ")");

        User user = new User();
        user.setId(1L);
        user.setName("Tom");
        userService.insert(user);
        logger.info("======================");

        User exampleUser = userService.selectById(user.getId());
        exampleUser.setAge(18);
        userService.updateById(exampleUser);

        List<User> userList = userService.selectList(
                new EntityWrapper<User>().eq("name", "Tom")
        );
        logger.info("========userList=========size====={}", userList.size());

        Page<User> userListTemp = userService.selectPage(
                new Page<User>(1, 2),
                new EntityWrapper<User>().eq("name", "Tom")
        );
        logger.info("========userList=========size====={}", userListTemp.getRecords().size());

        userList = userService.selectList(
                new EntityWrapper<User>().eq("age", "19")
        );
        logger.info("========userList===age===19===size====={}", userList.size());

        userService.delete(new EntityWrapper<User>().eq("age", "19"));

        userList = userService.selectList(
                new EntityWrapper<User>().eq("age", "19")
        );
        logger.info("========userList=========size====={}", userList.size());

        List<String> ages = userService.ageGroup();
        logger.info("========userList=========ages====={}", ages);
    }
}
