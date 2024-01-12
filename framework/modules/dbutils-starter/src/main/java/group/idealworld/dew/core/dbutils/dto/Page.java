package group.idealworld.dew.core.dbutils.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页辅助类.
 *
 * @param <E> 实体类型
 * @author gudaoxuri
 */
@Data
public class Page<E> {

    // start with 1
    private long pageNumber;
    private long pageSize;
    private long pageTotal;
    private long recordTotal;
    private List<E> objects;

}
