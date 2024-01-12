package group.idealworld.dew.core.hbase;

import org.apache.hadoop.hbase.client.Table;

/**
 * The hbase table action.
 *
 * @param <T> the type parameter
 * @author 迹_Jason
 */
@FunctionalInterface
public interface TableCallback<T> {
    /**
     * The hbase table action function.
     *
     * @param table table object
     * @return hope return object
     * @throws Throwable exception
     */
    T doInTable(Table table) throws Throwable;
}
