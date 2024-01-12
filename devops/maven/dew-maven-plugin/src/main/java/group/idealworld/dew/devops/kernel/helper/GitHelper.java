package group.idealworld.dew.devops.kernel.helper;

import org.slf4j.Logger;

/**
 * Git辅助类.
 * <p>
 * 使用多实例支持是为方便替换GitOpt为Mock对象以进行集成测试，详见测试实现
 *
 * @author gudaoxuri
 */
public class GitHelper extends MultiInstProcessor {

    /**
     * Init.
     *
     * @param log the log
     */
    public static void init(Logger log) {
        multiInit("GIT", "",
                () -> new GitOpt(log), "");
    }

    /**
     * Fetch GitOpt instance.
     *
     * @return GitOpt instance
     */
    public static GitOpt inst() {
        return multiInst("GIT", "");
    }

}
