package com.jdo.OUR3d;

import java.io.File;
import java.io.FileFilter;

public class OBJFileFilter implements FileFilter{

	public boolean accept(File file){
		if (file.getName().endsWith(".obj")){
			System.out.println("Here3333333!");
			return true;
		}else{
			return false;
		}
	}

}