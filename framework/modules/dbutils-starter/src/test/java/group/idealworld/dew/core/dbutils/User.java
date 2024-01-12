package group.idealworld.dew.core.dbutils;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class User {

    private long id;
    private String name;
    private String password;
    private int age;
    private float height1;
    private double height2;
    private Date createTime;
    private BigDecimal asset;
    private String txt;
    private boolean enable;

}
