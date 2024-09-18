package com.jdo.OUR3d;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Files {
	public static List<String> readAllLines(File f){
		List<String> lines = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = br.readLine()) != null){
				lines.add(line);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return lines;
	}
}
