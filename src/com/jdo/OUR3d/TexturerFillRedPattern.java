package com.jdo.OUR3d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;


public class TexturerFillRedPattern{
	static boolean debug;// = true;
	static boolean wireframe = true;
	private TexturerFillRedPattern(){}

	public static void fillRasterizedImage(int[][] newIntImage, List<Face> faces, Vertex[] newVertexs, Vertex[] textureCoordinates, Map<String,int[][]> textureImageCache, int middleScreenX, int middleScreenY, float[][] depth, Graphics g){
		
		for (Face f : faces){
			Vertex pointHeight = newVertexs[f.vIds[0]];
			Vertex pointCenter = newVertexs[f.vIds[1]];
			Vertex pointWidth = newVertexs[f.vIds[2]];
			if (pointHeight == null || pointCenter == null || pointWidth == null){
				continue;
			}
			Vertex[] vertices = new Vertex[]{pointHeight, pointCenter, pointWidth};
			Vertex[] sorted = sorter(vertices);
			Vertex top = sorted[0];
			Vertex mid = sorted[1];
			Vertex bottom = sorted[2];
			float slopeTopBottom = (top.x-bottom.x) / (top.y-bottom.y);
			float slopeTopMid = (top.x-mid.x) / (top.y-mid.y);
			float slopeMidBotton = (mid.x-bottom.x) / (mid.y-bottom.y);

			//TCs 
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

			double tX = Math.abs(pointWidth.x-pointCenter.x);
			double tY = Math.abs(pointWidth.y-pointCenter.y);
			double tYH = Math.abs(pointHeight.y-pointCenter.y);
			double tLength = Math.pow(tX*tX + tY*tY, .5);

			double tXUV = Math.abs(pointWidthUV.x-pointCenterUV.x);
			double tYUV = Math.abs(pointWidthUV.y-pointCenterUV.y);
			double tLengthUV = Math.pow(tXUV*tXUV + tYUV*tYUV, .5);

			float jump = (float)(tLengthUV / tLength);
			if (jump == 0){ //avoid infinite loop
				continue;
			}
			
			double mainSlopeUV = (pointWidthUV.y-pointCenterUV.y)/(pointWidthUV.x-pointCenterUV.x);
			
			
			for (float y=top.y; y >= bottom.y; y--){
				float startX = 0; 
				float endX = 0;
				if ((int)mid.y == (int)bottom.y && y == (int)bottom.y){
					//System.out.println("mid.y == bottom.y && y == Math.round(bottom.y)");
					if (mid.x < bottom.x){
						startX = mid.x;
						endX = bottom.x;
					}else{
						startX = bottom.x;
						endX = mid.x;
					}
				}else if ((int)top.y == (int)mid.y && y == top.y){
					//System.out.println("top.y == mid.y && y == top.y");
					if (top.x < mid.x){
						startX = top.x;
						endX = mid.x;
					}else{
						startX = mid.x;
						endX = top.x;
					}
				}else{
				
					if (-slopeTopBottom < -slopeTopMid){
						startX = top.x + (y-top.y) * slopeTopBottom;
						if (y > mid.y){
							endX = top.x + (y-top.y) * slopeTopMid;
						}else{
							endX = mid.x + (y-mid.y) * slopeMidBotton;
						}
					}else{
						if (y > mid.y){
							startX = top.x + (y-top.y) * slopeTopMid;
							//System.out.println("top.x:" + top.x + " - (y-top.y):" + (y-top.y) + " - slopeTopMid:" + slopeTopMid + " - top.x + (y-top.y) * slopeTopMid:" + startX);
						}else{
							startX = mid.x + (y-mid.y) * slopeMidBotton;
							//System.out.println("mid.x:" + mid.x + " - (y-mid.y):" + (y-mid.y) + " - slopeMidBotton:" + slopeMidBotton + " - mid.x + (y-mid.y) * slopeMidBotton:" + startX);
						}
						endX = top.x + (y-top.y) * slopeTopBottom;
					}
					//if (y == 0){
					//}
				}
				//System.out.println("startX:" + startX + " - endX:" + endX + " - y:" + y + " top.y:" + top.y);
				Vertex po = new Vertex(startX, y);
				Vertex pd = new Vertex(endX, y);
				int[] texels = getTexels(po, pd, imageDataUsed, jump, mainSlopeUV);
				for (float x = startX; x <= endX; x++){
					try {
						newIntImage[Math.round(x) + middleScreenX][Math.round(-y) + middleScreenY] = texels[(int)(x-startX)];//0xFFFF0000;
						//newIntImage[Math.round(x) + middleScreenX][Math.round(-y) + middleScreenY] = 0xFFFF0000;
					}catch (Exception e){}
				}
			}
			if (g != null){
				g.setColor(Color.blue);
				g.drawLine((int)pointHeight.x + middleScreenX, (int)-pointHeight.y + middleScreenY, (int)pointCenter.x + middleScreenX, (int)-pointCenter.y + middleScreenY);
				g.drawLine((int)pointCenter.x + middleScreenX, (int)-pointCenter.y + middleScreenY, (int)pointWidth.x + middleScreenX, (int)-pointWidth.y + middleScreenY);
				g.drawLine((int)pointHeight.x + middleScreenX, (int)-pointHeight.y + middleScreenY, (int)pointWidth.x + middleScreenX, (int)-pointWidth.y + middleScreenY);
			}
		}
	}

	public static Vertex[] sorter(Vertex[] vertices){
		if (vertices[1].y > vertices[0].y){
			Vertex temp = vertices[0];
			vertices[0] = vertices[1];
			vertices[1] = temp;
		}
		
		if (vertices[2].y > vertices[1].y){
			Vertex temp = vertices[1];
			vertices[1] = vertices[2];
			vertices[2] = temp;
		}
		
		if (vertices[1].y > vertices[0].y){
			Vertex temp = vertices[0];
			vertices[0] = vertices[1];
			vertices[1] = temp;
		}
		return vertices;
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
			if (po.x <= pd.x){
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
