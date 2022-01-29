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
                new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getPath() + File.separator
                        + "src" + File.separator
                        + "test");
        DevOps.Mock.invokeMock();
        // TODO
        // Assertions.assertEquals("mock", GitHelper.inst().getCurrentBranch());
    }
}
