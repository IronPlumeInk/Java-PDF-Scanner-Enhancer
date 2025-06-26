package io.github.IronPlumeInk;

import org.opencv.core.Rect;

    /**
    * 描述图像处理参数的类
    */

public class ImageProcessingParams {
    // 版面参数
    public String skewType; // "auto" or "manual"
    public Rect cutRegion;
    public double spotCompensation;
    public int dpi;

    // 图像处理参数
    public double gaussianBlurRadius;
    public double gaussianSharpenRadius;
    public int sharpenDegree;
    public int multiScaleDetail;
    public int usmIterations;
    public double usmAmount;
    public double usmRadius;
    public int usmThreshold;

    // 曲线调整参数（仅含图页面）
    public CurveAdjustmentParams curveAdjustment;
}
