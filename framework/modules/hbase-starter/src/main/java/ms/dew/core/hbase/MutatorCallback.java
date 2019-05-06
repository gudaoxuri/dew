package ms.dew.core.hbase;

import org.apache.hadoop.hbase.client.BufferedMutator;

/**
 * Mutator action interface.
 *
 * @author 迹_Jason
 *
 */
@FunctionalInterface
public interface MutatorCallback {

    /**
     * Mutator action function.
     * @param mutator  mutator
     * @throws Throwable  Throwable exception
     */
    void doInMutator(BufferedMutator mutator) throws Throwable;
}
