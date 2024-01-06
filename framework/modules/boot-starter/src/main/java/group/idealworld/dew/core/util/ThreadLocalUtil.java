package group.idealworld.dew.core.util;

/**
 * thread local
 *
 * @author rpf
 */
public class ThreadLocalUtil<T> {

    /**
     * store thread local variables
     */
    private final ThreadLocal<T> threadLocal = new ThreadLocal<>();

    /**
     * set value
     *
     * @param value the value
     */
    public void set(T value) {
        threadLocal.set(value);
    }

    /**
     * get thread local variables
     *
     * @return the local variables
     */
    public T get() {
        return threadLocal.get();
    }

    /**
     * remove thread local variables
     */
    public void remove() {
        threadLocal.remove();
    }

}
