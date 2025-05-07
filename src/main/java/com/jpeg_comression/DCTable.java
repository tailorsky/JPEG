package com.jpeg_comression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DCTable {
    private int[] category;
	private String[] codeWord;
	private String fileName;
	public DCTable(String fileName) {
		category = new int[12];
		codeWord = new String[12];
		this.fileName = fileName;
		init();
	}
	
	private void init() {
		File file = new File(fileName); 
        if(file.exists()){    
            try {    
                FileReader fileReader = new FileReader(file);    
                BufferedReader br = new BufferedReader(fileReader);    
                String lineContent = null;    
                int index = 0;
                while((lineContent = br.readLine())!=null){    
                	String[] ss = lineContent.split("\\s\\s");
                	category[index] = Integer.parseInt(ss[0]);
                	codeWord[index++] = ss[1];
                }    
                br.close();    
                fileReader.close();    
            } catch (FileNotFoundException e) {    
                System.out.println("[INFO] File does not exist");    
                e.printStackTrace();    
            } catch (IOException e) {    
                System.out.println("[INFO] Io exception");    
                e.printStackTrace();    
            }    
        }  
	}
	
	public String getCodeWord(int index) {
		for(int i=0; i<12; i++) {
			if(category[i]==index)
				return codeWord[i];
		}
		return "";
	}
	
	/**
	 * @param codeWord
	 * @return
	 */
	public int getCategory(String codeWord) {
		for(int i=0; i<12; i++) {
			if(this.codeWord[i].equals(codeWord))
				return category[i];
		}
		return -1;
	}
}
