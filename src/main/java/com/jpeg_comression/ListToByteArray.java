package com.jpeg_comression;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ListToByteArray {
    public static void writeEncodedMessageToFile(DataOutputStream outputStream, List<String> encodedMessages, int width, int height) throws IOException{
        outputStream.writeInt(width);
        outputStream.writeInt(height);
        String str = getString(encodedMessages);
        int zeroCnt = (8 - str.length() % 8) % 8;
        outputStream.writeInt(zeroCnt);
        int[] sizes = getSizes(encodedMessages);
        outputStream.writeInt(sizes.length);
        for (int size : sizes){
            outputStream.writeInt(size);
        }
        BitToByteWriter writer = new BitToByteWriter(outputStream);
        for (char bit : str.toCharArray()) {
            writer.writeBit(bit == '1' ? 1 : 0);
        }
        writer.close();
    }
    public static int getSize (List<String> encodedMessages){
        int size = 0;
        for (int i = 0; i < encodedMessages.size(); i++){
            size += encodedMessages.get(i).length();
        }
        System.out.println(size);
        return size % 8;
    }

    public static String getString(List<String> encodedMessages){
        StringBuilder result = new StringBuilder();
        for (String msg : encodedMessages){
            result.append(msg);
        }
        return result.toString();
    }    

    public static int[] getSizes (List<String> encodedMessages){
        int[] sizes = new int[encodedMessages.size()];
        for (int i = 0; i < encodedMessages.size(); i++){
            sizes[i] = encodedMessages.get(i).length();
        }
        return sizes;
    }

    public static List<String> fileToBitString(DataInputStream inputStream) throws IOException {
        List<String> result = new ArrayList<>();
        int zeroCnt = inputStream.readInt();

        int arrSize = inputStream.readInt();
        int[] sizes = new int[arrSize];

        for (int i = 0; i < arrSize; i++){
            sizes[i] = inputStream.readInt(); 
        }

        byte[] bytes = inputStream.readAllBytes();
        StringBuilder bitString = new StringBuilder();
        
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                bitString.append((b >> i) & 1);
            }
        }
        String allBits = bitString.substring(0, bitString.length() - zeroCnt);
        int cursor = 0;
        for (int size : sizes) {
            result.add(allBits.substring(cursor, cursor + size));
            cursor += size;
        }
        return result;
    }
}
