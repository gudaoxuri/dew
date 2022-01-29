/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew.devops.kernel.config;

import group.idealworld.dew.devops.BasicTest;
import group.idealworld.dew.devops.kernel.helper.YamlHelper;
import group.idealworld.dew.devops.kernel.util.DewLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        YamlHelper.init(DewLog.build(this.getClass()));
        String basicConfigStr = "namespace: dew\n"
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
        Assertions.assertEquals("dew-test", basicConfig.getProfiles().get("test").getNamespace());
        Assertions.assertTrue(basicConfig.getProfiles().get("test").getSkip());
        Assertions.assertEquals(2, basicConfig.getProfiles().get("test").getApp().getReplicas().intValue());
        Assertions.assertEquals(2, basicConfig.getProfiles().get("test").getApp().getRevisionHistoryLimit().intValue());
        Assertions.assertEquals(8080, basicConfig.getProfiles().get("test").getApp().getPort().intValue());
        Assertions.assertEquals("*.yaml", basicConfig.getProfiles().get("test").getIgnoreChangeFiles().iterator().next());
        Assertions.assertEquals("127.0.0.1", basicConfig.getProfiles().get("test").getDocker().getHost());

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

        Assertions.assertEquals("dew-spec", projectConfig.getNamespace());
        Assertions.assertEquals("dew-test-spec", projectConfig.getProfiles().get("test").getNamespace());
        Assertions.assertFalse(projectConfig.getProfiles().get("test").getSkip());
        Assertions.assertEquals(4, projectConfig.getProfiles().get("test").getApp().getReplicas().intValue());
        Assertions.assertEquals(5, projectConfig.getProfiles().get("test").getApp().getRevisionHistoryLimit().intValue());
        Assertions.assertEquals(8080, projectConfig.getProfiles().get("test").getApp().getPort().intValue());
        Assertions.assertEquals("*.yaml", projectConfig.getProfiles().get("test").getIgnoreChangeFiles().iterator().next());
        Assertions.assertEquals("127.0.0.2", projectConfig.getProfiles().get("test").getDocker().getHost());
        Assertions.assertEquals("sunisle", projectConfig.getProfiles().get("test").getDocker().getRegistryUserName());

    }
}
