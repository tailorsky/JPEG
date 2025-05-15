package com.jpeg_comression;

import java.io.File;
import java.io.IOException;

public class JPG {
    public static void main(String[] args) throws IOException{
        for (int quality = 0; quality <= 100; quality += 5){
            String filePathEnc = "LennaGR" + quality + ".hjpg";
            String filePathOut = "LennaGR" + quality + ".png";
            JPEG(filePathEnc, filePathOut, quality);
        }
    }

    public static void JPEG (String filePathEnc, String filePathOut, int quality){
        File inputFile = new File("LennaGRDownsampled.yuv");
        File outputFile = new File(filePathEnc); 
        EncodeJPEG.encode(inputFile, outputFile, quality);
        DecodeJPEG.decode(outputFile, filePathOut, quality);
    }
}
