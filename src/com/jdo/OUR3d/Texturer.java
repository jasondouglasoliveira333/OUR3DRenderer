package com.jdo.OUR3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class Texturer{
	static boolean debug;// = true;
	static boolean wireframe = true;
	private Texturer(){}

	public static void fillRasterizedImage(int[][] newIntImage, List<Face> faces, Vertex[] newVertexs, Vertex[] textureCoordinates, Map<String,int[][]> textureImageCache, int middleScreenX, int middleScreenY, float[][] depth){
		
		for (Face f : faces){
			try {
				Vertex pointHeight = newVertexs[f.vIds[0]];
				Vertex pointCenter = newVertexs[f.vIds[1]];
				Vertex pointWidth = newVertexs[f.vIds[2]];

				Vertex pointHeightUVOrig = textureCoordinates[f.tcIds[0]];
				Vertex pointCenterUVOrig = textureCoordinates[f.tcIds[1]];
				Vertex pointWidthUVOrig = textureCoordinates[f.tcIds[2]];

				int[][] imageDataUsed = null;
				if (textureImageCache.containsKey(f.textureId)){
					imageDataUsed = textureImageCache.get(f.textureId);
				}else{
					imageDataUsed = textureImageCache.get("OURDefaultImage");
				}
				int imageWidth = imageDataUsed.length;
				int imageHeight = imageDataUsed[0].length;

				//Transform to real triangle tc
				float pointHeightUVx = pointHeightUVOrig.x * imageWidth;
				float pointHeightUVy = pointHeightUVOrig.y * imageHeight;
				float pointCenterUVx = pointCenterUVOrig.x * imageWidth;
				float pointCenterUVy = pointCenterUVOrig.y * imageHeight;
				float pointWidthUVx = pointWidthUVOrig.x * imageWidth;
				float pointWidthUVy = pointWidthUVOrig.y * imageHeight;
				if (pointHeightUVx != 0){
					pointHeightUVx--;
				}
				if (pointHeightUVy != 0){
					pointHeightUVy--;
				}
				if (pointCenterUVx != 0){
					pointCenterUVx--;
				}
				if (pointCenterUVy != 0){
					pointCenterUVy--;
				}
				if (pointWidthUVy != 0){
					pointWidthUVy--;
				}
				if (pointWidthUVx != 0){
					pointWidthUVx--;
				}

				Vertex pointHeightUV = new Vertex(pointHeightUVx, pointHeightUVy);
				Vertex pointCenterUV = new Vertex(pointCenterUVx, pointCenterUVy);
				Vertex pointWidthUV = new Vertex(pointWidthUVx, pointWidthUVy);

				double tX = Math.abs(pointHeight.x-pointCenter.x);
				double tY = Math.abs(pointHeight.y-pointCenter.y);
				double tLength = Math.pow(tX*tX + tY*tY, .5);

				double tXUV = Math.abs(pointHeightUV.x-pointCenterUV.x);
				double tYUV = Math.abs(pointHeightUV.y-pointCenterUV.y);
				double tLengthUV = Math.pow(tXUV*tXUV + tYUV*tYUV, .5);

				float jump = (float)(tLengthUV / tLength);
				if (jump == 0){ //avoid infinite loop
					continue;
				}

				List<Vertex> origins = generateLineCoordinates(pointCenter, pointWidth, true);
				List<Vertex> destinations = generateLineCoordinates(pointHeight, pointWidth, true);

				List<Vertex> originsUV = generateLineCoordinates(pointCenterUV, pointWidthUV, false);
				List<Vertex> destinationsUV = generateLineCoordinates(pointHeightUV, pointWidthUV, false);

				double mainSlope = (pointHeight.y-pointCenter.y)/(pointHeight.x-pointCenter.x);
				double mainSlopeUV = (pointHeightUV.y-pointCenterUV.y)/(pointHeightUV.x-pointCenterUV.x);

				/*g.drawLine((int)(pointCenterUV.x + middleScreenX + 100), (int)(-pointCenterUV.y + middleScreenY + 100), (int)(pointHeightUV.x + middleScreenX + 100), (int)(-pointHeightUV.y + middleScreenY + 100));
				g.drawLine((int)(pointHeightUV.x + middleScreenX + 100), (int)(-pointHeightUV.y + middleScreenY + 100), (int)(pointWidthUV.x + middleScreenX + 100), (int)(-pointWidthUV.y + middleScreenY + 100));
				g.drawLine((int)(pointWidthUV.x + middleScreenX + 100), (int)(-pointWidthUV.y + middleScreenY + 100), (int)(pointCenterUV.x + middleScreenX + 100), (int)(-pointCenterUV.y + middleScreenY + 100));
				//g.drawLine((int)(pointWidthUVAux.x * 100 + middleScreenX + 100), (int)(-pointWidthUVAux.y * 100 + middleScreenY + 100), (int)(pointCenterUV.x * 100 + middleScreenX + 100), (int)(-pointCenterUV.y * 100 + middleScreenY + 100));
				g.drawString("JC",(int)(pointCenterUV.x + middleScreenX + 100), (int)(-pointCenterUV.y + middleScreenY + 100));
				g.drawString("JH", (int)(pointHeightUV.x + middleScreenX + 100), (int)(-pointHeightUV.y + middleScreenY + 100));
				g.drawString("JW", (int)(pointWidthUV.x + middleScreenX + 100), (int)(-pointWidthUV.y + middleScreenY + 100));
				g.drawString("DC", (int)(.36 * 100 + middleScreenX + 100), (int)(-.48 * 100 + middleScreenY + 100));
				*/
				/*System.out.println("origins.size():" + origins.size() + " - destinations.size():" + destinations.size());
				System.out.println("originsUV.size():" + originsUV.size() + " - destinationsUV.size():" + destinationsUV.size());
				*/
				double lineSlope = 0;
				double factorOD = (float)destinations.size() / origins.size();
				double factorODUV = (float)destinationsUV.size() / originsUV.size();
				double factorOO = (float)originsUV.size() / origins.size();

				for (int o=0; o < origins.size(); o++){
					int od = (int)(factorOD * o);
					Vertex po = origins.get(o);
					if (od >= destinations.size()){
						//System.out.println("od >= destinations.size()");
						break;
					}
					Vertex pd =  destinations.get(od);
					int oUV = (int)(o * factorOO);
					int odUV = (int)(factorODUV * oUV);
					if (oUV >= originsUV.size() || odUV >= destinationsUV.size()){
						//System.out.println("oUV >= originsUV.size() || odUV >= destinationsUV.size()");
						break;
					}
					Vertex poUV = originsUV.get(oUV);
					Vertex pdUV =  destinationsUV.get(odUV);
					double vX = Math.abs(pd.x-po.x);
					double vY = Math.abs(pd.y-po.y);
					double vLength = Math.pow(vX*vX + vY*vY, .5);
					//System.out.println("jump:" + jump + " - vLength:" + vLength + " - vLengthUV:" + vLengthUV);

					int[] texels = getTexels(poUV, pdUV, imageDataUsed, jump, mainSlopeUV);
					int texelIndex = 0;
					
					//Calculus to z buffering
					//double maxZ = Math.max(po.z, pd.z);
					//double minZ = Math.min(po.z, pd.z);
					double deltaZ = pd.z - po.z;
					
					if (Math.abs(mainSlope) >= 1){
						lineSlope = (pd.x-po.x)/(pd.y-po.y);
						float increment = (float)(vY/vLength);
						
						if (increment == 0){
							continue;
						}
						
						double interationSize = Math.abs(pd.y - po.y) * increment + 1;
						double incrementZ = deltaZ / interationSize;

						if ((int)po.y < (int)pd.y){
							for (double y=po.y; y <= pd.y + increment; ){
								float newZ = (float)(po.z + incrementZ * texelIndex);
								int imageX = (int)(po.x + (y-po.y) * lineSlope);
								int imageY = (int)(y);

								try {
									if (newZ > depth[imageX + middleScreenX][-imageY + middleScreenY]){
										newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
										//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
										depth[imageX + middleScreenX][-imageY + middleScreenY] = newZ;
									}
								}catch (Exception e){}
								
								y+=increment;
								texelIndex++;
							}
						}else {
							for (double y=po.y; y >= pd.y - increment; ){
								float newZ = (float)(po.z + incrementZ * texelIndex);
								int imageX = (int)(po.x + (y-po.y) * lineSlope);
								int imageY = (int)(y);

								try {
									if (newZ > depth[imageX + middleScreenX][-imageY + middleScreenY]){
										newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
										//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
										depth[imageX + middleScreenX][-imageY + middleScreenY] = newZ;
									}
								}catch (Exception e){}
								
								y-=increment;
								texelIndex++;
							}
						}
					}else{
						lineSlope = (pd.y-po.y)/(pd.x-po.x);
						float increment = (float)(vX/vLength);

						if (increment == 0){
							continue;
						}
						
						double interationSize = Math.abs(pd.x - po.x) * increment + 1;
						double incrementZ = deltaZ / interationSize;
						
						if (po.x < pd.x){
							for (double x=po.x; x <= pd.x + increment; ){
								float newZ = (float)(po.z + incrementZ * texelIndex);
								int imageX = (int)(x);
								int imageY = (int)(po.y  + (x-po.x) * lineSlope);

								try {
									if (newZ > depth[imageX + middleScreenX][-imageY + middleScreenY]){
										newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
										//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
										depth[imageX + middleScreenX][-imageY + middleScreenY] = newZ;
									}
								}catch (Exception e){}
								
								x+=increment;
								texelIndex++;
							}
						}else{
							for (double x=po.x; x >= pd.x - increment; ){
								float newZ = (float)(po.z + incrementZ * texelIndex);
								int imageX = (int)(x);
								int imageY = (int)(po.y  + (x-po.x) * lineSlope);

								try {
									if (newZ > depth[imageX + middleScreenX][-imageY + middleScreenY]){
										newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
										//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelIndex];//0xFFFF0000;
										depth[imageX + middleScreenX][-imageY + middleScreenY] = newZ;
									}
								}catch (Exception e){}
								
								x-=increment;
								texelIndex++;
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
				texels = new int[(int)((pd.y + increment - po.y) / increment)];
				for (double y=po.y; y <= pd.y + increment; ){
					int imageX = (int)(po.x + (y-po.y) * lineSlope);
					int imageY = (int)(y);
					try{
						texels[texelCount++] = imageDataUsed[imageX][imageHeight-1-imageY];
					}catch (Exception e){}
					y+=increment;
				}
			}else {
				texels = new int[(int)((po.y + increment - pd.y) / increment)];
				for (double y=po.y; y >= pd.y - increment; ){
					int imageX = (int)(po.x + (y-po.y) * lineSlope);
					int imageY = (int)(y);
					try{
						texels[texelCount++] = imageDataUsed[imageX][imageHeight-1-imageY];
					}catch (Exception e){}
					y-=increment;
				}
			}
		}else{
			lineSlope = (pd.y-po.y)/(pd.x-po.x);
			if (po.x < pd.x){
				texels = new int[(int)((pd.x + increment - po.x) / increment)];
				for (double x=po.x; x <= pd.x + increment; ){
					int imageX = (int)(x);
					int imageY = (int)(po.y  + (x-po.x) * lineSlope);
					try{
						texels[texelCount++] = imageDataUsed[imageX][imageHeight-1-imageY];
					}catch (Exception e){}
					x+=increment;
				}
			}else{
				texels = new int[(int)((po.x + increment - pd.x) / increment)];
				for (double x=po.x; x >= pd.x - increment; ){
					int imageX = (int)(x);
					int imageY = (int)(po.y  + (x-po.x) * lineSlope);
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
