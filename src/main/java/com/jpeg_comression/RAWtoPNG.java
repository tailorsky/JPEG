package com.jpeg_comression;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class RAWtoPNG {
    public static void saveRgb24ToPng(byte[] rgbData, int width, int height, String outputPath) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int idx = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = rgbData[idx++] & 0xFF;
                int g = rgbData[idx++] & 0xFF;
                int b = rgbData[idx++] & 0xFF;
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgb);
            }
        }

        boolean result = ImageIO.write(image, "png", new File(outputPath));
        if (!result) {
            throw new RuntimeException("ImageIO не смог сохранить PNG (возможно, неподдерживаемый формат)");
        }
    }
}