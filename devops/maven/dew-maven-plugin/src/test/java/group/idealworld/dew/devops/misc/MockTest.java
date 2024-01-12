package group.idealworld.dew.devops.misc;

import group.idealworld.dew.devops.BasicTest;
import group.idealworld.dew.devops.kernel.DevOps;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

/**
 * Mock test.
 *
 * @author gudaoxuri
 */
public class MockTest extends BasicTest {

    /**
     * Test all.
     *
     * @throws ClassNotFoundException    the class not found exception
     * @throws NoSuchMethodException     the no such method exception
     * @throws MalformedURLException     the malformed url exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException    the instantiation exception
     */
    @Test
    public void testAll()
            throws ClassNotFoundException, NoSuchMethodException, MalformedURLException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        DevOps.Mock.loadClass(
                new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getPath()
                        + File.separator
                        + "src" + File.separator
                        + "test");
        DevOps.Mock.invokeMock();
        // TODO
        // Assertions.assertEquals("mock", GitHelper.inst().getCurrentBranch());
    }
}
