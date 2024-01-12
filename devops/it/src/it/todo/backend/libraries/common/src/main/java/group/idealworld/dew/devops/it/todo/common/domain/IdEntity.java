package group.idealworld.dew.devops.it.todo.common.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Id entity.
 *
 * @author gudaoxuri
 */
@MappedSuperclass
public class IdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }
}
