package group.idealworld.dew.devops.kernel.helper;

import com.github.dockerjava.api.model.Image;
import group.idealworld.dew.devops.BasicTest;
import group.idealworld.dew.devops.kernel.util.DewLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    @BeforeEach
    public void before() {
        DockerHelper.init("", DewLog.build(this.getClass()),
                defaultDockerHost, defaultDockerRegistryUrl, defaultDockerRegistryUserName,
                defaultDockerRegistryPassword);
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
        Assertions.assertTrue(DockerHelper.inst("").registry.createProject("unit-test"));

        DockerHelper.inst("").image.pull("alpine:3.6", false);
        List<Image> images = DockerHelper.inst("").image.list("alpine:3.6");
        Assertions.assertEquals("alpine:3.6", images.get(0).getRepoTags()[0]);
        DockerHelper.inst("").image.build("harbor.dew.test/unit-test/test:1.0",
                this.getClass().getResource("/").getPath());
        DockerHelper.inst("").image.copy("harbor.dew.test/unit-test/test:1.0",
                defaultDockerRegistryHost + "/unit-test/test:1.0");
        Assertions.assertEquals(1,
                DockerHelper.inst("").image.list(defaultDockerRegistryHost + "/unit-test/test:1.0").size());
        DockerHelper.inst("").image.push(defaultDockerRegistryHost + "/unit-test/test:1.0", true);
        DockerHelper.inst("").image.remove(defaultDockerRegistryHost + "/unit-test/test:1.0");
        Assertions.assertEquals(0,
                DockerHelper.inst("").image.list(defaultDockerRegistryHost + "/unit-test/test:1.0").size());
        Assertions.assertTrue(
                DockerHelper.inst("").registry.existImage(defaultDockerRegistryHost + "/unit-test/test:1.0"));
        // Test Copy Image
        Assertions.assertTrue(DockerHelper.inst("").registry.copyImage("unit-test/test:1.0", "unit-test", "test_copy"));
        // Test Remove Image
        DockerHelper.inst("").registry.removeImage(defaultDockerRegistryHost + "/unit-test/test:1.0");
        Assertions.assertFalse(
                DockerHelper.inst("").registry.existImage(defaultDockerRegistryHost + "/unit-test/test:1.0"));

        // Test label
        Assertions.assertNull(DockerHelper.inst("").registry.getLabel("commit", Optional.of("unit-test")));
        Assertions.assertTrue(
                DockerHelper.inst("").registry.createOrUpdateLabel("commit", "ssssss", Optional.of("unit-test")));
        Assertions.assertEquals("ssssss",
                DockerHelper.inst("").registry.getLabel("commit", Optional.of("unit-test")).getDescription());
        Assertions.assertTrue(
                DockerHelper.inst("").registry.createOrUpdateLabel("commit", "ccccc", Optional.of("unit-test")));
        Assertions.assertEquals("ccccc",
                DockerHelper.inst("").registry.getLabel("commit", Optional.of("unit-test")).getDescription());

    }

}
