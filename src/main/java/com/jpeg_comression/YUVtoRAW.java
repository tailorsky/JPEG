package com.jpeg_comression;

public class YUVtoRAW {
    public static byte[] yuv444ToRgb24(byte[] yuv, int width, int height) {
        int frameSize = width * height;
        byte[] rgb = new byte[frameSize * 3];
    
        for (int i = 0; i < frameSize; i++) {
            int y = yuv[i] & 0xFF;
            int u = yuv[frameSize + i] & 0xFF;
            int v = yuv[2 * frameSize + i] & 0xFF;
    
            int c = y - 16;
            int d = u - 128;
            int e = v - 128;
    
            int r = clamp((298 * c + 409 * e + 128) >> 8);
            int g = clamp((298 * c - 100 * d - 208 * e + 128) >> 8);
            int b = clamp((298 * c + 516 * d + 128) >> 8);
    
            int rgbIndex = i * 3;
            rgb[rgbIndex] = (byte) r;
            rgb[rgbIndex + 1] = (byte) g;
            rgb[rgbIndex + 2] = (byte) b;
        }
    
        return rgb;
    }
    
    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }
}
