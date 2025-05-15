package com.jpeg_comression;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EncodeJPEG {
    public static final int BLOCK_SIZE = 8;

    public static void encode(File inputFile, File outputFile, int quality) {
        try (
            DataInputStream inputStream = new DataInputStream(new FileInputStream(inputFile));
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile))
        ) {
            int width = inputStream.readInt();
            int height = inputStream.readInt();

            byte[] rawImageData = inputStream.readAllBytes();

            int[][] yPlane  = BlockSplitter.extractPlanarChannel(rawImageData, width, height, "Y");
            int[][] cbPlane = BlockSplitter.extractPlanarChannel(rawImageData, width, height, "Cb");
            int[][] crPlane = BlockSplitter.extractPlanarChannel(rawImageData, width, height, "Cr");

            List<double[][]> yBlocks  = BlockSplitter.splitIntoBlocks(yPlane);
            List<double[][]> cbBlocks = BlockSplitter.splitIntoBlocks(cbPlane);
            List<double[][]> crBlocks = BlockSplitter.splitIntoBlocks(crPlane);

            List<double[][]> allBlocks = new ArrayList<>();
            allBlocks.addAll(yBlocks);
            allBlocks.addAll(cbBlocks);
            allBlocks.addAll(crBlocks);

            List<double[][]> dctBlocks = DCTProcessor.processDCT(allBlocks);

            List<int[][]> quantizedBlocks = Quantizer.processQuantize(width, height, dctBlocks, quality);

            List<int[][]> dcDiffBlocks = DCDifferenceProcessor.encodeDCDifference(quantizedBlocks);

            List<int[]> zigzagScanned = ZigZagScanner.processScanner(dcDiffBlocks);

            List<String> huffmanEncoded = HuffmanEncoding.processEncoding(width, height, zigzagScanned);

            ListToByteArray.writeEncodedMessageToFile(outputStream, huffmanEncoded, width, height);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
