package group.idealworld.dew.devops.kernel.helper;

import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Yaml helper.
 *
 * @author gudaoxuri
 */
public class YamlHelper {

    private static Logger log;
    private static Yaml yaml;

    /**
     * Init.
     *
     * @param log the log
     */
    public static void init(Logger log) {
        if (yaml == null) {
            YamlHelper.log = log;
            DumperOptions options = new DumperOptions();
            options.setCanonical(false);
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            options.setIndent(2);
            Representer representer = new io.kubernetes.client.util.Yaml.CustomRepresenter();
            representer.getPropertyUtils().setSkipMissingProperties(true);
            yaml = new Yaml(representer, options);
        }
    }

    /**
     * To object.
     *
     * @param <T>     the type parameter
     * @param content the content
     * @return the object
     */
    public static <T> T toObject(String content) {
        return yaml.load(content);
    }

    /**
     * To object.
     *
     * @param <T>     the type parameter
     * @param clazz   the clazz
     * @param content the content
     * @return the object
     */
    public static <T> T toObject(Class<T> clazz, String content) {
        return yaml.loadAs(content, clazz);
    }

    /**
     * To object.
     *
     * @param <T>      the type parameter
     * @param clazz    the clazz
     * @param contents the contents
     * @return the object
     */
    public static <T> T toObject(Class<T> clazz, String... contents) {
        String mergedContent = String.join("\r\n", contents);
        return yaml.loadAs(mergedContent, clazz);
    }

    /**
     * To string.
     *
     * @param content the content
     * @return yaml string
     */
    public static String toString(Object content) {
        String str = yaml.dump(content);
        if (str.startsWith("!!")) {
            return str.substring(str.indexOf('\n') + 1);
        }
        return str;
    }

}
