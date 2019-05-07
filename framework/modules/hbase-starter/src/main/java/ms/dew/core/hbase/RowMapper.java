package ms.dew.core.hbase;

import org.apache.hadoop.hbase.client.Result;

/**
 * The row mapper of the data that hbase data.
 *
 * @param <T> the type parameter
 * @author 迹_Jason
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * The row action function.
     *
     * @param result hbase data
     * @param rowNum row number
     * @return T hope return object.
     * @throws Exception exception
     */
    T mapRow(Result result, int rowNum) throws Exception;
}
