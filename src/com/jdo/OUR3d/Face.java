package com.jdo.OUR3d;

public class Face {
	int[] vIds;
	int[] tcIds;
	String textureId;
	float maxZ = 0;
	public Face(){}
	public Face(int[] vIds){
		this.vIds = vIds;
	}
	public Face(int[] vIds, int[] tcIds, int textureIdInt){
		this.vIds = vIds;
		this.tcIds = tcIds;
		//this.textureId = textureId;
	}

	public Face(int[] vIds, int[] tcIds, String textureId){
		this.vIds = vIds;
		this.tcIds = tcIds;
		this.textureId = textureId;
	}
}
