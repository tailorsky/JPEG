package com.jpeg_comression;

import java.util.*;

public class ZigZagScanner {
    public static final int BLOCK_SIZE = 8;

    public static List<int[]> processScanner(List<int[][]> blocks){
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++){
            int[] block = zigzagScan(blocks.get(i));
            result.add(block);
        }

        return result;
    }

    public static List<int[][]> processInverseScanner(List<int[]> blocks){
        List<int[][]> result= new ArrayList<>();
        for (int i =0; i < blocks.size(); i++){
            int[][] block = inverseZigzagScan(blocks.get(i));
            result.add(block);
        }
        return result;
    }

    private static int[] zigzagScan(int[][] block){
        int[] result = new int[BLOCK_SIZE*BLOCK_SIZE];
        
        int index = 0;

        for (int sum = 0; sum <= 2 * (BLOCK_SIZE - 1); sum++){
            if (sum % 2 == 0) {
                for (int y = 0; y <= sum; y++){
                    int x = sum - y;
                    if (x < BLOCK_SIZE && y < BLOCK_SIZE) result[index++] = block[y][x];
                }
            }
            else {
                for (int x = 0; x <= sum; x++){
                    int y = sum - x;
                    if (x < BLOCK_SIZE && y < BLOCK_SIZE) result[index++] = block[y][x];
                }
            }
        }
        return result;
    }

    private static int[][] inverseZigzagScan (int[] data){
        int[][] result = new int[BLOCK_SIZE][BLOCK_SIZE];
        int index = 0;
        for (int sum = 0; sum <= 2 * (BLOCK_SIZE - 1); sum++) {
            if (sum % 2 == 0) {
                for (int y = 0; y <= sum; y++) {
                    int x = sum - y;
                    if (x < BLOCK_SIZE && y < BLOCK_SIZE) result[y][x] = data[index++];
                }
            } else {
                for (int x = 0; x <= sum; x++) {
                    int y = sum - x;
                    if (x < BLOCK_SIZE && y < BLOCK_SIZE) result[y][x] = data[index++];
                }
            }
        }
        return result;
    }
}
