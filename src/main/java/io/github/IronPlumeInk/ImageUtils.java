package io.github.IronPlumeInk;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


 /**
 * 图片工具类，Mat 和 BufferedImage 之间的转换
 */
public class ImageUtils {

    /**
     * BufferedImage 转换为 Mat
     */
    public static Mat bufferedImageToMat(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        Mat mat;

        // 检查 BufferedImage 的类型
        DataBuffer dataBuffer = bi.getRaster().getDataBuffer();
        if (dataBuffer instanceof DataBufferByte) {
            byte[] sourcePixels = ((DataBufferByte) dataBuffer).getData();
            mat = new Mat(height, width, CvType.CV_8UC3);
            mat.put(0, 0, sourcePixels);
        } else if (dataBuffer instanceof DataBufferInt) {
            int[] sourcePixels = ((DataBufferInt) dataBuffer).getData();
            mat = new Mat(height, width, CvType.CV_8UC3);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int pixel = sourcePixels[i * width + j];
                    mat.put(i, j, new byte[]{
                            (byte) ((pixel >> 16) & 0xFF), // Red
                            (byte) ((pixel >> 8) & 0xFF),  // Green
                            (byte) (pixel & 0xFF)          // Blue
                    });
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported data buffer type: " + dataBuffer.getClass());
        }
        return mat;
    }

    /**
     * Mat 转换为 BufferedImage
     */
    public static BufferedImage matToBufferedImage(Mat mat) {
        int cols = mat.cols();
        int rows = mat.rows();
        int channels = mat.channels();

        BufferedImage image;

        if (channels == 1) {
            image = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_GRAY);
            byte[] data = new byte[cols * rows];
            mat.get(0, 0, data);
            image.getRaster().setDataElements(0, 0, cols, rows, data);
        } else if (channels == 3) {
            image = new BufferedImage(cols, rows, BufferedImage.TYPE_3BYTE_BGR);
            byte[] data = new byte[cols * rows * channels];
            mat.get(0, 0, data);
            image.getRaster().setDataElements(0, 0, cols, rows, data);
        } else {
            throw new UnsupportedOperationException("Unsupported Mat channel count: " + channels);
        }

        return image;
    }

}
