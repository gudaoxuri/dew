package group.idealworld.dew.core.dbutils;

import group.idealworld.dew.core.dbutils.dto.DBUtilsConfig;
import group.idealworld.dew.core.dbutils.dto.DSConfig;
import group.idealworld.dew.core.dbutils.process.DSLoader;

/**
 * 操作入口类.
 *
 * @author gudaoxuri
 */
public class DewDBUtils {

    /**
     * 初始化数据源.
     *
     * @param dbUtilsConfig 加载配置信息
     */
    public static void init(DBUtilsConfig dbUtilsConfig) {
        DSLoader.load(dbUtilsConfig);
    }

    /**
     * 添加数据源.
     *
     * @param dsConfig 配置文件
     */
    public static void addDS(DSConfig dsConfig) {
        DSLoader.addDS(dsConfig);
    }

    /**
     * 删除数据源.
     *
     * @param dsCode 数据源编码
     */
    public static void removeDS(String dsCode) {
        DSLoader.removeDS(dsCode);
    }

    /**
     * 删除数据源.
     *
     * @param dewDB DB实例
     */
    public static void removeDS(DewDB dewDB) {
        if (dewDB != null
                && dewDB.getDsInfo() != null
                && dewDB.getDsInfo().getDsConfig() != null) {
            DSLoader.removeDS(dewDB.getDsInfo().getDsConfig().getCode());
        }
    }

    /**
     * 选择DB实例.
     *
     * @param dsCode 数据源编码
     * @return DB实例
     */
    public static DewDB use(String dsCode) {
        return DewDB.pick(dsCode);
    }
}