package com.jpeg_comression;

import java.awt.Point;
import java.io.*;
import java.util.*;

public class ACTable {
    private Vector<Point> runSize;
	private Vector<String> codeWord;
	private String fileName;
	public ACTable(String fileName) {
		runSize = new Vector<Point>();
		codeWord = new Vector<String>();
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
                while((lineContent = br.readLine())!=null){
                	String[] ss = lineContent.split("\\s\\s");
                	runSize.addElement(handleRS(ss[0]));
                	codeWord.addElement(ss[1]);
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
	
	private Point handleRS(String s) {
		int x, y;
		String[] ss = s.split("/");
		if(ss[0].charAt(0) >= 'A')
			x = ss[0].charAt(0)-'A' +10;
		else 
			x = ss[0].charAt(0)-'0';
		if(ss[1].charAt(0) >= 'A')
			y = ss[1].charAt(0)-'A' +10;
		else 
			y = ss[1].charAt(0)-'0';
		return (new Point(x, y));
	}
	
	public String getCodeWord(int x, int y) {
		for(int i=0; i<runSize.size(); i++) {
			if(runSize.elementAt(i).x==x && runSize.elementAt(i).y==y)
				return codeWord.elementAt(i);
		}
		return "";
	}
	
	/**
	 * @param codeWord
	 * @return 
	 */
	public Point getRunSize(String codeWord) {
		for(int i=0; i<this.codeWord.size(); i++) {
			if(this.codeWord.elementAt(i).equals(codeWord))
				return runSize.elementAt(i);
		}
		return (new Point(-1,-1));
	}
}
