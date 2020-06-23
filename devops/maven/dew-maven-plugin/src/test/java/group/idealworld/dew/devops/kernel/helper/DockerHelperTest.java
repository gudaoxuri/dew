/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.devops.kernel.helper;

import com.github.dockerjava.api.model.Image;
import group.idealworld.dew.devops.BasicTest;
import group.idealworld.dew.devops.kernel.util.DewLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

/**
 * Docker helper test.
 *
 * @author gudaoxuri
 */
public class DockerHelperTest extends BasicTest {

    /**
     * Before.
     */
    @Before
    public void before() {
        DockerHelper.init("", DewLog.build(this.getClass()),
                defaultDockerHost, defaultDockerRegistryUrl, defaultDockerRegistryUserName, defaultDockerRegistryPassword);
    }

    /**
     * Test all.
     */
    @Test
    public void testAll() {
        // Init Project
        DockerHelper.inst("").image.remove("harbor.dew.test/unit-test/test:1.0");
        DockerHelper.inst("").image.remove(defaultDockerRegistryHost + "/unit-test/test:1.0");
        DockerHelper.inst("").registry.removeImage(defaultDockerRegistryHost + "/unit-test/test");
        DockerHelper.inst("").registry.removeImage(defaultDockerRegistryHost + "/unit-test/test_copy");
        DockerHelper.inst("").registry.deleteProject("unit-test");
        Assert.assertTrue(DockerHelper.inst("").registry.createProject("unit-test"));

        DockerHelper.inst("").image.pull("alpine:3.6", false);
        List<Image> images = DockerHelper.inst("").image.list("alpine:3.6");
        Assert.assertEquals("alpine:3.6", images.get(0).getRepoTags()[0]);
        DockerHelper.inst("").image.build("harbor.dew.test/unit-test/test:1.0", this.getClass().getResource("/").getPath());
        DockerHelper.inst("").image.copy("harbor.dew.test/unit-test/test:1.0", defaultDockerRegistryHost + "/unit-test/test:1.0");
        Assert.assertEquals(1, DockerHelper.inst("").image.list(defaultDockerRegistryHost + "/unit-test/test:1.0").size());
        DockerHelper.inst("").image.push(defaultDockerRegistryHost + "/unit-test/test:1.0", true);
        DockerHelper.inst("").image.remove(defaultDockerRegistryHost + "/unit-test/test:1.0");
        Assert.assertEquals(0, DockerHelper.inst("").image.list(defaultDockerRegistryHost + "/unit-test/test:1.0").size());
        Assert.assertTrue(DockerHelper.inst("").registry.existImage(defaultDockerRegistryHost + "/unit-test/test:1.0"));
        // Test Copy Image
        Assert.assertTrue(DockerHelper.inst("").registry.copyImage("unit-test/test:1.0", "unit-test","test_copy"));
        // Test Remove Image
        DockerHelper.inst("").registry.removeImage(defaultDockerRegistryHost + "/unit-test/test:1.0");
        Assert.assertFalse(DockerHelper.inst("").registry.existImage(defaultDockerRegistryHost + "/unit-test/test:1.0"));

        // Test label
        Assert.assertNull(DockerHelper.inst("").registry.getLabel("commit", Optional.of("unit-test")));
        Assert.assertTrue(DockerHelper.inst("").registry.createOrUpdateLabel("commit", "ssssss", Optional.of("unit-test")));
        Assert.assertEquals("ssssss", DockerHelper.inst("").registry.getLabel("commit", Optional.of("unit-test")).getDescription());
        Assert.assertTrue(DockerHelper.inst("").registry.createOrUpdateLabel("commit", "ccccc", Optional.of("unit-test")));
        Assert.assertEquals("ccccc", DockerHelper.inst("").registry.getLabel("commit", Optional.of("unit-test")).getDescription());

    }

}
