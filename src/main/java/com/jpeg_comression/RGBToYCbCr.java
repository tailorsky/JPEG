package com.jpeg_comression;

import java.io.*;

public class RGBToYCbCr {
    public static void main(String[] args) throws IOException {
        File inputFile = new File("o.raw");
        File outputFile = new File("o.yuv");

        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(inputFile));
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile))) {

            fromRGBtoYCbCr(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fromRGBtoYCbCr(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        int width = inputStream.readInt();
        int height = inputStream.readInt();

        byte[] data = inputStream.readAllBytes();
        int numPixels = width * height;

        byte[] YPlane = new byte[numPixels];
        byte[] CbPlane = new byte[numPixels];
        byte[] CrPlane = new byte[numPixels];

        for (int i = 0; i < numPixels; i++) {
            int R = data[i * 3] & 0xFF;
            int G = data[i * 3 + 1] & 0xFF;
            int B = data[i * 3 + 2] & 0xFF;

            int Y  = clamp((int)( 0.299 * R + 0.587 * G + 0.114 * B));
            int Cb = clamp((int)(-0.168736 * R - 0.331264 * G + 0.5 * B + 128));
            int Cr = clamp((int)( 0.5 * R - 0.418688 * G - 0.081312 * B + 128));

            YPlane[i] = (byte) Y;
            CbPlane[i] = (byte) Cb;
            CrPlane[i] = (byte) Cr;
        }

        outputStream.writeInt(width);
        outputStream.writeInt(height);

        outputStream.write(YPlane);
        outputStream.write(CbPlane);
        outputStream.write(CrPlane);
    }

    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }
}
