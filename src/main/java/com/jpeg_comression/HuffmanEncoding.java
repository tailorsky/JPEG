package com.jpeg_comression;

import java.awt.Point;
import java.util.*;

public class HuffmanEncoding {
    private static DCTable DCC, DCL;
    private static ACTable ACC, ACL;
    public static final int BLOCK_SIZE = 8;

    public enum ChannelType {
        LUMINANCE,
        CHROMINANCE
    }

    public static List<String> processEncoding(int width, int height, List<int[]> blocks){
        DCC = new DCTable("./Huffman Table/DCC");
        DCL = new DCTable("./Huffman Table/DCL");
        ACC = new ACTable("./Huffman Table/ACC");
        ACL = new ACTable("./Huffman Table/ACL");

        List<String> result = new ArrayList<>();

        int yBlockCount = (width * height) / (BLOCK_SIZE * BLOCK_SIZE);
        int chromaBlockCount = (width / 2 * height / 2) / (BLOCK_SIZE * BLOCK_SIZE);
        int totalBlocks = yBlockCount + 2 * chromaBlockCount;

        for (int blockIndex = 0; blockIndex < totalBlocks; blockIndex++) {
            if (blockIndex < yBlockCount) 
                result.add(LumEncoding(blocks.get(blockIndex)));
            else
                result.add(ChrEncoding(blocks.get(blockIndex)));
        }
        return result;
    }

    public static List<int[]> processDecoding(int width, int height, List<String> encodedBlocks) {
        DCC = new DCTable("./Huffman Table/DCC");
        DCL = new DCTable("./Huffman Table/DCL");
        ACC = new ACTable("./Huffman Table/ACC");
        ACL = new ACTable("./Huffman Table/ACL");
    
        List<int[]> result = new ArrayList<>();
    
        int yBlockCount = (width * height) / (BLOCK_SIZE * BLOCK_SIZE);
        int chromaBlockCount = (width / 2 * height / 2) / (BLOCK_SIZE * BLOCK_SIZE);
        int totalBlocks = yBlockCount + 2 * chromaBlockCount;

        for (int blockIndex = 0; blockIndex < totalBlocks; blockIndex++) {
            int[] decodedBlock = new int[64];
    
            if (blockIndex < yBlockCount) {
                LumDecoding(encodedBlocks.get(blockIndex), decodedBlock);
            }
            else {
                ChrDecoding(encodedBlocks.get(blockIndex), decodedBlock);
            }
    
            result.add(decodedBlock);
        }
        return result;
    }
    

