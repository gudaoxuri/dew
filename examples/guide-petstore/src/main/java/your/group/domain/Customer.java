package your.group.domain;

import com.ecfront.dew.core.entity.Column;
import com.ecfront.dew.core.entity.Entity;
import com.ecfront.dew.core.entity.PkColumn;
import com.ecfront.dew.core.jdbc.DewDao;

import java.io.Serializable;

@Entity
public class Customer implements Serializable{

    @PkColumn
    private int id;
    @Column(notNull = true)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ActiveRecord $$ = new ActiveRecord();

    public static class ActiveRecord implements DewDao<Integer, Customer> {

    }
}
