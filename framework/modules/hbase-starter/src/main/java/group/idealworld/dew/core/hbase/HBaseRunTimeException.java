package group.idealworld.dew.core.hbase;

/**
 * The spring boot hbase cause error while run time.
 *
 * @author 迹_Jason
 */
public class HBaseRunTimeException extends RuntimeException {

    /**
     * Init the spring boot hbase exception.
     *
     * @param cause throwable
     */
    public HBaseRunTimeException(Throwable cause) {
        super(cause);
    }
}
