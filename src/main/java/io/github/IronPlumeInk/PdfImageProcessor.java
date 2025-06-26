package io.github.IronPlumeInk;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Size;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


/**
 * PDF图像处理方法
 */
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
        //mat = usmSharpening(mat, params.usmIterations, params.usmAmount, params.usmRadius, params.usmThreshold);
        mat = usmSharpening(mat, params.usmAmount, params.usmRadius, params.usmThreshold);

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
//        Mat sharpened = new Mat();
//        Imgproc.GaussianBlur(blurred, sharpened, new Size(params.gaussianSharpenRadius, params.gaussianSharpenRadius), 0);
//        Core.addWeighted(blurred, 1.5, sharpened, -0.5, 0, sharpened);

        Mat sharpened = usmSharpening(blurred, params.usmAmount, params.usmRadius, params.usmThreshold);
        // mat = usmSharpening(blurred, params.usmAmount, params.usmRadius, params.usmThreshold);

        // 自适应阈值二值化
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(sharpened, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        return binary;
    }

    // 纠斜修正：自动或手动
//    private static Mat skewCorrection(Mat mat, String skewType) {
//        if (skewType.equals("auto")) {
//            // 自动纠斜
//        }
//        return mat;
//    }

    /**
     * 自动纠斜处理
     * @param mat 输入图像矩阵
     * @param skewType 纠斜类型（auto/manual）
     * @return 纠斜后的图像矩阵
     */
    private static Mat skewCorrection(Mat mat, String skewType) {
        if ("auto".equals(skewType)) {
            Mat gray = new Mat();
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

            // Canny边缘检测
            Mat edges = new Mat();
            Imgproc.Canny(gray, edges, 50, 150);

            // Hough Line Transform
            Mat lines = new Mat();
            Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 100);

            List<Double> angles = new ArrayList<>();

            for (int i = 0; i < lines.rows(); i++) {
                double[] line = lines.get(i, 0);
                double rho = line[0];
                double theta = line[1];
                double angleDeg = theta * 180 / Math.PI;

                // 忽略接近水平和垂直的线
                if (Math.abs(angleDeg - 90) > 5 && Math.abs(angleDeg) > 5) {
                    angles.add(angleDeg > 90 ? angleDeg - 180 : angleDeg);
                }
            }

            if (!angles.isEmpty()) {
                // 计算平均倾斜角度
                double avgAngle = angles.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                // 确定目标角度（修正到最接近的 0°、90°、180°、270°）
                double targetAngle;
                if (Math.abs(avgAngle) < 45) {
                    targetAngle = 0; // 横向排版
                } else if (Math.abs(avgAngle - 90) < 45) {
                    targetAngle = 90; // 竖向排版
                } else if (Math.abs(avgAngle + 90) < 45) {
                    targetAngle = -90; // 反向竖向排版
                } else {
                    targetAngle = 180; // 其他情况
                }

//                if (!angles.isEmpty()) {
//                    System.out.println("Detected average angle: " + avgAngle);
//                    // 后续旋转逻辑
//                }


                // 使用仿射变换进行旋转校正
                Point center = new Point(mat.cols() / 2, mat.rows() / 2);
                Mat rotationMatrix = Imgproc.getRotationMatrix2D(center, targetAngle, 1.0);
                Mat rotatedMat = new Mat();
                Imgproc.warpAffine(mat, rotatedMat, rotationMatrix, mat.size(), Imgproc.INTER_LINEAR,
                        Core.BORDER_CONSTANT, Scalar.all(255)); // 白色背景填充


                return rotatedMat; // 返回校正后的图像


            }
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
//    private static Mat usmSharpening(Mat mat, int iterations, double amount, double radius, double threshold) {
//        // USM锐化算法
//        return mat;
//    }

    /**
     * USM 锐化（Unsharp Mask）
     * @param mat 输入图像 Mat (8UC1 或 8UC3)
     * @param amount 锐化强度（建议范围：0.1 - 5.0）
     * @param radius 模糊半径（建议范围：0.5 - 2.0）
     * @param threshold 边缘检测阈值（建议范围：0 - 10）
     * @return 锐化后的图像 Mat
     */
    private static Mat usmSharpening(Mat mat, double amount, double radius, int threshold) {
        if (mat.channels() != 1 && mat.channels() != 3) {
            throw new IllegalArgumentException("Input Mat must be 8UC1 or 8UC3");
        }

        // 创建模糊副本
        Mat blurred = new Mat();
        int kernelSize = (int) Math.ceil(radius * 2) * 2 + 1; // 确保奇数尺寸
        Size ksize = new Size(kernelSize, kernelSize);
        Imgproc.GaussianBlur(mat, blurred, ksize, radius);

        // 创建掩模
        Mat mask = new Mat();
        Core.subtract(mat, blurred, mask);

        // 应用阈值限制锐化区域
        if (threshold > 0) {
            Mat maskGT = new Mat();
            Mat maskLT = new Mat();

            //使用Core.CMP_GT和Core.CMP_LT
            Core.compare(mask, Scalar.all(threshold), maskGT, Core.CMP_GT);
            Core.compare(mask, Scalar.all(-threshold), maskLT, Core.CMP_LT);

            Mat combinedMask = new Mat();
            Core.bitwise_or(maskGT, maskLT, combinedMask);

            combinedMask.convertTo(combinedMask, CvType.CV_8U, amount,  0);
            Core.multiply(mask, combinedMask, mask);
        } else {
            mask.convertTo(mask, CvType.CV_8U, amount, 0);
        }

        // 叠加回原始图像
        Mat sharpened = new Mat();
        Core.add(mat, mask, sharpened);

        return sharpened;
    }

}
