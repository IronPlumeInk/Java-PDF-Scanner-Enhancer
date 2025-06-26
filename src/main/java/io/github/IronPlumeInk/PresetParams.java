package io.github.IronPlumeInk;

import org.opencv.core.Rect;

public class PresetParams {

    public static ImageProcessingParams getTextPreset() {
        ImageProcessingParams p = new ImageProcessingParams();
        p.skewType = "auto";
        p.cutRegion = null; // 或 new Rect(x, y, w, h)
        p.spotCompensation = 8;
        p.dpi = 600;

        p.gaussianBlurRadius = 1.2;
        p.gaussianSharpenRadius = 3;
        p.sharpenDegree = 7;
        p.multiScaleDetail = 18;
        p.usmIterations = 1;
        p.usmAmount = 100;
        p.usmRadius = 5.0;
        p.usmThreshold = 20;
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
