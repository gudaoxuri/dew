package group.idealworld.dew.sdkgen.helper;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;

/**
 * The type Handlebars helper.
 *
 * @author gudaoxuri
 */
public class HandlebarsHelper {

    private HandlebarsHelper() {
    }

    public static void registerFormatClassName(Handlebars handlebars) {
        handlebars.registerHelper("formatClassName",
                (Helper<String>) (s, options) -> NameHelper.formatClassName(s));
    }

    public static void registerFormatPackage(Handlebars handlebars) {
        handlebars.registerHelper("formatPackage",
                (Helper<String>) (s, options) -> NameHelper.formatPackage(s));
    }

}
