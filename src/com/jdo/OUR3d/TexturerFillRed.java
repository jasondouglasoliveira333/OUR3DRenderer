package com.jdo.OUR3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class TexturerFillRed{
	static boolean debug;// = true;
	static boolean wireframe = true;
	private TexturerFillRed(){}

	public static void fillRasterizedImage(int[][] newIntImage, List<Face> faces, Vertex[] newVertexs, Vertex[] textureCoordinates, Map<String,int[][]> textureImageCache, int middleScreenX, int middleScreenY, float[][] depth){
		
		for (Face f : faces){
			try {
				Vertex pointHeight = newVertexs[f.vIds[0]];
				Vertex pointCenter = newVertexs[f.vIds[1]];
				Vertex pointWidth = newVertexs[f.vIds[2]];

				List<Vertex> origins = generateLineCoordinates(pointCenter, pointWidth, false);
				List<Vertex> destinations = generateLineCoordinates(pointHeight, pointWidth, false);

				double mainSlope = (pointHeight.y-pointCenter.y)/(pointHeight.x-pointCenter.x);

				double lineSlope = 0;
				double factorOD = (float)destinations.size() / origins.size();

				for (int o=0; o < origins.size(); o++){
					int od = (int)(factorOD * o);
					Vertex po = origins.get(o);
					if (od >= destinations.size()){
						break;
					}
					Vertex pd =  destinations.get(od);
					double vX = Math.abs(pd.x-po.x);
					double vY = Math.abs(pd.y-po.y);
					double vLength = Math.pow(vX*vX + vY*vY, .5);
					//System.out.println("jump:" + jump + " - vLength:" + vLength + " - vLengthUV:" + vLengthUV);

					if (Math.abs(mainSlope) >= 1){
						lineSlope = (pd.x-po.x)/(pd.y-po.y);
						float increment = (float)(vY/vLength);
						if (increment == 0){
							continue;
						}
						if ((int)po.y < (int)pd.y){
							for (double y=po.y; y <= pd.y + increment; ){
								int imageX = (int)(po.x + (y-po.y) * lineSlope);
								int imageY = (int)(y);
								try {
									newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
									//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
								}catch (Exception e){}
								y+=increment;
							}
						}else {
							for (double y=po.y; y >= pd.y - increment; ){
								int imageX = (int)(po.x + (y-po.y) * lineSlope);
								int imageY = (int)(y);
								try {
									newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
								}catch (Exception e){}
								y-=increment;
							}
						}
					}else{
						lineSlope = (pd.y-po.y)/(pd.x-po.x);
						float increment = (float)(vX/vLength);
						if (increment == 0){
							continue;
						}
						if (po.x < pd.x){
							for (double x=po.x; x <= pd.x + increment; ){
								int imageX = (int)(x);
								int imageY = (int)(po.y  + (x-po.x) * lineSlope);
								
								try {
									newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
								}catch (Exception e){}
								
								x+=increment;
							}
						}else{
							for (double x=po.x; x >= pd.x - increment; ){
								int imageX = (int)(x);
								int imageY = (int)(po.y  + (x-po.x) * lineSlope);
								try {
									newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
								}catch (Exception e){}
								x-=increment;
							}
						}
					}
				}
				//debug = false;
			}catch (Exception e){}
		}

	}

	private static List<Vertex> generateLineCoordinates(Vertex a, Vertex b, boolean geometricVertexes){
		List<Vertex> scanCoords = new ArrayList<>();
		Vertex lesser = null;
		Vertex greater = null;
		boolean invertCoords = false;
		float slope = (b.y-a.y)/(b.x-a.x);

		double vX = Math.abs(a.x-b.x);
		double vY = Math.abs(a.y-b.y);
		double vLength = Math.pow(vX*vX + vY*vY, .5);
		
		//double maxZ = Math.max(a.z, b.z);
		//double minZ = Math.min(a.z, b.z);
		//double deltaZ = maxZ - minZ;
		if (a.x != b.x){
			if (a.x <= b.x){
				lesser = a;
				greater = b;
			}else{
				lesser = b;
				greater = a;
				invertCoords = true;
			}
			double incrementX = vX/vLength/2;
			for (double x=lesser.x; x < greater.x;){
				float y = (float)(lesser.y + (x-lesser.x) * slope);
				scanCoords.add(new Vertex((float)x, y));
				x+=incrementX;
			}
		}else{ //WE must do this in y
			//System.out.println("In a.x and b.x equals a.y:" + a.y + " - b.y:" + b.y);
			slope = (b.x-a.x)/(b.y-a.y);
			if (a.y < b.y){
				lesser = a;
				greater = b;
			}else{
				lesser = b;
				greater = a;
				invertCoords = true;
			}
			double incrementY = vY/vLength/2;
			for (double y=lesser.y; y < greater.y;){
				float x = (float)(lesser.x + (y-lesser.y) * slope);
				scanCoords.add(new Vertex(x, (float)y));
				y+=incrementY;
			}
		}
		
		if (geometricVertexes){
			//calculating z for Z buffering 
			double deltaZ = greater.z - lesser.z;
			double deltaZU = deltaZ / scanCoords.size();
			for (int x=0; x < scanCoords.size(); x++){
				float newZ = (float)(lesser.z + deltaZU * x);
				if (debug){
					//System.out.println("minZ:" + minZ + " - maxZ:" + maxZ + " - newZ:" + newZ + "deltaZU:" + deltaZU + " - size:" + scanCoords.size());
				}
				scanCoords.get(x).z = newZ;
			}
		}
		if (invertCoords){
			Collections.reverse(scanCoords);
		}
		return scanCoords;
	}

	private static int[] getTexels(Vertex po, Vertex pd, int[][] imageDataUsed, float increment, double mainSlopeUV){
		int[] texels = null;
		int texelCount = 0;
		double lineSlope = 0;

		int imageHeight = imageDataUsed[0].length;
		if (Math.abs(mainSlopeUV) >= 1){
			lineSlope = (pd.x-po.x)/(pd.y-po.y);
			if ((int)po.y < (int)pd.y){
				texels = new int[(int)Math.round((pd.y + increment - po.y) / increment)];
				for (double y=po.y; y <= pd.y + increment; ){
					int imageX = (int)Math.round(po.x + (y-po.y) * lineSlope);
					int imageY = (int)Math.round(y);
					try{
						texels[texelCount++] = imageDataUsed[imageX][imageHeight-1-imageY];
					}catch (Exception e){}
					y+=increment;
				}
			}else {
				texels = new int[(int)Math.round((po.y + increment - pd.y) / increment)];
				for (double y=po.y; y >= pd.y - increment; ){
					int imageX = (int)Math.round(po.x + (y-po.y) * lineSlope);
					int imageY = (int)Math.round(y);
					try{
						texels[texelCount++] = imageDataUsed[imageX][imageHeight-1-imageY];
					}catch (Exception e){}
					y-=increment;
				}
			}
		}else{
			lineSlope = (pd.y-po.y)/(pd.x-po.x);
			if (po.x < pd.x){
				texels = new int[(int)Math.round((pd.x + increment - po.x) / increment)];
				for (double x=po.x; x <= pd.x + increment; ){
					int imageX = (int)Math.round(x);
					int imageY = (int)Math.round(po.y  + (x-po.x) * lineSlope);
					try{
						texels[texelCount++] = imageDataUsed[imageX][imageHeight-1-imageY];
					}catch (Exception e){}
					x+=increment;
				}
			}else{
				texels = new int[(int)Math.round((po.x + increment - pd.x) / increment)];
				for (double x=po.x; x >= pd.x - increment; ){
					int imageX = (int)Math.round(x);
					int imageY = (int)Math.round(po.y  + (x-po.x) * lineSlope);
					try{
						texels[texelCount++] = imageDataUsed[imageX][imageHeight-1-imageY];
					}catch (Exception e){}
					x-=increment;
				}
			}
		}
		return texels;
	}

}
