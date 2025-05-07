package com.jpeg_comression;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class PNGToYUVgrayscale {
    public static void convertToYUVGrayscale(String pngPath, String outputYuvPath) throws IOException {
        BufferedImage image = ImageIO.read(new File(pngPath));
        int width = image.getWidth();
        int height = image.getHeight();

        byte[] yPlane = new byte[width * height];
        byte[] uPlane = new byte[width * height];
        byte[] vPlane = new byte[width * height];

        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Используем усреднённое значение как яркость (Y), т.к. изображение ч/б
                int gray = (r + g + b) / 3;

                yPlane[index] = (byte) gray;
                uPlane[index] = (byte) 128; // Нейтральный U
                vPlane[index] = (byte) 128; // Нейтральный V
                index++;
            }
        }

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(outputYuvPath))) {
            out.writeInt(width);
            out.writeInt(height);
            out.write(yPlane);
            out.write(uPlane);
            out.write(vPlane);
        }

        System.out.println("YUV-файл сохранён: " + outputYuvPath);
    }

    public static void main(String[] args) {
        try {
            convertToYUVGrayscale("LennaBWD.png", "LennaBWD_gray.yuv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
