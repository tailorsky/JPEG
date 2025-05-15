package com.jpeg_comression;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class DecodeJPEG {

    public static void decode(File inputFile, String outputFile, int quality) {
        try (
            DataInputStream inputStream = new DataInputStream(new FileInputStream(inputFile));
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile))
        ) {
            int width = inputStream.readInt();
            int height = inputStream.readInt();

            List<String> bitStrings = ListToByteArray.fileToBitString(inputStream);

            List<int[]> decodedCoefficients = HuffmanEncoding.processDecoding(width, height, bitStrings);

            List<int[][]> reorderedBlocks = ZigZagScanner.processInverseScanner(decodedCoefficients);

            List<int[][]> blocksWithRestoredDC = DCDifferenceProcessor.decodeDCDifference(reorderedBlocks);

            List<double[][]> dequantizedBlocks = Quantizer.processDequantize(width, height, blocksWithRestoredDC, quality);

            List<double[][]> spatialBlocks = DCTProcessor.processiDCT(dequantizedBlocks);

            List<double[][]> yBlocks  = BlockSplitter.separateBlocksByChannels(width, height, spatialBlocks, "Y");
            List<double[][]> cbBlocks = BlockSplitter.separateBlocksByChannels(width, height, spatialBlocks, "Cb");
            List<double[][]> crBlocks = BlockSplitter.separateBlocksByChannels(width, height, spatialBlocks, "Cr");

            int[][] yPlane  = BlockSplitter.blockJoiner(width, height, yBlocks, "Y");
            int[][] cbPlane = BlockSplitter.blockJoiner(width, height, cbBlocks, "Cb");
            int[][] crPlane = BlockSplitter.blockJoiner(width, height, crBlocks, "Cr");

            outputStream.writeInt(width);
            outputStream.writeInt(height);

            byte[] yuvData = BlockSplitter.combineChannels(yPlane, cbPlane, crPlane, width, height);
            yuvData = BlockSplitter.upsample420to444(yuvData, width, height);

            byte[] rgbData = YUVtoRAW.yuv444ToRgb24(yuvData, width, height);

            RAWtoPNG.saveRgb24ToPng(rgbData, width, height, outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
