package io.github.IronPlumeInk;

import org.opencv.core.Rect;

    /**
    * 预设参数
    */

public class PresetParams {

    public static ImageProcessingParams getTextPreset() {
        ImageProcessingParams p = new ImageProcessingParams();
        p.skewType = "auto";
        p.cutRegion = null; // 或 new Rect(x, y, w, h)
        p.spotCompensation = 8;
        p.dpi = 600;

        p.gaussianBlurRadius = 1.2; // 模糊
        p.gaussianSharpenRadius = 3;// 锐化
        p.sharpenDegree = 7;        // 锐化程度
        p.multiScaleDetail = 18;    // 多尺度细节增强
        p.usmIterations = 1;        // 锐化次数
        p.usmAmount = 1.5;          //锐化强度
        p.usmRadius = 1.2;          //锐化范围（高斯模糊半径，影响边缘检测范围）
        p.usmThreshold = 2;         //锐化阈值（用于控制只对强边缘进行锐化，避免噪声放大）
        return p;
    }

    public static ImageProcessingParams getImagePreset() {
        ImageProcessingParams p = getTextPreset();
        p.multiScaleDetail = 20;
        p.curveAdjustment = new CurveAdjustmentParams();
        p.curveAdjustment.x1 = 150;
        p.curveAdjustment.y1 = 0;
        p.curveAdjustment.x2 = 110;
        p.curveAdjustment.y2 = 255;
        return p;
    }
}
