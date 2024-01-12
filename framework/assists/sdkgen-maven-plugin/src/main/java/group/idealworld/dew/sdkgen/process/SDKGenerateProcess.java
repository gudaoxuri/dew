package group.idealworld.dew.sdkgen.process;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.tuple.Tuple3;
import group.idealworld.dew.sdkgen.helper.NameHelper;
import group.idealworld.dew.sdkgen.maven.SDKGenerateMojo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashMap;

import static group.idealworld.dew.sdkgen.Constants.GENERATED_BASE_PATH;
import static group.idealworld.dew.sdkgen.Constants.GENERATED_OPENAPI_FILE_NAME;

/**
 * The type Generate process.
 *
 * @author gudaoxuri
 */
@Slf4j
public class SDKGenerateProcess {
    private SDKGenerateProcess() {
    }

    /**
     * Process.
     *
     * @param mojo         the mojo
     * @param mavenProject the maven project
     * @param language     the language
     * @return the tuple: groupId, artifactId, version
     */
    public static Tuple3<String, String, String> process(SDKGenerateMojo mojo,
            MavenProject mavenProject, String language) {
        log.info("Generate SDK by {}", language);

        String groupId = mavenProject.getGroupId();
        String artifactId = mavenProject.getArtifactId() + ".sdk";
        setAndGetIfNotExist(mojo, "groupId", groupId);
        setAndGetIfNotExist(mojo, "artifactId", artifactId);
        setAndGetIfNotExist(mojo, "artifactVersion", mavenProject.getVersion());

        String basePackage = NameHelper.formatPackage(groupId + "." + mavenProject.getArtifactId() + ".sdk");
        setAndGetIfNotExist(mojo, "apiPackage", basePackage + ".api");
        setAndGetIfNotExist(mojo, "modelPackage", basePackage + ".model");
        setAndGetIfNotExist(mojo, "invokerPackage", basePackage + ".invoker");

        String basePath = mavenProject.getBasedir().getPath() + File.separator +
                "target" + File.separator +
                GENERATED_BASE_PATH;
        setValueToParentField(mojo, "output", new File(basePath + File.separator + "sdk"));
        setValueToParentField(mojo, "inputSpec",
                basePath + File.separator + GENERATED_OPENAPI_FILE_NAME);
        if ("java".equals(language)) {
            setValueToParentField(mojo, "language", "group.idealworld.dew.sdkgen.lang.java.DewJavaClientCodegen");
        }
        setAndGetIfNotExist(mojo, "configOptions", new HashMap<String, Object>() {
            {
                put("sourceFolder", "src/main/" + language);
            }
        });
        return new Tuple3<>(groupId, artifactId, mavenProject.getVersion());
    }

    private static <T> T setAndGetIfNotExist(SDKGenerateMojo mojo, String field, T defaultValue) {
        T value = (T) $.bean.getValue(mojo, field);
        if (value != null) {
            return value;
        }
        $.bean.setValue(mojo, field, defaultValue);
        return defaultValue;
    }

    @SneakyThrows
    private static void setValueToParentField(SDKGenerateMojo mojo, String field, Object value) {
        $.bean.setValue(mojo, mojo.getClass().getSuperclass().getDeclaredField(field), value);
    }

}
