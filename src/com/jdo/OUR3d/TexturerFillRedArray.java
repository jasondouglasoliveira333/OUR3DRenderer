package com.jdo.OUR3d;

import java.util.List;
import java.util.Map;


public class TexturerFillRedArray{
	static boolean debug;// = true;
	static boolean wireframe = true;
	static long time;
	private TexturerFillRedArray(){}

	public static void fillRasterizedImage(int[][] newIntImage, List<Face> faces, Vertex[] newVertexs, Vertex[] textureCoordinates, Map<String,int[][]> textureImageCache, int middleScreenX, int middleScreenY, float[][] depth){
		time = 0;
		for (Face f : faces){
			try {
				Vertex pointHeight = newVertexs[f.vIds[0]];
				Vertex pointCenter = newVertexs[f.vIds[1]];
				Vertex pointWidth = newVertexs[f.vIds[2]];

				double mainSlope = (pointHeight.y-pointCenter.y)/(pointHeight.x-pointCenter.x);

				double vX = Math.abs(pointHeight.x-pointCenter.x);
				double vY = Math.abs(pointHeight.y-pointCenter.y);
				double vLength = Math.sqrt(vX*vX + vY*vY);
				
				double lineSlopeY = (pointHeight.x-pointCenter.x)/(pointHeight.y-pointCenter.y);
				float incrementY = (float)(vY/vLength);

				double lineSlopeX = (pointHeight.y-pointCenter.y)/(pointHeight.x-pointCenter.x);
				float incrementX = (float)(vX/vLength);

				if ((Math.abs(mainSlope) >= 1 && incrementY == 0) || 
						(Math.abs(mainSlope) < 1  && incrementX == 0)){
					continue;
				}

				Vertex[] origins = generateLineCoordinates(pointCenter, pointWidth, false);
				Vertex[] destinations = generateLineCoordinates(pointHeight, pointWidth, false);

				double factorOD = (float)destinations.length / origins.length;

				
				for (int o=0; o < origins.length; o++){
					int od = (int)(factorOD * o);
					Vertex po = origins[o];
					if (od >= destinations.length){
						break;
					}
					Vertex pd =  destinations[od];


					if (Math.abs(mainSlope) >= 1){
						if (po.y < pd.y){
							float counterLimit = pd.y + incrementY;
							for (float y=po.y; y <= counterLimit; ){
								int imageX = (int)(po.x + (y-po.y) * lineSlopeY);
								int imageY = (int)(y);
								try {
									newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
									//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
								}catch (Exception e){}
								y+=incrementY;
							}
						}else {
							float counterLimit = pd.y - incrementY;
							for (float y=po.y; y >= counterLimit; ){
								int imageX = (int)(po.x + (y-po.y) * lineSlopeY);
								int imageY = (int)(y);
								try {
									newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
								}catch (Exception e){}
								y-=incrementY;
							}
						}
					}else{
						if (po.x < pd.x){
							float counterLimit = pd.x + incrementX;
							for (float x=po.x; x <= counterLimit; ){
								int imageX = (int)(x);
								int imageY = (int)(po.y  + (x-po.x) * lineSlopeX);
								
								try {
									newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
								}catch (Exception e){}
								
								x+=incrementX;
							}
						}else{
							float counterLimit = pd.x - incrementX;
							for (float x=po.x; x >= counterLimit; ){
								int imageX = (int)(x);
								int imageY = (int)(po.y  + (x-po.x) * lineSlopeX);
								try {
									newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
								}catch (Exception e){}
								x-=incrementX;
							}
						}
					}
				}
				//debug = false;
			}catch (Exception e){}
		}
		System.out.println("time:" + time);
	}

	private static Vertex[] generateLineCoordinates(Vertex lesser, Vertex greater, boolean geometricVertexes){
		Vertex[] scanCoords = null;
		/*Vertex lesser = null;
		Vertex greater = null;*/
		boolean invertCoords = false;
		float slope = 0;

		double vX = Math.abs(lesser.x-greater.x);
		double vY = Math.abs(lesser.y-greater.y);
		double vLength = Math.sqrt(vX*vX + vY*vY);
		//double vLength = (vX*vX + vY*vY) / 2;
		
		//double maxZ = Math.max(a.z, b.z);
		//double minZ = Math.min(a.z, b.z);
		//double deltaZ = maxZ - minZ;
		if (lesser.x != greater.x){
			slope = (greater.y-lesser.y)/(greater.x-lesser.x);
			if (lesser.x > greater.x){
				Vertex temp = lesser;
				lesser = greater;
				greater = temp;
				invertCoords = true;
			}
			double incrementX = vX/vLength/2;
			scanCoords = new Vertex[(int)((greater.x-lesser.x)/incrementX)];
			int counter=0;
			for (double x=lesser.x; x < greater.x;){
				float y = (float)(lesser.y + (x-lesser.x) * slope);
				try {
					scanCoords[counter++] = new Vertex((float)x, y);
				}catch (Exception e){}
				x+=incrementX;
			}
		}else{ //WE must do this in y
			//System.out.println("In a.x and b.x equals a.y:" + a.y + " - b.y:" + b.y);
			slope = (greater.x-lesser.x)/(greater.y-lesser.y);
			if (lesser.y > greater.y){
				Vertex temp = lesser;
				lesser = greater;
				greater = temp;
				invertCoords = true;
			}
			double incrementY = vY/vLength/2;
			scanCoords = new Vertex[(int)((greater.y-lesser.y)/incrementY)];
			int counter=0;
			for (double y=lesser.y; y < greater.y;){
				float x = (float)(lesser.x + (y-lesser.y) * slope);
				try {
					scanCoords[counter++] = new Vertex(x, (float)y);
				}catch (Exception e){}
				y+=incrementY;
			}
		}
		
		if (invertCoords){
			reverse(scanCoords);
		}
		return scanCoords;
	}

	private static void reverse(Object[] scanCoords) {
		int mid = scanCoords.length / 2;
		for (int x=0; x < mid; x++){
			Object temp = scanCoords[x];
			scanCoords[x] = scanCoords[scanCoords.length-1-x];
			scanCoords[scanCoords.length-1-x] = temp;
		}
	}

}
