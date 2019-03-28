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

package ms.dew.devops.helper;

import com.github.dockerjava.api.model.Image;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DockerHelperTest {

    @Before
    public void before() {
        DockerHelper.init("", new SystemStreamLog(),
                "tcp://10.200.131.182:2375",
                "https://harbor.dew.env/v2", "admin", "Harbor12345");
    }

    @Test
    public void testAll() {
        DockerHelper.Image.pull("alpine:3.6", false, "");
        List<Image> images = DockerHelper.Image.list("alpine:3.6", "");
        Assert.assertEquals("alpine:3.6", images.get(0).getRepoTags()[0]);
        String imageId = DockerHelper.Image.build("harbor.dew.env/dew-test/test:1.0", this.getClass().getResource("/").getPath(), "");
        Assert.assertEquals(1, DockerHelper.Image.list("harbor.dew.env/dew-test/test:1.0", "").size());
        DockerHelper.Image.push("harbor.dew.env/dew-test/test:1.0", true, "");
        DockerHelper.Image.remove(imageId, "");
        Assert.assertEquals(0, DockerHelper.Image.list("harbor.dew.env/dew-test/test:1.0", "").size());
    }

}