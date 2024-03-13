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
        String metaData = $.http.get(
                itSnapshotRepositoryUrl + "group/idealworld/dew/devops/it/todo-parent/3.0.0-rc.7/maven-metadata.xml");
        Document doc = JOOX.builder().parse(new ByteArrayInputStream(metaData.getBytes()));
        String lastUpdateTime = JOOX.$(doc).find("lastUpdated").get(0).getTextContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        long offsetMinutes = (System.currentTimeMillis() - sdf.parse(lastUpdateTime).getTime()) / 1000 / 60 / 60;
        Assert.assertTrue("parent | last deploy time check", offsetMinutes < 5);

        // verify common
        metaData = $.http.get(
                itSnapshotRepositoryUrl + "group/idealworld/dew/devops/it/todo-common/3.0.0-rc.7/maven-metadata.xml");
        doc = JOOX.builder().parse(new ByteArrayInputStream(metaData.getBytes()));
        lastUpdateTime = JOOX.$(doc).find("lastUpdated").get(0).getTextContent();
        sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        offsetMinutes = (System.currentTimeMillis() - sdf.parse(lastUpdateTime).getTime()) / 1000 / 60 / 60;
        Assert.assertTrue("common | last deploy time check", offsetMinutes < 5);

    }
}
