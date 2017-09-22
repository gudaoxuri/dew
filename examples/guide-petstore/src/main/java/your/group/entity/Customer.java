package your.group.entity;

import com.ecfront.dew.core.entity.Column;
import com.ecfront.dew.core.entity.Entity;
import com.ecfront.dew.core.entity.PkColumn;

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
}
