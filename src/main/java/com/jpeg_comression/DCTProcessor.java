package com.jpeg_comression;

import java.util.*;

public class DCTProcessor {

    public static final int BLOCK_SIZE = 8;
    public static void main(String[] args) {
        
    }

    public static List<double[][]> processDCT(List<double[][]> blocks){
        List<double[][]> result = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++){
            double[][] dctBlock = DCTII(blocks.get(i));
            result.add(dctBlock);
        }

        return result;
    }

    public static List<double[][]> processiDCT(List<double[][]> dctBlocks){
        List<double[][]> result = new ArrayList<>();
        
        for (int i = 0; i < dctBlocks.size(); i++){
            double[][] dctBlock = iDCTII(dctBlocks.get(i));
            result.add(dctBlock);
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
                        sum += block[x][y] *
                               Math.cos(Math.PI * (2 * x + 1) * u / (2.0 * BLOCK_SIZE)) *
                               Math.cos(Math.PI * (2 * y + 1) * v / (2.0 * BLOCK_SIZE));
                    }
                }
                double alphaU = (u == 0) ? Math.sqrt(1.0 / BLOCK_SIZE) : Math.sqrt(2.0 / BLOCK_SIZE);
                double alphaV = (v == 0) ? Math.sqrt(1.0 / BLOCK_SIZE) : Math.sqrt(2.0 / BLOCK_SIZE);
                result[u][v] = alphaU * alphaV * sum; // тут убрал множитель
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
                        sum += alphaU * alphaV * block[u][v] *
                               Math.cos(Math.PI * (2 * x + 1) * u / (2.0 * BLOCK_SIZE)) *
                               Math.cos(Math.PI * (2 * y + 1) * v / (2.0 * BLOCK_SIZE));
                    }
                }
                result[x][y] = sum; // Убрал 0.25 *
            }
        }
        return result;
    }
}
