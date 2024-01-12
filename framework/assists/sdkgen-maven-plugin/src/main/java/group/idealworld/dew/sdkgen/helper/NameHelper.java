package group.idealworld.dew.sdkgen.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Name helper.
 *
 * @author gudaoxuri
 */
public class NameHelper {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("[^a-zA-Z0-9]+(\\w{1})");

    private NameHelper() {
    }

    /**
     * Format package.
     *
     * @param pkg the pkg
     * @return the result
     */
    public static String formatPackage(String pkg) {
        return pkg.toLowerCase().replaceAll("[^a-zA-Z0-9.]+", "");
    }

    /**
     * Format class name.
     *
     * @param name the name
     * @return the result
     */
    public static String formatClassName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length() - 4) + "SDK";
        Matcher matcher = CLASS_NAME_PATTERN.matcher(name);
        return matcher.replaceAll(s -> s.group(1).toUpperCase());
    }
}
