package com.jpeg_comression;

public class VLI {
    public static int getIndex(int num) {
        if (num == 0) return 0;
        num = Math.abs(num);
        int result = (int) Math.floor(Math.log(num) / Math.log(2)) + 1;
        return result;
    }

    public static String getCode(int num) {
        if (num == 0) return "";
        int index = getIndex(num);
        String result;

        if (num < 0) {
            result = Integer.toBinaryString((-num) ^ ((1 << index) - 1));
        } else {
            result = Integer.toBinaryString(num);
        }

        int padding = index - result.length();
        for (int i = 0; i < padding; i++) {
            result = "0" + result;
        }

        return result;
    }
    public static int getNum(String code) {
        if (code.equals("")) return 0;
        int len = code.length();
    
        if (code.charAt(0) == '1') {
            return Integer.parseInt(code, 2);
        } else {
            StringBuilder inverted = new StringBuilder();
            for (int i = 0; i < len; i++) {
                inverted.append(code.charAt(i) == '0' ? '1' : '0');
            }
            return -Integer.parseInt(inverted.toString(), 2);
        }
    }
    
}
