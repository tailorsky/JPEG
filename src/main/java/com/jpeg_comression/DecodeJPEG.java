package com.jpeg_comression;

import java.util.*;
import java.io.*;

public class DecodeJPEG {
    public static void DecodeJPG(File inputFile, String outputFile){
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(inputFile)); DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile))){
            int width = inputStream.readInt();
            int height = inputStream.readInt();

            List<String> fromFile = ListToByteArray.fileToBitString(inputStream);

            List<int[]> DECODED = HuffmanEncoding.processDecoding(width, height, fromFile);

            List<int[][]> antizigzag = ZigZagScanner.processInverseScanner(DECODED);

            List<int[][]> AntiDiff = DCDifferenceProcessor.decodeDCDifference(antizigzag);

            List<double[][]> AntiQuant = Quantizer.processDequantize(width, height, AntiDiff);

            List<double[][]> antiDCT = DCTProcessor.processiDCT(AntiQuant);

            List<double[][]> Yantiblock = BlockSplitter.separateBlocksByChannels(width, height, antiDCT, "Y");
            List<double[][]> Cbantiblock = BlockSplitter.separateBlocksByChannels(width, height, antiDCT, "Cb");
            List<double[][]> Crantiblock = BlockSplitter.separateBlocksByChannels(width, height, antiDCT, "Cr");

            int[][] YH = BlockSplitter.blockJoiner(width, height, Yantiblock, "Y");
            int[][] CbH = BlockSplitter.blockJoiner(width, height, Cbantiblock, "Cb");
            int[][] CrH = BlockSplitter.blockJoiner(width, height, Crantiblock, "Cr");

            outputStream.writeInt(width);
            outputStream.writeInt(height);

            byte[] picture = BlockSplitter.combineChannels(YH, CbH, CrH, width, height);
            picture = BlockSplitter.upsample420to444(picture, width, height);

            picture = YUVtoRAW.yuv444ToRgb24(picture, width, height);
            RAWtoPNG.saveRgb24ToPng(picture, width, height, outputFile);
            //outputStream.write(picture);

            //TestClass.displayYUV(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
