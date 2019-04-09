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

import java.io.File;

/**
 * @author gudaoxuri
 */
public interface Verify {

    default void verify(File basedir) throws Exception {
        String expectedResPath = new File(basedir, File.separator + "expected").getPath() + File.separator;
        String buildPath = new File(basedir, File.separator + "target").getPath() + File.separator;
        doVerify(buildPath, expectedResPath);
    }

    void doVerify(String buildPath, String expectedResPath) throws Exception;

}
