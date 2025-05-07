package com.jpeg_comression;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class JpegToRawConverter {
    public static void convertJpegToRaw(String inputPath, String outputPath) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputPath));
        if (image == null) {
            throw new IOException("Не удалось прочитать изображение");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        byte[] rawData = new byte[width * height * 3];

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                rawData[index++] = (byte) ((rgb >> 16) & 0xFF);
                rawData[index++] = (byte) ((rgb >> 8) & 0xFF);
                rawData[index++] = (byte) (rgb & 0xFF);
            }
        }

        try (DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputPath))) {
            fos.writeInt(width);
            fos.writeInt(height);
            fos.write(rawData);
        }
    }

    public static BufferedImage openRawImage(String inputPath, int width, int height) throws IOException {
        File file = new File(inputPath);
        byte[] rawData = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(rawData);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = rawData[index++] & 0xFF;
                int g = rawData[index++] & 0xFF;
                int b = rawData[index++] & 0xFF;
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }

    public static void displayRawImage(String inputPath, int width, int height) throws IOException {
        BufferedImage image = openRawImage(inputPath, width, height);
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);
        JFrame frame = new JFrame("RAW Image Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException{
        String inputPath = "o.jpg";
        String outputPath = "o.raw";
        convertJpegToRaw(inputPath, outputPath);
        //displayRawImage("decoded.raw", 735, 878);
    }
}