    public static String LumEncoding(int[] array) {
        Vector<int[]> data = RLEencoding(array);
        data = entropyCoding(data);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < data.size(); i++) {
            int x = data.elementAt(i)[0],
                y = data.elementAt(i)[1],
                z = data.elementAt(i)[2];

            if (i == 0) {
                result.append(DCL.getCodeWord(x));
                result.append(VLI.getCode(y));
            } else {
                result.append(ACL.getCodeWord(x, y));
                result.append(VLI.getCode(z));
            }
        }
        return result.toString();
    }

    public static String ChrEncoding(int[] array) {
        Vector<int[]> data = RLEencoding(array);
        data = entropyCoding(data);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < data.size(); i++) {
            int x = data.elementAt(i)[0],
                y = data.elementAt(i)[1],
                z = data.elementAt(i)[2];

            if (i == 0) {
                result.append(DCC.getCodeWord(x));
                result.append(VLI.getCode(y));
            } else {
                result.append(ACC.getCodeWord(x, y));
                result.append(VLI.getCode(z));
            }
        }
        return result.toString();
    }

    private static Vector<int[]> RLEencoding(int[] array) {
        Vector<int[]> data = new Vector<>();
        int zeroCnt = 0;

        for (int i = 0; i < 64; i++) {
            int[] curData = new int[3];
            if (i == 0) {
                curData[0] = array[0];
                data.addElement(curData);
            } else {
                boolean flag = true;
                for (int j = i; j < 64; j++) {
                    if (array[j] != 0) flag = false;
                }
                if (flag) {
                    curData[0] = curData[1] = 0;
                    data.addElement(curData);
                    break;
                }
                if (zeroCnt == 16) {
                    curData[0] = 15;
                    curData[1] = 0;
                    zeroCnt = 0;
                    data.addElement(curData);
                }
                if (array[i] == 0) {
                    zeroCnt++;
                } else {
                    curData[0] = zeroCnt;
                    curData[1] = array[i];
                    zeroCnt = 0;
                    data.addElement(curData);
                }
            }
        }
        return data;
    }

    private static Vector<int[]> entropyCoding(Vector<int[]> data) {
        for (int i = 0; i < data.size(); i++) {
            int[] curData = data.elementAt(i);
            if (i == 0) {
                int DC = curData[0];
                data.elementAt(0)[0] = VLI.getIndex(DC);
                data.elementAt(0)[1] = DC;
            } else if (data.elementAt(i)[0] != 0 || data.elementAt(i)[1] != 0) {
                data.elementAt(i)[2] = data.elementAt(i)[1];
                data.elementAt(i)[1] = VLI.getIndex(data.elementAt(i)[2]);
            }
        }
        return data;
    }

    public static String ChrDecoding(String data, int[] line) {
        int DCHeadLen = -1;
        for (int rIndex = 2; rIndex < 11; rIndex++) {
            DCHeadLen = DCC.getCategory(data.substring(0, rIndex));
            if (DCHeadLen >= 0) {
                data = data.substring(rIndex);
                break;
            }
        }

        if (DCHeadLen == -1) {
            System.out.println("[INFO] ERROR: ChrDecoding 1!");
        }

        line[0] = VLI.getNum(data.substring(0, DCHeadLen));
        data = data.substring(DCHeadLen);

        int lineLen = 1;
        while (lineLen < 64) {
            Point curP = new Point(-1, -1);
            for (int rIndex = 2; rIndex <= 16; rIndex++) {
                if (data.length() == 0) return data;
                curP = ACC.getRunSize(data.substring(0, rIndex));
                if (!(curP.x == -1 && curP.y == -1)) {
                    data = data.substring(rIndex);
                    break;
                }
            }

            if (curP.x == -1 && curP.y == -1) {
                System.out.println("[INFO] ERROR: ChrDecoding 2!");
            }

            int x = curP.x;
            int y = VLI.getNum(data.substring(0, curP.y));
            data = data.substring(curP.y);

            if (x == y && y == 0) {
                while (lineLen < 64)
                    line[lineLen++] = 0;
                return data;
            }

            for (int i = 0; i < x; i++)
                line[lineLen++] = 0;

            line[lineLen++] = y;
        }
        return data;
    }

    public static String LumDecoding(String data, int[] line) {
        int DCHeadLen = -1;
        for (int rIndex = 2; rIndex <= 9; rIndex++) {
            DCHeadLen = DCL.getCategory(data.substring(0, rIndex));
            if (DCHeadLen >= 0) {
                data = data.substring(rIndex);
                break;
            }
        }

        if (DCHeadLen == -1) {
            System.out.println("[INFO] ERROR: LumDecoding 1!");
        }

        line[0] = VLI.getNum(data.substring(0, DCHeadLen));
        data = data.substring(DCHeadLen);

        int lineLen = 1;
        while (lineLen < 64) {
            Point curP = new Point(-1, -1);
            for (int rIndex = 2; rIndex <= 16; rIndex++) {
                if (data.length() == 0) return data;
                curP = ACL.getRunSize(data.substring(0, rIndex));
                if (!(curP.x == -1 && curP.y == -1)) {
                    data = data.substring(rIndex);
                    break;
                }
            }

            if (curP.x == -1 && curP.y == -1) {
                System.out.println("[INFO] ERROR: LumDecoding 2!");
            }

            int x = curP.x;
            int y = VLI.getNum(data.substring(0, curP.y));
            data = data.substring(curP.y);
            
            if (x == y && y == 0) {
                while (lineLen < 64)
                    line[lineLen++] = 0;
                break;
            }

            for (int i = 0; i < x; i++)
                line[lineLen++] = 0;

            line[lineLen++] = y;
        }
        return data;
    }
}