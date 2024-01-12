package group.idealworld.dew.sdkgen.lang.java;

import com.github.jknack.handlebars.Handlebars;
import group.idealworld.dew.sdkgen.helper.HandlebarsHelper;
import group.idealworld.dew.sdkgen.helper.NameHelper;
import io.swagger.codegen.v3.SupportingFile;
import io.swagger.codegen.v3.generators.java.JavaClientCodegen;

import java.io.File;

/**
 * The type Dew java client codegen.
 *
 * @author gudaoxuri
 */
public class DewJavaClientCodegen extends JavaClientCodegen {

    @Override
    public void processOpts() {
        super.processOpts();
        String basePackage = invokerPackage.substring(0, invokerPackage.lastIndexOf("."));
        final String baseFolder = (sourceFolder + File.separator + basePackage).replace(".", File.separator);
        String sdkClassName = NameHelper.formatClassName(this.artifactId);
        supportingFiles.add(new SupportingFile("sdk.mustache", baseFolder, sdkClassName + ".java"));
    }

    @Override
    public void addHandlebarHelpers(Handlebars handlebars) {
        super.addHandlebarHelpers(handlebars);
        HandlebarsHelper.registerFormatPackage(handlebars);
        HandlebarsHelper.registerFormatClassName(handlebars);
    }

}
