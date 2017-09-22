package your.group;


import com.ecfront.dew.core.Dew;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 在根路径创建用于初始化数据/行为的类
 * <p>
 * 减少滥用PostConstruct造成的不可控因素
 */
@Component
public class PetstoreInitiator {

    @PostConstruct
    public void init() {
        // 初始宠物表
        Dew.ds().jdbc().execute("CREATE TABLE pet\n" +
                "(\n" +
                "id int primary key auto_increment,\n" +
                "type varchar(50),\n" +
                "price decimal(11,4) not null,\n" +
                "create_time datetime,\n" +
                "update_time datetime,\n" +
                "enabled bool\n" +
                ")");
        // 初始化客户表
        Dew.ds().jdbc().execute("CREATE TABLE customer\n" +
                "(\n" +
                "id int primary key auto_increment,\n" +
                "name varchar(50)\n" +
                ")");
        // 初始化订单表
        Dew.ds().jdbc().execute("CREATE TABLE t_order\n" +
                "(\n" +
                "id int primary key auto_increment,\n" +
                "pet_id int,\n" +
                "customer_id int,\n" +
                "price decimal(11,4) not null,\n" +
                "create_time datetime \n" +
                ")");
    }

}
