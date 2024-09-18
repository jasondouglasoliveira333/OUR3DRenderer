package com.jdo.OUR3d;

public class Vertex{
	float x;
	float y;
	float z;
	Vertex(float x, float y){
		this(x, y, 0);
	}
	Vertex(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vertex() {
	}
}

