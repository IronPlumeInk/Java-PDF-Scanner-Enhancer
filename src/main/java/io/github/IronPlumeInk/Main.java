package io.github.IronPlumeInk;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        String inputPdfPath = "input.pdf";
        String outputDir = "output/images/";

        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        try (PDDocument document = PDDocument.load(new File(inputPdfPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<BufferedImage> processedImages = new ArrayList<>();

            // 预设参数（可改为读取配置）
            ImageProcessingParams textParams = PresetParams.getTextPreset();
            ImageProcessingParams imageParams = PresetParams.getImagePreset();

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 600, ImageType.RGB);

                // === 自动判断页面类型 ===
                boolean isTextOnly = PageClassifier.isTextPage(image);

                // === 选择不同的处理方法 ===
                BufferedImage processed;
                if (isTextOnly) {
                    processed = PdfImageProcessor.processTextPage(image, textParams);
                } else {
                    processed = PdfImageProcessor.processImagePage(image, imageParams);
                }

                processedImages.add(processed);

                // 保存图像
                File outputFile = new File(outputDir + "page_" + (page + 1) + ".jpg");
                ImageIO.write(processed, "jpg", outputFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
