package group.idealworld.dew.core.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分布式锁服务.
 *
 * @author gudaoxuri
 */
public interface ClusterLock {

    /**
     * The Logger.
     */
    Logger LOGGER = LoggerFactory.getLogger(ClusterLock.class);

    /**
     * 尝试加锁，加锁成功后执行对应的函数，执行完成自动解锁.
     * <p>
     * 推荐使用
     * {@link #tryLockWithFun(long waitMillSec, long leaseMillSec, VoidProcessFun fun)}
     *
     * @param fun 加锁成功后执行的函数
     */
    void tryLockWithFun(VoidProcessFun fun) throws Exception;

    /**
     * 尝试加锁，加锁成功后执行对应的函数，执行完成自动解锁.
     * <p>
     * 推荐使用
     * {@link #tryLockWithFun(long waitMillSec, long leaseMillSec, VoidProcessFun fun)}
     *
     * @param waitMillSec 等待时间，单位毫秒
     * @param fun         加锁成功后执行的函数
     */
    void tryLockWithFun(long waitMillSec, VoidProcessFun fun) throws Exception;

    /**
     * 尝试加锁，加锁成功后执行对应的函数，执行完成自动解锁.
     *
     * @param waitMillSec  等待毫秒数
     * @param leaseMillSec 锁释放毫秒数
     * @param fun          加锁成功后执行的函数
     */
    void tryLockWithFun(long waitMillSec, long leaseMillSec, VoidProcessFun fun) throws Exception;

    /**
     * 尝试加锁.
     * <p>
     * 推荐使用 {@link #tryLock(long waitMillSec, long leaseMillSec)}
     *
     * @return 是否加锁成功
     */
    boolean tryLock();

    /**
     * 尝试加锁.
     * <p>
     * 推荐使用 {@link #tryLock(long waitMillSec, long leaseMillSec)}
     *
     * @param waitMillSec 等待毫秒数
     * @return 是否加锁成功
     */
    boolean tryLock(long waitMillSec) throws InterruptedException;

    /**
     * 尝试加锁.
     *
     * @param waitMillSec  等待毫秒数
     * @param leaseMillSec 锁释放毫秒数，估计值，实际释放时间视各中间件的配置及执行环境会有一定偏差
     * @return 是否加锁成功
     */
    boolean tryLock(long waitMillSec, long leaseMillSec) throws InterruptedException;

    /**
     * 解锁操作，只有加锁的实例及线程才能解锁.
     *
     * @return 是否解锁成功
     */
    boolean unLock();

    /**
     * 强制解锁，不用匹配加锁的实例与线程.
     * <p>
     * 谨慎使用
     */
    void delete();

    /**
     * 判断是否有锁.
     *
     * @return true:有锁，false:没有锁
     */
    boolean isLocked();

}
