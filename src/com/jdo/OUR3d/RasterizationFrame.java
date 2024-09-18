package com.jdo.OUR3d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class RasterizationFrame extends JFrame{
	private static final long serialVersionUID = 6980218134254639406L;

	public static void main(String...strings){
		Vertex v1 = new Vertex(3,-1.3f);
		float slope = v1.x / v1.y;
		System.out.println("slope:" + slope);

		Vertex v2 = new Vertex(3,-3f);
		float slope2 = v2.x / v2.y;
		System.out.println("slope3:" + slope2);

		Vertex v3 = new Vertex(3,-4.3f);
		float slope3 = v3.x / v3.y;
		System.out.println("slope3:" + slope3);

		List<Vertex> vs = new ArrayList<>(); 
		vs.add(new Vertex(0, 3));
		vs.add(new Vertex(0, 33));
		vs.add(new Vertex(0, 13));
		
		Vertex[] vsArray = sorter(vs.toArray(new Vertex[3]));
		
		for (Vertex v : vsArray){
			System.out.println("v.y:" + v.y);
		}
		/*
		RasterizationFrame rf  = new RasterizationFrame();
		rf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		rf.setSize(800, 600);
		rf.setVisible(true);
		*/
	}

	public void paint(Graphics g){
		g.translate(13, 33);
		int middleX = 800/2;
		int middleY = 600/2;
		Vertex top = new Vertex(33, 33);
		Vertex mid = new Vertex(3, 23);
		//Vertex mid = new Vertex(53, 23);
		Vertex bottom = new Vertex(31, 3); //3
		float slopeTopBottom = (top.x-bottom.x) / (top.y-bottom.y);
		float slopeTopMid = (top.x-mid.x) / (top.y-mid.y);
		float slopeMidBotton = (mid.x-bottom.x) / (mid.y-bottom.y);
		for (float y=top.y; y > bottom.y; y--){
			int startX = 0; 
			int endX = 0;
			if (bottom.x < mid.x){
				startX = (int)(top.x + (y-top.y) * slopeTopBottom);
				if (y > mid.y){
					endX = (int)(top.x + (y-top.y) * slopeTopMid);
				}else{
					endX = (int)(mid.x + (y-mid.y) * slopeMidBotton);
				}
			}else{
				if (y > mid.y){
					startX = (int)(top.x + (y-top.y) * slopeTopMid);
				}else{
					startX = (int)(mid.x + (y-mid.y) * slopeMidBotton);
				}
				endX = (int)(top.x + (y-top.y) * slopeTopBottom);
			}
			for (int x = startX; x <= endX; x++){
				g.drawRect(x, (int)y, 1, 1);
			}
		}
		
		g.setColor(Color.red);
		g.drawLine((int)top.x, (int)top.y, (int)mid.x, (int)mid.y);
		g.drawLine((int)top.x, (int)top.y, (int)bottom.x, (int)bottom.y);
		g.drawLine((int)mid.x, (int)mid.y, (int)bottom.x, (int)bottom.y);
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
