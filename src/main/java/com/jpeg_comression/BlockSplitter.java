package com.jpeg_comression;

import java.util.*;

public class BlockSplitter {

    public static final int BLOCK_SIZE = 8;

    public static List<double[][]> splitIntoBlocks(int[][] channel) {
        int height = channel.length;
        int width = channel[0].length;

        int blocksVertically = (int) Math.ceil(height / (double) BLOCK_SIZE);
        int blocksHorizontally = (int) Math.ceil(width / (double) BLOCK_SIZE);

        List<double[][]> result = new ArrayList<>();

        for (int by = 0; by < blocksVertically; by++) {
            for (int bx = 0; bx < blocksHorizontally; bx++) {
                double[][] block = new double[BLOCK_SIZE][BLOCK_SIZE];
                for (int y = 0; y < BLOCK_SIZE; y++) {
                    for (int x = 0; x < BLOCK_SIZE; x++) {
                        int srcY = by * BLOCK_SIZE + y;
                        int srcX = bx * BLOCK_SIZE + x;
                        if (srcY < height && srcX < width) {
                            block[y][x] = channel[srcY][srcX];
                        } else {
                            block[y][x] = 0;
                        }
                    }
                }
                result.add(block);
            }
        }
        return result;
    }

    public static int[][] blockJoiner(int width, int height, List<double[][]> blocks, String channel) {
        final int BLOCK_SIZE = 8;
        int[][] channelData;
    
        boolean isY = channel.equals("Y");
        int w = isY ? width : width / 2;
        int h = isY ? height : height / 2;
    
        channelData = new int[h][w];
    
        int blockIndex = 0;
    
        for (int y = 0; y < h; y += BLOCK_SIZE) {
            for (int x = 0; x < w; x += BLOCK_SIZE) {
                if (blockIndex >= blocks.size()) {
                    System.out.println("Ошибка: превышен размер списка блоков, блок: " + blockIndex);
                    break;
                }
    
                double[][] block = blocks.get(blockIndex++);
    
                for (int dy = 0; dy < BLOCK_SIZE; dy++) {
                    for (int dx = 0; dx < BLOCK_SIZE; dx++) {
                        int yy = y + dy;
                        int xx = x + dx;
    
                        if (yy < h && xx < w) {
                            channelData[yy][xx] = (int) Math.round(block[dy][dx]);
                        }
                    }
                }
            }
        }
    
        return channelData;
    }

