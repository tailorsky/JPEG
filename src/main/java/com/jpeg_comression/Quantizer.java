package com.jpeg_comression;

import java.util.ArrayList;
import java.util.List;

public class Quantizer {
    private static final int BLOCK_SIZE = 8;

    private static final int[][] BASE_LUMINANCE_QUANT_TABLE = {
        {16, 11, 10, 16, 24, 40, 51, 61},
        {12, 12, 14, 19, 26, 58, 60, 55},
        {14, 13, 16, 24, 40, 57, 69, 56},
        {14, 17, 22, 29, 51, 87, 80, 62},
        {18, 22, 37, 56, 68,109,103, 77},
        {24, 35, 55, 64, 81,104,113, 92},
        {49, 64, 78, 87,103,121,120,101},
        {72, 92, 95, 98,112,100,103, 99}
    };

    private static final int[][] BASE_CHROMINANCE_QUANT_TABLE = {
        {17, 18, 24, 47, 99, 99, 99, 99},
        {18, 21, 26, 66, 99, 99, 99, 99},
        {24, 26, 56, 99, 99, 99, 99, 99},
        {47, 66, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 99, 99, 99, 99, 99}
    };

    public enum ChannelType {
        LUMINANCE,
        CHROMINANCE
    }

    public static int[][] scaleQuantTable(int[][] baseTable, int quality) {
        quality = Math.max(1, Math.min(100, quality));
        double scaleFactor = (quality < 50)
                ? 5000.0 / quality
                : 200.0 - 2 * quality;
        scaleFactor /= 100.0;

        int[][] scaledTable = new int[BLOCK_SIZE][BLOCK_SIZE];
        for (int y = 0; y < BLOCK_SIZE; y++) {
            for (int x = 0; x < BLOCK_SIZE; x++) {
                int scaled = (int) Math.round(baseTable[y][x] * scaleFactor);
                scaledTable[y][x] = Math.min(255, Math.max(1, scaled));
            }
        }
        return scaledTable;
    }

    public static List<int[][]> processQuantize(int width, int height, List<double[][]> dctBlocks, int quality) {
        int[][] luminanceTable = scaleQuantTable(BASE_LUMINANCE_QUANT_TABLE, quality);
        int[][] chrominanceTable = scaleQuantTable(BASE_CHROMINANCE_QUANT_TABLE, quality);

        int yBlockCount = (width * height) / (BLOCK_SIZE * BLOCK_SIZE);
        int chromaBlockCount = ((width / 2) * (height / 2)) / (BLOCK_SIZE * BLOCK_SIZE);
        int totalBlocks = yBlockCount + 2 * chromaBlockCount;

        List<int[][]> result = new ArrayList<>(totalBlocks);
        for (int i = 0; i < totalBlocks; i++) {
            ChannelType type = (i < yBlockCount) ? ChannelType.LUMINANCE : ChannelType.CHROMINANCE;
            int[][] table = (type == ChannelType.LUMINANCE) ? luminanceTable : chrominanceTable;
            result.add(quantize(dctBlocks.get(i), table));
        }
        return result;
    }

    public static List<double[][]> processDequantize(int width, int height, List<int[][]> quantizedBlocks, int quality) {
        int[][] luminanceTable = scaleQuantTable(BASE_LUMINANCE_QUANT_TABLE, quality);
        int[][] chrominanceTable = scaleQuantTable(BASE_CHROMINANCE_QUANT_TABLE, quality);

        int yBlockCount = (width * height) / (BLOCK_SIZE * BLOCK_SIZE);
        int chromaBlockCount = ((width / 2) * (height / 2)) / (BLOCK_SIZE * BLOCK_SIZE);
        int totalBlocks = yBlockCount + 2 * chromaBlockCount;

        List<double[][]> result = new ArrayList<>(totalBlocks);
        for (int i = 0; i < totalBlocks; i++) {
            ChannelType type = (i < yBlockCount) ? ChannelType.LUMINANCE : ChannelType.CHROMINANCE;
            int[][] table = (type == ChannelType.LUMINANCE) ? luminanceTable : chrominanceTable;
            result.add(dequantize(quantizedBlocks.get(i), table));
        }
        return result;
    }

    private static int[][] quantize(double[][] dctBlock, int[][] quantTable) {
        int[][] result = new int[BLOCK_SIZE][BLOCK_SIZE];
        for (int y = 0; y < BLOCK_SIZE; y++) {
            for (int x = 0; x < BLOCK_SIZE; x++) {
                result[y][x] = (int) Math.round(dctBlock[y][x] / quantTable[y][x]);
            }
        }
        return result;
    }

    private static double[][] dequantize(int[][] quantBlock, int[][] quantTable) {
        double[][] result = new double[BLOCK_SIZE][BLOCK_SIZE];
        for (int y = 0; y < BLOCK_SIZE; y++) {
            for (int x = 0; x < BLOCK_SIZE; x++) {
                result[y][x] = quantBlock[y][x] * quantTable[y][x];
            }
        }
        return result;
    }
}
