package groop.idealworld.dew.ossutils.bean;

import java.util.Map;

/**
 * @author yiye
 */
public class ImageProcessParam {
    /**
     * 宽度,目标缩略图的宽度。取值为[1，4096]
     */
    private Integer width;

    /**
     * 高度,目标缩略图的高度。取值为[1，4096]
     */
    private Integer height;

    /**
     * 设置缩略的类型。取值为lfit、mfit、fill、pad和fixed，默认值为lfit。
     * <p>
     * lfit：指定一个w和h的矩形，将图片进行等比缩放，取在矩形内最大的图片。
     * mfit：指定一个w和h的矩形，将图片进行等比缩放，取在矩形延伸区域的最小图片。
     * fill：指定一个w和h的矩形，将图片进行等比缩放，取在延伸区域的最小图片，并进行居中剪切。即将mfit缩略类型的图片进行居中剪裁。
     * pad：指定一个w和h的矩形，将图片进行等比缩放，取在矩形内最大的图片，并在矩形空白处进行颜色填充。即lfit缩略类型的图片在矩形空白处进行颜色填充。
     * fixed：强制按照固定的宽高进行缩略。
     */
    private String resizeType;

    /**
     * 等比例缩放的倍数百分比。使用该参数时，无法使用其它参数。取值范围为[1，1000]。当取值为：
     * <p>
     * ＜100：缩小。
     * =100：保持原图大小。
     * ＞100：放大
     */
    private Integer resizeRatio;

    /**
     * 按照顺时针旋转的角度，取值范围为[0，360]。
     * <p>
     * 默认值为0，0表示不旋转。数值越大，图片按顺时针方向旋转的角度越大。
     */
    private Integer rotate;

    /**
     * 如需其他处理，需自行查询官方文档生成参数，例如
     * 添加水印，image/watermark,image_aW1hZ2UtZGVtby9sb2dvLnBuZw==,g_br,t_90,x_10,y_10
     * otherParam.put("watermark","image_aW1hZ2UtZGVtby9sb2dvLnBuZw==,g_br,t_90,x_10,y_10")
     */
    private Map<String, String> otherParam;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getResizeType() {
        return resizeType;
    }

    public void setResizeType(String resizeType) {
        this.resizeType = resizeType;
    }

    public Integer getResizeRatio() {
        return resizeRatio;
    }

    public void setResizeRatio(Integer resizeRatio) {
        this.resizeRatio = resizeRatio;
    }

    public Integer getRotate() {
        return rotate;
    }

    public void setRotate(Integer rotate) {
        this.rotate = rotate;
    }

    public Map<String, String> getOtherParam() {
        return otherParam;
    }

    public void setOtherParam(Map<String, String> otherParam) {
        this.otherParam = otherParam;
    }
}
