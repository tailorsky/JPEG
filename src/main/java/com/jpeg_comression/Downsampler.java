package com.jpeg_comression;

import java.io.*;

public class Downsampler {
    public static void main(String[] args) throws IOException {
        File inputFile = new File("PictureBWD.yuv");
        File outputFile = new File("PictureBWDDownsampled.yuv");

        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(inputFile));
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile))) {
            proccessDownSamplig(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void proccessDownSamplig(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        int width = inputStream.readInt();
        int height = inputStream.readInt();

        byte[] data = inputStream.readAllBytes();

        outputStream.writeInt(width);
        outputStream.writeInt(height);

        int ySize = width * height;
        if (data.length != ySize * 3) throw new RuntimeException("Размер файла неправильный!");
        else {
            int[][] Y = extractPlanarChannel(data, width, height, "Y");
            int[][] Cb = extractPlanarChannel(data, width, height, "Cb");
            int[][] Cr = extractPlanarChannel(data, width, height, "Cr");

            int[][] CbDown = downsample420(Cb);
            int[][] CrDown = downsample420(Cr);

            writePlanarYUV420(Y, CbDown, CrDown, outputStream);
        }
    }

    private static int[][] extractPlanarChannel(byte[] data, int width, int height, String channel) {
        int[][] matrix = new int[height][width];
        int ySize = width * height;

        switch (channel) {
            case "Y":
                for (int i = 0; i < ySize; i++) {
                    matrix[i / width][i % width] = data[i] & 0xFF;
                }
                break;
            case "Cb":
                for (int i = 0; i < ySize; i++) {
                    matrix[i / width][i % width] = data[ySize + i] & 0xFF;
                }
                break;
            case "Cr":
                for (int i = 0; i < ySize; i++) {
                    matrix[i / width][i % width] = data[2 * ySize + i] & 0xFF;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown channel: " + channel);
        }

        return matrix;
    }

    public static int[][] downsample420(int[][] inputMatrix) {
        int h = (inputMatrix.length + 1) / 2;
        int w = (inputMatrix[0].length + 1) / 2;

        int[][] result = new int[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int sum = 0;
                int count = 0;

                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        int iy = y * 2 + dy;
                        int ix = x * 2 + dx;
                        if (iy < inputMatrix.length && ix < inputMatrix[0].length) {
                            sum += inputMatrix[iy][ix];
                            count++;
                        }
                    }
                }

                result[y][x] = sum / count;
            }
        }

        return result;
    }

    public static void writePlanarYUV420(int[][] Y, int[][] CbDown, int[][] CrDown, DataOutputStream outputStream) throws IOException {
        int height = Y.length;
        int width = Y[0].length;

        int chromaHeight = (height + 1) / 2;
        int chromaWidth = (width + 1) / 2;

        byte[] yuvData = new byte[width * height + 2 * chromaWidth * chromaHeight];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                yuvData[index++] = (byte) Y[y][x];
            }
        }
        for (int y = 0; y < chromaHeight; y++) {
            for (int x = 0; x < chromaWidth; x++) {
                yuvData[index++] = (byte) CbDown[y][x];
            }
        }
        for (int y = 0; y < chromaHeight; y++) {
            for (int x = 0; x < chromaWidth; x++) {
                yuvData[index++] = (byte) CrDown[y][x];
            }
        }

        outputStream.write(yuvData);
    }
}