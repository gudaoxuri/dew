/*
 * Copyright 2019. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ms.dew.devops.it.verify;

import com.ecfront.dew.common.$;
import ms.dew.devops.it.BasicProcessor;
import org.junit.Assert;

import java.io.File;

/**
 * @author gudaoxuri
 */
public class HelloWorldBackendVerify extends BasicProcessor implements Verify {

    @Override
    public void doVerify(String buildPath, String expectedResPath) throws Exception {
        Assert.assertTrue("exist dockerFile", new File(buildPath + "dew_build" + File.separator + "Dockerfile").exists());
        Assert.assertTrue("exist run-java.sh", new File(buildPath + "dew_build" + File.separator + "run-java.sh").exists());
        Assert.assertTrue("exist serv.jar", new File(buildPath + "dew_build" + File.separator + "serv.jar").exists());
        Assert.assertTrue("serv.jar length > 30MB", new File(buildPath + "dew_build" + File.separator + "serv.jar").length() / 1024 / 1024 > 30);
        Assert.assertTrue("exist Deployment.yaml", new File(buildPath + "dew_release" + File.separator + "Deployment.yaml").exists());
        Assert.assertTrue("exist Service.yaml", new File(buildPath + "dew_release" + File.separator + "Service.yaml").exists());
        Assert.assertEquals("match Deployment.yaml", $.file.readAllByPathName(expectedResPath + "Deployment.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Deployment.yaml", "UTF-8"));
        Assert.assertEquals("match Service.yaml", $.file.readAllByPathName(expectedResPath + "Service.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Service.yaml", "UTF-8"));

    }
}
