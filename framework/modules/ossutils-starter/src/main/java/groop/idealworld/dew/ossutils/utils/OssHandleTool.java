package groop.idealworld.dew.ossutils.utils;


import groop.idealworld.dew.ossutils.bean.ImageProcessParam;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author yiye
 * @date 2022/4/1
 * @description
 **/

public class OssHandleTool {


    /**
         * 缩略图处理方法，oss,obs共用
         * @param process
         * @return
         */
    public static String imageProcess(ImageProcessParam process) {
        StringBuffer style = new StringBuffer();
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
            style = new StringBuffer();
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
