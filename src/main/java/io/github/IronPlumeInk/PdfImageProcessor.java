package io.github.IronPlumeInk;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Size;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

public class PdfImageProcessor {

    static {
        // 加载 OpenCV 本地库
        System.load("E:\\SereneShellback\\01_Programming\\Environment\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    /**
     * 增强和二值化图像
     */
    public static BufferedImage enhanceAndBinarize(BufferedImage bufferedImage) {
        // 将BufferedImage转为Mat对象
        Mat mat = ImageUtils.bufferedImageToMat(bufferedImage);

        // 转为灰度图像
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        // 高斯模糊
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(1.2, 1.2), 0);

        // 自适应阈值二值化
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(blurred, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY, 11, 2);

        // 转回BufferedImage
        return ImageUtils.matToBufferedImage(binary);
    }


    // 处理纯文字页面
    public static BufferedImage processTextPage(BufferedImage bufferedImage, ImageProcessingParams params) {
        Mat mat = ImageUtils.bufferedImageToMat(bufferedImage);

        // 纠斜：自动检测和修正
        mat = skewCorrection(mat, params.skewType);

        // 切边：手动选择区域，忽略斑点
        mat = edgeCutting(mat, params.cutRegion, params.spotCompensation);

        // DPI调整：模拟
        mat = adjustDPI(mat, params.dpi);

        // 黑白文字增强：高斯模糊、锐化、USM锐化等
        mat = enhanceBlackAndWhite(mat, params);

        return ImageUtils.matToBufferedImage(mat);
    }

    // 处理含图页面
    public static BufferedImage processImagePage(BufferedImage bufferedImage, ImageProcessingParams params) {
        Mat mat = ImageUtils.bufferedImageToMat(bufferedImage);

        // 纠斜：自动修正
        mat = skewCorrection(mat, params.skewType);

        // 切边：忽略斑点和强力去背景
        mat = edgeCutting(mat, params.cutRegion, params.spotCompensation);

        // 曲线去背景：特别用于含图页面
        mat = backgroundRemoval(mat, params.curveAdjustment);

        // 高斯模糊和锐化
        mat = gaussianSharpening(mat, params.gaussianBlurRadius, params.gaussianSharpenRadius, params.sharpenDegree);

        // 多尺度细节增强
        mat = multiScaleDetailEnhancement(mat, params.multiScaleDetail);

        // USM锐化：增强细节
        mat = usmSharpening(mat, params.usmIterations, params.usmAmount, params.usmRadius, params.usmThreshold);

        return ImageUtils.matToBufferedImage(mat);
    }

    // 图像处理方法：增强黑白文字图像
    private static Mat enhanceBlackAndWhite(Mat mat, ImageProcessingParams params) {
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        // 高斯模糊
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(params.gaussianBlurRadius, params.gaussianBlurRadius), 0);

        // 锐化（USM锐化）
        Mat sharpened = new Mat();
        Imgproc.GaussianBlur(blurred, sharpened, new Size(params.gaussianSharpenRadius, params.gaussianSharpenRadius), 0);
        Core.addWeighted(blurred, 1.5, sharpened, -0.5, 0, sharpened);

        // 自适应阈值二值化
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(sharpened, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        return binary;
    }

    // 纠斜修正：自动或手动
    private static Mat skewCorrection(Mat mat, String skewType) {
        if (skewType.equals("auto")) {
            // 自动纠斜
        }
        return mat;
    }

    // 切边：处理指定区域
    private static Mat edgeCutting(Mat mat, Rect cutRegion, double spotCompensation) {
        if (cutRegion != null) {
            mat = mat.submat(cutRegion);
        }
        return mat;
    }

    // DPI调整：模拟
    private static Mat adjustDPI(Mat mat, int dpi) {
        // 调整DPI逻辑（模拟处理）
        return mat;
    }

    // 曲线去背景：含图页面处理
    private static Mat backgroundRemoval(Mat mat, CurveAdjustmentParams curveParams) {
        // 曲线调整去背景
        return mat;
    }

    // 高斯模糊和锐化
    private static Mat gaussianSharpening(Mat mat, double blurRadius, double sharpenRadius, int sharpenDegree) {
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(mat, blurred, new Size(blurRadius, blurRadius), 0);
        Mat sharpened = new Mat();
        Imgproc.GaussianBlur(blurred, sharpened, new Size(sharpenRadius, sharpenRadius), sharpenDegree);
        Core.addWeighted(blurred, 1.5, sharpened, -0.5, 0, sharpened);
        return sharpened;
    }

    // 多尺度细节增强
    private static Mat multiScaleDetailEnhancement(Mat mat, int enhancementLevel) {
        // 进行多尺度增强
        return mat;
    }

    // USM锐化
    private static Mat usmSharpening(Mat mat, int iterations, double amount, double radius, double threshold) {
        // USM锐化算法
        return mat;
    }
}