    public static int[][] extractPlanarChannel(byte[] data, int width, int height, String channel) {
        int[][] matrix;
    
        int ySize = width * height;
        int chromaWidth = width / 2;
        int chromaHeight = height / 2;
        int chromaSize = chromaWidth * chromaHeight;
    
        switch (channel) {
            case "Y":
                matrix = new int[height][width];
                for (int i = 0; i < ySize; i++) {
                    int val = data[i] & 0xFF;
                    matrix[i / width][i % width] = val;
                }
                break;
            case "Cb":
                matrix = new int[chromaHeight][chromaWidth];
                for (int i = 0; i < chromaSize; i++) {
                    int val = data[ySize + i] & 0xFF;
                    matrix[i / chromaWidth][i % chromaWidth] = val;
                }
                break;
            case "Cr":
                matrix = new int[chromaHeight][chromaWidth];
                for (int i = 0; i < chromaSize; i++) {
                    int val = data[ySize + chromaSize + i] & 0xFF;
                    matrix[i / chromaWidth][i % chromaWidth] = val;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown channel: " + channel);
        }
    
        return matrix;
    }

    public static List<double[][]> separateBlocksByChannels(int width, int height, List<double[][]> blocks, String type) {
        List<double[][]> result = new ArrayList<>();
        int yBlockCount = (width * height) / (BLOCK_SIZE * BLOCK_SIZE);
        int chromaBlockCount = (width / 2 * height / 2) / (BLOCK_SIZE * BLOCK_SIZE);

        switch (type) {
            case "Y":
                for (int blockIndex = 0; blockIndex < yBlockCount; blockIndex++) {
                    result.add(blocks.get(blockIndex));
                }   
                break;
            case "Cb":
                for (int blockIndex = yBlockCount; blockIndex < yBlockCount + chromaBlockCount; blockIndex++) {
                    result.add(blocks.get(blockIndex));
                }   
                break;
            case "Cr":
                for (int blockIndex = yBlockCount + chromaBlockCount; blockIndex < yBlockCount + chromaBlockCount * 2; blockIndex++) {
                    result.add(blocks.get(blockIndex));
                }   
                break;
        }
        return result;
    }

    public static List<double[][]> separateBlocksByChannels444(int width, int height, List<double[][]> blocks, String type) {
        List<double[][]> result = new ArrayList<>();
        int yBlockCount = (width * height) / (BLOCK_SIZE * BLOCK_SIZE);

        switch (type) {
            case "Y":
                for (int blockIndex = 0; blockIndex < yBlockCount; blockIndex++) {
                    result.add(blocks.get(blockIndex));
                }   
                break;
            case "Cb":
                for (int blockIndex = yBlockCount; blockIndex < yBlockCount + yBlockCount; blockIndex++) {
                    result.add(blocks.get(blockIndex));
                }   
                break;
            case "Cr":
                for (int blockIndex = yBlockCount + yBlockCount; blockIndex < yBlockCount + yBlockCount * 2; blockIndex++) {
                    result.add(blocks.get(blockIndex));
                }   
                break;
        }
        return result;
    }

    public static byte[] combineChannels(int[][] Y, int[][] Cb, int[][] Cr, int width, int height) {
        int ySize = width * height;
        int chromaWidth = width / 2;
        int chromaHeight = height / 2;
        int chromaSize = chromaWidth * chromaHeight;
        
        byte[] data = new byte[ySize + 2 * chromaSize]; // Y + Cb + Cr

        for (int i = 0; i < ySize; i++) {
            data[i] = (byte) Y[i / width][i % width];
        }

        for (int i = 0; i < chromaSize; i++) {
            data[ySize + i] = (byte) Cb[i / chromaWidth][i % chromaWidth];
        }

        for (int i = 0; i < chromaSize; i++) {
            data[ySize + chromaSize + i] = (byte) Cr[i / chromaWidth][i % chromaWidth];
        }
    
        return data;
    }

    public static byte[] upsample420to444(byte[] yuv420, int width, int height) {
        int frameSize = width * height;
        int chromaSize = frameSize / 4;
    
        byte[] y = Arrays.copyOfRange(yuv420, 0, frameSize);
        byte[] u = Arrays.copyOfRange(yuv420, frameSize, frameSize + chromaSize);
        byte[] v = Arrays.copyOfRange(yuv420, frameSize + chromaSize, frameSize + 2 * chromaSize);
    
        byte[] uFull = new byte[frameSize];
        byte[] vFull = new byte[frameSize];

        for (int j = 0; j < height / 2; j++) {
            for (int i = 0; i < width / 2; i++) {
                int srcIdx = j * (width / 2) + i;
    
                int dstIdx1 = (2 * j) * width + (2 * i);
                int dstIdx2 = (2 * j) * width + (2 * i + 1);
                int dstIdx3 = (2 * j + 1) * width + (2 * i);
                int dstIdx4 = (2 * j + 1) * width + (2 * i + 1);
    
                uFull[dstIdx1] = u[srcIdx];
                uFull[dstIdx2] = u[srcIdx];
                uFull[dstIdx3] = u[srcIdx];
                uFull[dstIdx4] = u[srcIdx];
    
                vFull[dstIdx1] = v[srcIdx];
                vFull[dstIdx2] = v[srcIdx];
                vFull[dstIdx3] = v[srcIdx];
                vFull[dstIdx4] = v[srcIdx];
            }
        }

        byte[] yuv444 = new byte[frameSize * 3];
        System.arraycopy(y, 0, yuv444, 0, frameSize);
        System.arraycopy(uFull, 0, yuv444, frameSize, frameSize);
        System.arraycopy(vFull, 0, yuv444, 2 * frameSize, frameSize);
    
        return yuv444;
    }
}
