package com.jpeg_comression;

import java.util.*;

public class DCDifferenceProcessor {
    public static List<int[][]> encodeDCDifference(List<int[][]> quantizedBlocks) {
        List<int[][]> result = new ArrayList<>();
    
        int previousDC = 0;
    
        for (int i = 0; i < quantizedBlocks.size(); i++) {
            int[][] block = quantizedBlocks.get(i);
            int dc = block[0][0];
            int dcDiff = dc - previousDC;
            previousDC = dc;

            int[][] newBlock = new int[block.length][block[0].length];
            for (int y = 0; y < block.length; y++) {
                System.arraycopy(block[y], 0, newBlock[y], 0, block[0].length);
            }
    
            newBlock[0][0] = dcDiff;
            result.add(newBlock);
        }
    
        return result;
    }

    public static List<int[][]> decodeDCDifference(List<int[][]> diffBlocks) {
        List<int[][]> result = new ArrayList<>();
    
        int previousDC = 0;
    
        for (int i = 0; i < diffBlocks.size(); i++) {
            int[][] block = diffBlocks.get(i);
            int dcDiff = block[0][0];
            int dc = dcDiff + previousDC;
            previousDC = dc;
    
            int[][] newBlock = new int[block.length][block[0].length];
            for (int y = 0; y < block.length; y++) {
                System.arraycopy(block[y], 0, newBlock[y], 0, block[0].length);
            }
    
            newBlock[0][0] = dc;
            result.add(newBlock);
        }
    
        return result;
    }
}
