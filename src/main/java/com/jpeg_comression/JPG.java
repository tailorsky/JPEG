package com.jpeg_comression;

import java.io.*;

public class JPG {
    public static void main(String[] args) throws IOException{
        int quality = 60;
        String filePathEnc = "LennaBW" + quality + ".hjpg";
        String filePathOut = "LennaBW" + quality + ".png";
        JPEG(filePathEnc, filePathOut, quality);
    }

    public static void JPEG (String filePathEnc, String filePathOut, int quality){
        File inputFile = new File("LennaBWDownsampled.yuv");
        File outputFile = new File(filePathEnc); 
        EncodeJPEG.EncodeJPG(inputFile, outputFile, quality);
        DecodeJPEG.DecodeJPG(outputFile, filePathOut);
    }   
}
