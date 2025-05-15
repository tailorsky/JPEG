package com.jpeg_comression;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Test {
    static final int BLOCK_SIZE = 8;

    public static double[][] CUSTOM_DCT_MATRIX = {
        {0,   0,   0,   0,   0, 0, 0, 1000000},
        {0,   0,   0,   0,   0,   0, 0, 0},
        {0,   0,   0,   0,   0,   0,   0, 0},
        {0,   0,   0,   0,   0,   0,   0,   0},
        {0,   0,   0,   0,   0,   0,   0,   0},
        {0,   0,   0,   0,   0,   0,   0,   0},
        {0,   0,   0,   0,   0,   0,   0,   0},
        {0,   0,   0,   0,   0,   0,   0,   0}
    };
    public static void main(String[] args) throws IOException {
        double[][] dctMatrix = new double[BLOCK_SIZE][BLOCK_SIZE];

        // Заполняем DCT-матрицу как в примере
        dctMatrix[0][5] = 100;
        dctMatrix[0][6] = 100;
        dctMatrix[0][7] = 100;
        dctMatrix[1][6] = 100;
        dctMatrix[1][7] = 100;
        dctMatrix[2][7] = 100;

        double[][] spatial = iDCTII(CUSTOM_DCT_MATRIX);

        // Преобразуем в изображение
        BufferedImage image = new BufferedImage(BLOCK_SIZE, BLOCK_SIZE, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < BLOCK_SIZE; x++) {
            for (int y = 0; y < BLOCK_SIZE; y++) {
                int val = (int) Math.round(spatial[x][y] + 128); // сдвиг к диапазону [0, 255]
                val = Math.max(0, Math.min(255, val)); // clamp
                int gray = (val << 16) | (val << 8) | val;
                image.setRGB(y, x, gray); // учти: x=строка, y=столбец
            }
        }

        // Сохраняем изображение
        ImageIO.write(image, "png", new File("output.png"));
        System.out.println("Готово: output.png");
    }

    public static double[][] iDCTII(double[][] block) {
        double[][] result = new double[BLOCK_SIZE][BLOCK_SIZE];
        for (int x = 0; x < BLOCK_SIZE; x++) {
            for (int y = 0; y < BLOCK_SIZE; y++) {
                double sum = 0;
                for (int u = 0; u < BLOCK_SIZE; u++) {
                    for (int v = 0; v < BLOCK_SIZE; v++) {
                        double alphaU = (u == 0) ? Math.sqrt(1.0 / BLOCK_SIZE) : Math.sqrt(2.0 / BLOCK_SIZE);
                        double alphaV = (v == 0) ? Math.sqrt(1.0 / BLOCK_SIZE) : Math.sqrt(2.0 / BLOCK_SIZE);
                        sum += alphaU * alphaV * block[u][v] *
                               Math.cos(Math.PI * (2 * x + 1) * u / (2.0 * BLOCK_SIZE)) *
                               Math.cos(Math.PI * (2 * y + 1) * v / (2.0 * BLOCK_SIZE));
                    }
                }
                result[x][y] = sum;
            }
        }
        return result;
    }
}
