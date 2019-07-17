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
import org.joox.JOOX;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * To-do verify.
 *
 * @author gudaoxuri
 */
public class TodoVerify extends BasicProcessor implements Verify {

    @Override
    public void doVerify(String buildPath, String expectedResPath) throws Exception {
        loadConfig();

        // verify parent
        String metaData = $.http.get(itSnapshotRepositoryUrl + "ms/dew/devops/it/todo-parent/2.0.0-beta1/maven-metadata.xml");
        Document doc = JOOX.builder().parse(new ByteArrayInputStream(metaData.getBytes()));
        String lastUpdateTime = JOOX.$(doc).find("lastUpdated").get(0).getTextContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        long offsetMinutes = (System.currentTimeMillis() - sdf.parse(lastUpdateTime).getTime()) / 1000 / 60 / 60;
        Assert.assertTrue("parent | last deploy time check", offsetMinutes < 5);

        // verify common
        metaData = $.http.get(itSnapshotRepositoryUrl + "ms/dew/devops/it/todo-common/2.0.0-beta1/maven-metadata.xml");
        doc = JOOX.builder().parse(new ByteArrayInputStream(metaData.getBytes()));
        lastUpdateTime = JOOX.$(doc).find("lastUpdated").get(0).getTextContent();
        sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        offsetMinutes = (System.currentTimeMillis() - sdf.parse(lastUpdateTime).getTime()) / 1000 / 60 / 60;
        Assert.assertTrue("common | last deploy time check", offsetMinutes < 5);

        String basePath = new File(buildPath).getParentFile().getPath() + File.separator;
        // verify kernel
        expectedResPath = basePath
                + "backend" + File.separator
                + "services" + File.separator
                + "kernel" + File.separator
                + "expected" + File.separator;
        buildPath = basePath + "backend" + File.separator + "services" + File.separator + "kernel" + File.separator + "target" + File.separator;
        Assert.assertFalse("kernel | exist dockerFile", new File(buildPath + "dew_build" + File.separator + "Dockerfile").exists());
        Assert.assertFalse("kernel | exist run-java.sh", new File(buildPath + "dew_build" + File.separator + "run-java.sh").exists());
        Assert.assertFalse("kernel | exist serv.jar", new File(buildPath + "dew_build" + File.separator + "serv.jar").exists());
        Assert.assertTrue("kernel | exist Deployment.yaml", new File(buildPath + "dew_release" + File.separator + "Deployment.yaml").exists());
        Assert.assertTrue("kernel | exist Service.yaml", new File(buildPath + "dew_release" + File.separator + "Service.yaml").exists());
        verifyResourceDescriptors("kernel | match Deployment.yaml", $.file.readAllByPathName(expectedResPath + "Deployment.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Deployment.yaml", "UTF-8"));
        verifyResourceDescriptors("kernel | match Service.yaml", $.file.readAllByPathName(expectedResPath + "Service.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Service.yaml", "UTF-8"));
        // verify compute
        expectedResPath = basePath
                + "backend" + File.separator
                + "services" + File.separator
                + "compute" + File.separator
                + "expected" + File.separator;
        buildPath = basePath + "backend" + File.separator + "services" + File.separator + "compute" + File.separator + "target" + File.separator;
        Assert.assertFalse("compute | exist dockerFile", new File(buildPath + "dew_build" + File.separator + "Dockerfile").exists());
        Assert.assertFalse("compute | exist run-java.sh", new File(buildPath + "dew_build" + File.separator + "run-java.sh").exists());
        Assert.assertFalse("compute | exist serv.jar", new File(buildPath + "dew_build" + File.separator + "serv.jar").exists());
        Assert.assertTrue("compute | exist Deployment.yaml", new File(buildPath + "dew_release" + File.separator + "Deployment.yaml").exists());
        Assert.assertTrue("compute | exist Service.yaml", new File(buildPath + "dew_release" + File.separator + "Service.yaml").exists());
        verifyResourceDescriptors("compute | match Deployment.yaml", $.file.readAllByPathName(expectedResPath + "Deployment.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Deployment.yaml", "UTF-8"));
        verifyResourceDescriptors("compute | match Service.yaml", $.file.readAllByPathName(expectedResPath + "Service.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Service.yaml", "UTF-8"));
        // verify notifier
        expectedResPath = basePath
                + "backend" + File.separator
                + "services" + File.separator
                + "notifier" + File.separator
                + "expected" + File.separator;
        buildPath = basePath + "backend" + File.separator + "services" + File.separator + "notifier" + File.separator + "target" + File.separator;
        Assert.assertFalse("notifier | exist dockerFile", new File(buildPath + "dew_build" + File.separator + "Dockerfile").exists());
        Assert.assertFalse("notifier | exist run-java.sh", new File(buildPath + "dew_build" + File.separator + "run-java.sh").exists());
        Assert.assertFalse("notifier | exist serv.jar", new File(buildPath + "dew_build" + File.separator + "serv.jar").exists());
        Assert.assertTrue("notifier | exist Deployment.yaml", new File(buildPath + "dew_release" + File.separator + "Deployment.yaml").exists());
        Assert.assertTrue("notifier | exist Service.yaml", new File(buildPath + "dew_release" + File.separator + "Service.yaml").exists());
        verifyResourceDescriptors("notifier | match Deployment.yaml", $.file.readAllByPathName(expectedResPath + "Deployment.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Deployment.yaml", "UTF-8"));
        verifyResourceDescriptors("notifier | match Service.yaml", $.file.readAllByPathName(expectedResPath + "Service.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Service.yaml", "UTF-8"));
        // verify frontend
        expectedResPath = basePath
                + "frontend" + File.separator
                + "expected" + File.separator;
        buildPath = basePath + "frontend" + File.separator + "target" + File.separator;
        Assert.assertTrue("frontend | exist dockerFile", new File(buildPath + "dew_build" + File.separator + "Dockerfile").exists());
        Assert.assertTrue("frontend | exist dist", new File(buildPath + "dew_build" + File.separator + "dist").exists());
        Assert.assertTrue("frontend | exist Deployment.yaml", new File(buildPath + "dew_release" + File.separator + "Deployment.yaml").exists());
        Assert.assertTrue("frontend | exist Service.yaml", new File(buildPath + "dew_release" + File.separator + "Service.yaml").exists());
        verifyResourceDescriptors("frontend | match Deployment.yaml", $.file.readAllByPathName(expectedResPath + "Deployment.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Deployment.yaml", "UTF-8"));
        verifyResourceDescriptors("frontend | match Service.yaml", $.file.readAllByPathName(expectedResPath + "Service.yaml", "UTF-8"),
                $.file.readAllByPathName(buildPath + "dew_release" + File.separator + "Service.yaml", "UTF-8"));
    }
}
