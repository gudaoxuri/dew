package mock;

import group.idealworld.dew.devops.kernel.helper.GitHelper;
import group.idealworld.dew.devops.kernel.util.DewLog;

/**
 * Mock.
 *
 * @author gudaoxuri
 */
public class Mock {

    public Mock() {
        GitHelper.forceInit("GIT", "", new MockGitOpt(DewLog.build(this.getClass())));
    }

}
