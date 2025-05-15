package com.jpeg_comression;

import java.util.ArrayList;
import java.util.List;

public class DCTProcessor {

    public static final int BLOCK_SIZE = 8;

    private static final double[][] cosTable = new double[BLOCK_SIZE][BLOCK_SIZE];
    static {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            for (int j = 0; j < BLOCK_SIZE; j++) {
                cosTable[i][j] = Math.cos(Math.PI * (2 * i + 1) * j / (2.0 * BLOCK_SIZE));
            }
        }
    }
    private static double[][] shiftBlockBeforeDCT(double[][] block) { //added
        double[][] shifted = new double[BLOCK_SIZE][BLOCK_SIZE];
        for (int y = 0; y < BLOCK_SIZE; y++) {
            for (int x = 0; x < BLOCK_SIZE; x++) {
                shifted[y][x] = block[y][x] - 128.0;
            }
        }
        return shifted;
    }
    private static double[][] shiftBlockAfterIDCT(double[][] block) {
        double[][] shifted = new double[BLOCK_SIZE][BLOCK_SIZE];
        for (int y = 0; y < BLOCK_SIZE; y++) {
            for (int x = 0; x < BLOCK_SIZE; x++) {
                double val = block[y][x] + 128.0;
                shifted[y][x] = Math.min(255.0, Math.max(0.0, val));
            }
        }
        return shifted;
    }

    public static List<double[][]> processDCT(List<double[][]> blocks) {
        List<double[][]> result = new ArrayList<>(blocks.size());
        for (double[][] block : blocks) {
            double[][] shifted = shiftBlockBeforeDCT(block);
            result.add(DCTII(shifted));
        }
        return result;
    }

    public static List<double[][]> processiDCT(List<double[][]> dctBlocks) {
        List<double[][]> result = new ArrayList<>(dctBlocks.size());
        for (double[][] block : dctBlocks) {
            double[][] idctBlock = iDCTII(block);
            double[][] shiftedBack = shiftBlockAfterIDCT(idctBlock);
            result.add(shiftedBack);
        }
        return result;
    }

    public static double[][] DCTII(double[][] block) {
        double[][] result = new double[BLOCK_SIZE][BLOCK_SIZE];

        for (int u = 0; u < BLOCK_SIZE; u++) {
            for (int v = 0; v < BLOCK_SIZE; v++) {
                double sum = 0;
                for (int x = 0; x < BLOCK_SIZE; x++) {
                    for (int y = 0; y < BLOCK_SIZE; y++) {
                        sum += block[x][y] * cosTable[x][u] * cosTable[y][v];
                    }
                }
                double alphaU = (u == 0) ? Math.sqrt(1.0 / BLOCK_SIZE) : Math.sqrt(2.0 / BLOCK_SIZE);
                double alphaV = (v == 0) ? Math.sqrt(1.0 / BLOCK_SIZE) : Math.sqrt(2.0 / BLOCK_SIZE);
                result[u][v] = alphaU * alphaV * sum;
            }
        }

        return result;
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
                        sum += alphaU * alphaV * block[u][v] * cosTable[x][u] * cosTable[y][v];
                    }
                }
                result[x][y] = sum;
            }
        }

        return result;
    }
}
