package group.idealworld.dew.devops.it.verify;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.it.BasicProcessor;
import org.junit.Assert;

import java.io.File;

/**
 * Hello world frontend verify.
 *
 * @author gudaoxuri
 */
public class HelloWorldFrontendVerify extends BasicProcessor implements Verify {

        @Override
        public void doVerify(String buildPath, String expectedResPath) throws Exception {
                Assert.assertTrue("exist dockerFile",
                                new File(buildPath + "dew_release" + File.separator + "Dockerfile").exists());
                Assert.assertTrue("exist dist", new File(buildPath + "dew_release" + File.separator + "dist").exists());
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
