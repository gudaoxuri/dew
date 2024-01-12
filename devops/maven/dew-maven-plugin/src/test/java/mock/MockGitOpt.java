package mock;

import group.idealworld.dew.devops.kernel.helper.GitOpt;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock Git Opt.
 *
 * @author gudaoxuri
 */
public class MockGitOpt extends GitOpt {

    protected MockGitOpt(Logger log) {
        super(log);
    }

    @Override
    public List<String> diff(String startCommitHash, String endCommitHash) {
        log.warn("Mock diff");
        return new ArrayList<>() {
            {
                // 有一个文件变更
                add("pom.xml");
            }
        };
    }

    @Override
    public String getCurrentBranch() {
        log.warn("Mock getCurrentBranch");
        return "mock";
    }

    @Override
    public String getCurrentCommit() {
        log.warn("Mock getCurrentCommit");
        return "00000000000000000000000000";
    }

    @Override
    public String getScmUrl() {
        log.warn("Mock getScmUrl");
        return "https://github.com/gudaoxuri/dew.git";
    }
}
