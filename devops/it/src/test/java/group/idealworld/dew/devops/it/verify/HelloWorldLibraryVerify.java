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
 * Hello world library verify.
 *
 * @author gudaoxuri
 */
public class HelloWorldLibraryVerify extends BasicProcessor implements Verify {

    @Override
    public void doVerify(String buildPath, String expectedResPath) throws Exception {
        loadConfig();
        String metaData = $.http.get(itSnapshotRepositoryUrl
                + "group/idealworld/dew/devops/it/helloworld-library/3.0.0-rc.7/maven-metadata.xml");
        Document doc = JOOX.builder().parse(new ByteArrayInputStream(metaData.getBytes()));
        String lastUpdateTime = JOOX.$(doc).find("lastUpdated").get(0).getTextContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        long offsetMinutes = (System.currentTimeMillis() - sdf.parse(lastUpdateTime).getTime()) / 1000 / 60 / 60;
        Assert.assertTrue("last deploy time check", offsetMinutes < 5);
    }

}
