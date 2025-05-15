package com.jpeg_comression;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComputeCoefs {

    public static void main(String[] args) {
        calculateAndWriteCSV("LennaGRCoefs.csv");
    }

    public static void calculateAndWriteCSV(String outputFileName) {
        File originalFile = new File("LennaGR.yuv");
        List<String> lines = new ArrayList<>();

        if (!originalFile.exists() || originalFile.length() == 0) {
            System.out.println("Файл Lenna.yuv не найден или пуст.");
            return;
        }

        long originalSize = originalFile.length();
        lines.add("quality,compression_ratio");

        for (int i = 0; i <= 100; i += 5) {
            String filename = "LennaGR" + i + ".hjpg";
            File compressedFile = new File(filename);

            if (!compressedFile.exists() || compressedFile.length() == 0) {
                System.out.println("Файл " + filename + " не найден или пуст. Пропускаем.");
                continue;
            }

            long compressedSize = compressedFile.length();
            double ratio = (double) compressedSize / originalSize;
            lines.add(i + "," + ratio);
        }

        try (FileWriter writer = new FileWriter(outputFileName)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
            System.out.println("CSV файл успешно создан: " + outputFileName);
        } catch (IOException e) {
            System.out.println("Ошибка при записи CSV: " + e.getMessage());
        }
    }
}
