package group.idealworld.dew.ossutils.utils;


import group.idealworld.dew.ossutils.bean.ImageProcessParam;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author yiye
 **/

public class OssHandleTool {

    private OssHandleTool() {
    }

    /**
     * 缩略图处理方法，oss,obs共用
     *
     * @param process 图片处理参数
     * @return 处理后的图片url
     */
    public static String imageProcess(ImageProcessParam process) {
        StringBuilder style = new StringBuilder();
        style.append("image/");
        if (process.getResizeRatio() == null) {
            style.append("resize,m_");
            style.append(StringUtils.hasLength(process.getResizeType()) ? process.getResizeType() : "fixed");
            style.append(",w_");
            style.append(process.getWidth());
            style.append(",h_");
            style.append(process.getHeight());
            if (process.getRotate() != null) {
                style.append("/rotate,");
                style.append(process.getRotate());
            }
        } else {
            style.append("resize,m_");
            style.append(StringUtils.hasLength(process.getResizeType()) ? process.getResizeType() : "fixed");
            style.append(",p_");
            style.append(process.getResizeRatio());
        }

        if (process.getOtherParam() != null) {
            style = new StringBuilder();
            style.append("image/");
            for (Map.Entry<String, String> entry : process.getOtherParam().entrySet()) {
                style.append(entry.getKey());
                style.append(",");
                style.append(entry.getValue());
                style.append("/");
            }
        }
        return style.toString();
    }

}
