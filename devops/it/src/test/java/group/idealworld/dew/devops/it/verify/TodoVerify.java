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

package group.idealworld.dew.devops.it.verify;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.it.BasicProcessor;
import org.joox.JOOX;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * To-do verify.
 *
 * @author gudaoxuri
 */
public class TodoVerify extends BasicProcessor implements Verify {

    @Override
    public void doVerify(String buildPath, String expectedResPath) throws Exception {
        loadConfig();

        // verify parent
        String metaData = $.http.get(itSnapshotRepositoryUrl + "group/idealworld/dew/devops/it/todo-parent/3.0.0-Beta3/maven-metadata.xml");
        Document doc = JOOX.builder().parse(new ByteArrayInputStream(metaData.getBytes()));
        String lastUpdateTime = JOOX.$(doc).find("lastUpdated").get(0).getTextContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        long offsetMinutes = (System.currentTimeMillis() - sdf.parse(lastUpdateTime).getTime()) / 1000 / 60 / 60;
        Assert.assertTrue("parent | last deploy time check", offsetMinutes < 5);

        // verify common
        metaData = $.http.get(itSnapshotRepositoryUrl + "group/idealworld/dew/devops/it/todo-common/3.0.0-Beta3/maven-metadata.xml");
        doc = JOOX.builder().parse(new ByteArrayInputStream(metaData.getBytes()));
        lastUpdateTime = JOOX.$(doc).find("lastUpdated").get(0).getTextContent();
        sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        offsetMinutes = (System.currentTimeMillis() - sdf.parse(lastUpdateTime).getTime()) / 1000 / 60 / 60;
        Assert.assertTrue("common | last deploy time check", offsetMinutes < 5);

    }
}
