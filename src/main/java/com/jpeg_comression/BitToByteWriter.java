package com.jpeg_comression;

import java.io.IOException;
import java.io.OutputStream;

public class BitToByteWriter {
    int bitBuffer;
    int bitWritten;
    int bytesWritten;

    OutputStream target;

    public BitToByteWriter(OutputStream target){
        this.target = target;
    }

    public void close() throws IOException{
        if ((bitWritten & 0b111) != 0){
            target.write(bitBuffer);
            bytesWritten += 1;
        }
        target.flush();
        target.close();
    }

    public void writeBit(int bit) throws IOException {
        int bitIndex = 7 - (bitWritten % 8); // MSB first
        if (bit == 1) {
            bitBuffer |= (1 << bitIndex);
        } else {
            bitBuffer &= ~(1 << bitIndex);
        }
    
        bitWritten++;
        if (bitWritten % 8 == 0) {
            target.write(bitBuffer);
            bytesWritten++;
            bitBuffer = 0;
        }
    }
    

    public void writeByte(int val) throws IOException {
        int mask = 1;
        for (int i = 0; i < 8; i++){
            writeBit((val & mask) > 0 ? 1 : 0);
            mask <<= 1;
        }
    }
}
