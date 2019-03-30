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

package ms.dew.devops.misc;

import ms.dew.devops.BasicTest;
import ms.dew.devops.helper.GitHelper;
import ms.dew.devops.kernel.Dew;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

/**
 * @author gudaoxuri
 */
public class MockTest extends BasicTest {

    @Test
    public void testAll() throws ClassNotFoundException, NoSuchMethodException, MalformedURLException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Dew.log = new SystemStreamLog();
        Dew.Mock.loadClass(new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getPath() + File.separator + "src" + File.separator + "test");
        Dew.Mock.invokeMock();
        Assert.assertEquals("mock", GitHelper.inst().getCurrentBranch());
    }
}
