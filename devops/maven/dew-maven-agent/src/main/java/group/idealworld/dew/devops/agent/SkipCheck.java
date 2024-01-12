package group.idealworld.dew.devops.agent;

import java.io.File;

/**
 * Skip check.
 *
 * @author gudaoxuri
 */
public class SkipCheck {

    /**
     * Check skip.
     *
     * @param basedDir the based dir
     * @return the result
     */
    public static boolean skip(File basedDir) {
        return new File(basedDir.getPath() + File.separator + "target" + File.separator + ".dew_skip_flag").exists();
    }

}
