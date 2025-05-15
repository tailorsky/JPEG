package com.jpeg_comression;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PngToYUV {

    public static void convertToYUVPlanar(String pngPath, String outputYuvPath) throws IOException {
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

                int Y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                int U = (int) (-0.169 * r - 0.331 * g + 0.5 * b + 128);
                int V = (int) (0.5 * r - 0.419 * g - 0.081 * b + 128);

                yPlane[index] = (byte) clamp(Y);
                uPlane[index] = (byte) clamp(U);
                vPlane[index] = (byte) clamp(V);
                index++;
            }
        }

        try (DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputYuvPath))) {
            fos.writeInt(width);
            fos.writeInt(height);
            
            fos.write(yPlane); // Y-плоскость
            fos.write(uPlane); // U-плоскость
            fos.write(vPlane); // V-плоскость
        }

        System.out.println("YUV-файл сохранён: " + outputYuvPath);
    }

    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }

    public static void main(String[] args) {
        try {
            convertToYUVPlanar("Big_bw_dithered.png", "Big_bw_dithered.yuv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
