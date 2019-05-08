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

package ms.dew.devops.kernel.config;

import ms.dew.devops.BasicTest;
import ms.dew.devops.kernel.helper.YamlHelper;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Assert;
import org.junit.Test;

/**
 * 配置构建测试.
 *
 * @author gudaoxuri
 */
public class TestConfigBuilder extends BasicTest {

    /**
     * Test all.
     */
    @Test
    public void testAll() {
        YamlHelper.init(new SystemStreamLog());
        String basicConfigStr = "namespace: dew\n"
                + "kind: JVM_SERVICE\n"
                + "ignoreChangeFiles:\n"
                + "- '*.txt'\n"
                + "app:\n"
                + "  replicas: 1\n"
                + "  revisionHistoryLimit: 2\n"
                + "profiles:\n"
                + "  test:\n"
                + "    namespace: dew-test\n"
                + "    skip: true\n"
                + "    ignoreChangeFiles:\n"
                + "    - '*.yaml'\n"
                + "    app:\n"
                + "      replicas: 2\n"
                + "      port: 8080\n"
                + "    docker:\n"
                + "      host: 127.0.0.1";

        basicConfigStr = ConfigBuilder.mergeProfiles(basicConfigStr);
        DewConfig basicConfig = YamlHelper.toObject(DewConfig.class, basicConfigStr);
        Assert.assertEquals("dew-test", basicConfig.getProfiles().get("test").getNamespace());
        Assert.assertTrue(basicConfig.getProfiles().get("test").isSkip());
        Assert.assertEquals(AppKind.JVM_SERVICE, basicConfig.getProfiles().get("test").getKind());
        Assert.assertEquals(2, basicConfig.getProfiles().get("test").getApp().getReplicas());
        Assert.assertEquals(2, basicConfig.getProfiles().get("test").getApp().getRevisionHistoryLimit());
        Assert.assertEquals(8080, basicConfig.getProfiles().get("test").getApp().getPort());
        Assert.assertEquals("*.yaml", basicConfig.getProfiles().get("test").getIgnoreChangeFiles().iterator().next());
        Assert.assertEquals("127.0.0.1", basicConfig.getProfiles().get("test").getDocker().getHost());

        String projectConfigStr =
                "namespace: dew-spec\n"
                        + "app:\n"
                        + "  revisionHistoryLimit: 4\n"
                        + "profiles:\n"
                        + "  test:\n"
                        + "    namespace: dew-test-spec\n"
                        + "    skip: false\n"
                        + "    ignoreChangeFiles:\n"
                        + "    - '*.yaml'\n"
                        + "    app:\n"
                        + "      replicas: 4\n"
                        + "      revisionHistoryLimit: 5\n"
                        + "      port: 8080\n"
                        + "    docker:\n"
                        + "      host: 127.0.0.2\n"
                        + "      registryUserName: sunisle";

        projectConfigStr = ConfigBuilder.mergeProject(basicConfigStr, projectConfigStr);
        DewConfig projectConfig = YamlHelper.toObject(DewConfig.class, projectConfigStr);

        Assert.assertEquals("dew-spec", projectConfig.getNamespace());
        Assert.assertEquals("dew-test-spec", projectConfig.getProfiles().get("test").getNamespace());
        Assert.assertEquals(AppKind.JVM_SERVICE, projectConfig.getKind());
        Assert.assertEquals(AppKind.JVM_SERVICE, projectConfig.getProfiles().get("test").getKind());
        Assert.assertFalse(projectConfig.getProfiles().get("test").isSkip());
        Assert.assertEquals(4, projectConfig.getProfiles().get("test").getApp().getReplicas());
        Assert.assertEquals(5, projectConfig.getProfiles().get("test").getApp().getRevisionHistoryLimit());
        Assert.assertEquals(8080, projectConfig.getProfiles().get("test").getApp().getPort());
        Assert.assertEquals("*.yaml", projectConfig.getProfiles().get("test").getIgnoreChangeFiles().iterator().next());
        Assert.assertEquals("127.0.0.2", projectConfig.getProfiles().get("test").getDocker().getHost());
        Assert.assertEquals("sunisle", projectConfig.getProfiles().get("test").getDocker().getRegistryUserName());

    }
}
