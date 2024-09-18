package com.jdo.OUR3d;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.Toolkit;

import java.io.File;

import java.nio.file.Files;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageHelper{
	private ImageHelper(){}

	static Map<String, int[][]> textureImageCache = new HashMap<>();

	public static Image generateImage(int[][] newIntImage){
		int imageWidth = newIntImage.length;
		int imageHeight = newIntImage[0].length;
		int imaCounter = 0;
		int[] newImageDataOneDimension =  new int[imageWidth*imageHeight];
		for (int y=0; y < imageHeight; y++){
			for (int x=0; x < imageWidth; x++){
				try {
					newImageDataOneDimension[imaCounter++] = newIntImage[x][y];
				}catch(Exception e){}
			}
		}
		MemoryImageSource mis = new MemoryImageSource(imageWidth, imageHeight, newImageDataOneDimension, 0, imageWidth);
		Image newImage = Toolkit.getDefaultToolkit().createImage(mis);
		return newImage;
	}

	static void loadMTL(File file){
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			String textureKey = null;
			Color kdColor = null;
			for (String line : lines){
				//System.out.println("line:" + line);
				if (line.startsWith("newmtl")){
					if (textureKey != null){
						int[][] singlePixel = new int[1][1];
						singlePixel[0][0] = kdColor.getRGB();
						textureImageCache.put(textureKey, singlePixel);
					}
					textureKey = line.substring(7);
				}else if (line.startsWith("Kd")){
					String[] kdColorRGB = line.substring(3).split(" ");
					kdColor = new Color(Float.parseFloat(kdColorRGB[0]), Float.parseFloat(kdColorRGB[1]), Float.parseFloat(kdColorRGB[2]));
				}else if (line.trim().startsWith("map_Kd")){
					String imageFile = line.substring(7);
					System.out.println("textureKey:" + textureKey + "imageFile:" + imageFile);
					try {
						textureImageCache.put(textureKey, generateImageIntArray(new File(imageFile)));
						textureKey = null;
					}catch (Exception e){
						//e.printStackTrace();
					}
				}
			}
		}catch (Exception e){
			//e.printStackTrace();
		}
	}

	static void add(String textureKey, File imageFile) throws Exception {
		textureImageCache.put(textureKey, generateImageIntArray(imageFile));
	}

	static int[][] generateImageIntArray(File imageFile) throws Exception{
		Image image = ImageIO.read(imageFile);
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		PixelGrabber pg = new PixelGrabber(image, 0, 0, imageWidth, imageHeight, true);
		pg.grabPixels();
		int[] localImageData = (int[])pg.getPixels();
		int[][] newImageData = new int[imageWidth][imageHeight];
		try {
			for (int x=0; x < localImageData.length; x++){
				int imageX = x % imageWidth;
				int imageY = x / imageWidth;
				try{
					newImageData[imageX][imageY] = localImageData[x];
				}catch(Exception ee){
					System.out.println("x:" + x + " imageX:" + imageX + " - imageY:" + imageY + "imageWidth:" + imageWidth + " - imageHeight:" + imageHeight + " - localImageData.length:" + localImageData.length);
				}
			}
		}catch (Exception e){
		}
		return newImageData;
	}

	public static void main(String...string){

		String file = "C:\\jason\\generic_workspace\\javatest\\model_temp\\temp\\cat.mtl";
		//String file = "C:\\jason\\generic_workspace\\javatest\\model_temp\\temp\\Snow covered CottageOBJ.mtl";
		try {
			loadMTL(new File(file));
			for (String textureKey : textureImageCache.keySet()){
				int[][] imageData = textureImageCache.get(textureKey);
				System.out.println("textureKey:" + textureKey + " - imageData:" + imageData + " - imageWidth:" + imageData.length + " - imageHeight:" + imageData[0].length);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
