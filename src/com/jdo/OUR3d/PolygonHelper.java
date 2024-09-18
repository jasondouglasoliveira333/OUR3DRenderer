package com.jdo.OUR3d;

import java.util.ArrayList;
import java.util.List;

public class PolygonHelper {

	public static List<Polygon> split(Polygon p) {
		List<Polygon> ps = new ArrayList<>();
		int facesByPolygon = p.faces.length / 4; 
		for (int x = 0; x < 3; x++){
			Face[] newFaces = new Face[facesByPolygon];
			System.arraycopy(p.faces, facesByPolygon * x, newFaces, 0, facesByPolygon);
			Polygon newP = new Polygon(newFaces, p.objName, p.vertices, p.textureCoordinates);
			ps.add(newP);
		}
		int facesByPolygonLast = facesByPolygon + p.faces.length % 4;
		/*System.out.println("p.faces.length:" + p.faces.length + 
				" - facesByPolygon:" + facesByPolygon + " - facesByPolygonLast:" + facesByPolygonLast
				+ " - facesByPolygon * 3:" + (facesByPolygon * 3));
		*/
		Face[] newFaces = new Face[facesByPolygonLast];
		System.arraycopy(p.faces, facesByPolygon * 3, newFaces, 0, facesByPolygonLast);
		Polygon newP = new Polygon(newFaces, p.objName, p.vertices, p.textureCoordinates);
		ps.add(newP);
		return ps;
	}

}
