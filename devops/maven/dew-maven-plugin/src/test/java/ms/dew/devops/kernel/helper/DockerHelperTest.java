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

package ms.dew.devops.kernel.helper;

import com.github.dockerjava.api.model.Image;
import ms.dew.devops.BasicTest;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

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
        DockerHelper.init("", new SystemStreamLog(),
                defaultDockerHost, defaultDockerRegistryUrl, defaultDockerRegistryUserName, defaultDockerRegistryPassword);
    }

    /**
     * Test all.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testAll() throws IOException {
        DockerHelper.inst("").registry.remove("harbor.trc.com/dew-prod/todo-compute:0001");

        DockerHelper.inst("").image.remove("harbor.dew.env/dew-test/test:1.0");
        DockerHelper.inst("").image.remove(defaultDockerRegistryHost + "/dew-test/test:1.0");

        DockerHelper.inst("").image.pull("alpine:3.6", false);
        List<Image> images = DockerHelper.inst("").image.list("alpine:3.6");
        Assert.assertEquals("alpine:3.6", images.get(0).getRepoTags()[0]);
        DockerHelper.inst("").image.build("harbor.dew.env/dew-test/test:1.0", this.getClass().getResource("/").getPath());
        DockerHelper.inst("").image.copy("harbor.dew.env/dew-test/test:1.0", defaultDockerRegistryHost + "/dew-test/test:1.0");
        Assert.assertEquals(1, DockerHelper.inst("").image.list(defaultDockerRegistryHost + "/dew-test/test:1.0").size());
        DockerHelper.inst("").image.push(defaultDockerRegistryHost + "/dew-test/test:1.0", true);
        DockerHelper.inst("").image.remove(defaultDockerRegistryHost + "/dew-test/test:1.0");
        Assert.assertEquals(0, DockerHelper.inst("").image.list(defaultDockerRegistryHost + "/dew-test/test:1.0").size());
        Assert.assertTrue(DockerHelper.inst("").registry.exist(defaultDockerRegistryHost + "/dew-test/test:1.0"));
        DockerHelper.inst("").registry.remove(defaultDockerRegistryHost + "/dew-test/test:1.0");
        Assert.assertFalse(DockerHelper.inst("").registry.exist(defaultDockerRegistryHost + "/dew-test/test:1.0"));
    }

}
