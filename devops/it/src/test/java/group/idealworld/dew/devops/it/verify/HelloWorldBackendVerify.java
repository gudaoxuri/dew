package group.idealworld.dew.devops.it.verify;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.it.BasicProcessor;
import org.junit.Assert;

import java.io.File;

/**
 * Hello world backend verify.
 *
 * @author gudaoxuri
 */
public class HelloWorldBackendVerify extends BasicProcessor implements Verify {

        @Override
        public void doVerify(String buildPath, String expectedResPath) throws Exception {
                Assert.assertTrue("exist dockerFile",
                                new File(buildPath + "dew_release" + File.separator + "Dockerfile").exists());
                Assert.assertTrue("exist run-java.sh",
                                new File(buildPath + "dew_release" + File.separator + "run-java.sh").exists());
                Assert.assertTrue("exist serv.jar",
                                new File(buildPath + "dew_release" + File.separator + "serv.jar").exists());
                Assert.assertTrue("serv.jar length > 30MB",
                                new File(buildPath + "dew_release" + File.separator + "serv.jar").length() / 1024
                                                / 1024 > 30);
                Assert.assertTrue("exist Deployment.yaml",
                                new File(buildPath + "dew_release" + File.separator + "Deployment.yaml").exists());
                Assert.assertTrue("exist Service.yaml",
                                new File(buildPath + "dew_release" + File.separator + "Service.yaml").exists());
                verifyResourceDescriptors("match Deployment.yaml",
                                $.file.readAllByPathName(expectedResPath + "Deployment.yaml", "UTF-8"),
                                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Deployment.yaml",
                                                "UTF-8"));
                verifyResourceDescriptors("match Service.yaml",
                                $.file.readAllByPathName(expectedResPath + "Service.yaml", "UTF-8"),
                                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Service.yaml",
                                                "UTF-8"));

        }
}
