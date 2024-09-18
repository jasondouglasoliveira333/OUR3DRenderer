package com.jdo.OUR3d;

public class Polygon{
	Face[] faces;
	String objName;
	float middleX = 0;
	float middleY = 0;
	float middleZ = 0;
	Vertex[] vertices;
	Vertex[] textureCoordinates;

	public Polygon(Face[] faces){
		this(faces, null, null, null);
	}
	public Polygon(Face[] faces, String objName){
		this(faces, objName, null, null);
	}
	public Polygon(Face[] faces, String objName, Vertex[] vertices, Vertex[] textureCoordinates){
		this.faces = faces;
		this.objName = objName;
		this.vertices = vertices;
		this.textureCoordinates = textureCoordinates;
	}
}
