package group.idealworld.dew.devops.kernel.plugin.appkind.frontend.node_native;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.release.BasicPrepareFlow;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Native Node frontend prepare flow.
 *
 * @author Liuhongcheng
 */
public class FrontendNativeNodePrepareFlow extends BasicPrepareFlow {
    @Override
    protected boolean needExecutePreparePackageCmd(FinalProjectConfig config, String currentPath) {
        return false;
    }

    @Override
    protected Optional<String> getPreparePackageCmd(FinalProjectConfig config, String currentPath) {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getPackageCmd(FinalProjectConfig config, String currentPath) {
        return Optional.empty();
    }

    protected void postPrepareBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        // 将前端项目文件 全部复制到 前端路径下的dist文件夹内
        FileUtils.copyDirectory(new File(config.getDirectory()), new File(config.getDirectory() + "dist"));

        FileUtils.deleteDirectory(new File(flowBasePath + "dist"));
        Files.move(Paths.get(config.getDirectory() + "dist"), Paths.get(flowBasePath + "dist"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    protected boolean existsReuseVersion(FinalProjectConfig config) {
        return false;
    }
}
