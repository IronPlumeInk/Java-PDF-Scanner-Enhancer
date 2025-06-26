package io.github.IronPlumeInk;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * 页面分类器，判断为文字还是图像页面
 */
public class PageClassifier {
    public static boolean isTextPage(BufferedImage image) {
        // 非正式算法示例：图像暗像素占比大 -> 认为是文字页
        int darkPixelCount = 0;
        int total = image.getWidth() * image.getHeight();

        for (int y = 0; y < image.getHeight(); y += 10) {
            for (int x = 0; x < image.getWidth(); x += 10) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb);
                int brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                if (brightness < 100) darkPixelCount++;
            }
        }

        double ratio = (double) darkPixelCount / ((image.getWidth() / 10) * (image.getHeight() / 10));
        return ratio > 0.35; // 自定义阈值
    }



    /**
     * 判断当前页面是否需要纠斜
     * @param image 页面图像
     * @return 是否需要纠斜
     */
    public static boolean isSkewRequired(BufferedImage image) {
        // 将BufferedImage转为OpenCV Mat
        Mat mat = ImageUtils.bufferedImageToMat(image);
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        // 边缘检测
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 150);

        // 霍夫变换检测直线
        Mat lines = new Mat();
        Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 100);

        int horizontalCount = 0;
        int totalLines = lines.rows();

        for (int i = 0; i < totalLines; i++) {
            double[] line = lines.get(i, 0);
            double rho = line[0];
            double theta = line[1];

            // 近似水平线或垂直线
            if (theta < Math.PI / 180 * 10 || Math.abs(theta - Math.PI / 2) < Math.PI / 180 * 10) {
                continue;
            }

            // 倾斜角度大于阈值（如5度）
            if (Math.abs(theta * 180 / Math.PI - 90) > 5) {
                horizontalCount++;
            }
        }

        // 如果检测到较多非水平/垂直线条，则认为需要纠斜
        return horizontalCount > totalLines * 0.3;
    }

}
