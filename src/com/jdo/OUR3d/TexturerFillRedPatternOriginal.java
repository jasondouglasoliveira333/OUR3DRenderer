package com.jdo.OUR3d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;


public class TexturerFillRedPatternOriginal{
	static boolean debug;// = true;
	static boolean wireframe = true;
	private TexturerFillRedPatternOriginal(){}

	public static void fillRasterizedImage(int[][] newIntImage, List<Face> faces, Vertex[] newVertexs, Vertex[] textureCoordinates, Map<String,int[][]> textureImageCache, int middleScreenX, int middleScreenY, float[][] depth, Graphics g){
		
		for (Face f : faces){
			Vertex first = newVertexs[f.vIds[0]];
			Vertex second = newVertexs[f.vIds[1]];
			Vertex third = newVertexs[f.vIds[2]];
			if (first == null || second == null || third == null){
				continue;
			}
			Vertex[] vertices = new Vertex[]{first, second,third};
			Vertex[] sorted = sorter(vertices);
			Vertex top = sorted[0];
			Vertex mid = sorted[1];
			Vertex bottom = sorted[2];
			float slopeTopBottom = (top.x-bottom.x) / (top.y-bottom.y);
			float slopeTopMid = (top.x-mid.x) / (top.y-mid.y);
			float slopeMidBotton = (mid.x-bottom.x) / (mid.y-bottom.y);
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
				for (float x = startX; x <= endX; x++){
					try {
						newIntImage[Math.round(x) + middleScreenX][Math.round(-y) + middleScreenY] = 0xFFFF0000;
					}catch (Exception e){}
				}
			}
			if (g != null){
				g.setColor(Color.blue);
				g.drawLine((int)first.x + middleScreenX, (int)-first.y + middleScreenY, (int)second.x + middleScreenX, (int)-second.y + middleScreenY);
				g.drawLine((int)second.x + middleScreenX, (int)-second.y + middleScreenY, (int)third.x + middleScreenX, (int)-third.y + middleScreenY);
				g.drawLine((int)first.x + middleScreenX, (int)-first.y + middleScreenY, (int)third.x + middleScreenX, (int)-third.y + middleScreenY);
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

}
