package com.jpeg_comression;

import java.io.*;
import java.util.*;

public class EncodeJPEG {
    public static final int BLOCK_SIZE = 8;

    public static void EncodeJPG(File inputFile, File outputFile, int quality){
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(inputFile)); DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile))){
            int width = inputStream.readInt();
            int height = inputStream.readInt();
            
            byte[] data = inputStream.readAllBytes();

            int[][] Y = BlockSplitter.extractPlanarChannel(data, width, height, "Y");
            int[][] Cb = BlockSplitter.extractPlanarChannel(data, width, height, "Cb");
            int[][] Cr = BlockSplitter.extractPlanarChannel(data, width, height, "Cr");

            List<double[][]> Ybl = BlockSplitter.splitIntoBlocks(Y);
            List<double[][]> Cbbl = BlockSplitter.splitIntoBlocks(Cb);
            List<double[][]> Crbl = BlockSplitter.splitIntoBlocks(Cr);

            List<double[][]> YCbCrBlocked = new ArrayList<>();
            YCbCrBlocked.addAll(Ybl);
            YCbCrBlocked.addAll(Cbbl);
            YCbCrBlocked.addAll(Crbl);

            List<double[][]> YCbCrDCT = DCTProcessor.processDCT(YCbCrBlocked);
            List<int[][]> YCbCrQuantized = Quantizer.processQuantize(width, height, YCbCrDCT, quality);
            
            List<int[][]> YCbCtDiff = DCDifferenceProcessor.encodeDCDifference(YCbCrQuantized);

            List<int[]> YCbCrZigzag = ZigZagScanner.processScanner(YCbCtDiff);

            List<String> ENCODED = HuffmanEncoding.processEncoding(width, height, YCbCrZigzag);

            ListToByteArray.writeEncodedMessageToFile(outputStream, ENCODED, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
