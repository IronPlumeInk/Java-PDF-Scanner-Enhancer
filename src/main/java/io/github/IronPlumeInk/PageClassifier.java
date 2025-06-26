package io.github.IronPlumeInk;

import java.awt.image.BufferedImage;
import java.awt.Color;

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
}
