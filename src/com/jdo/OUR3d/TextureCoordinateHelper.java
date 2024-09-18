package com.jdo.OUR3d;

import java.util.Arrays;
import java.util.List;

public class TextureCoordinateHelper {

	public static void main(String...string){
		//System.out.println("turnPositiveAngle(treatAngle(350, 20):" + turnPositiveAngle(treatAngle(20, 350)));
		/*
		Point2 pointHeightUV = new Point2(.71f,.51f);
		Point2 pointCenterUV = new Point2(.13f,.72f);
		Point2 pointWidthUV = new Point2(.23f,.15f);
		*/
		Vertex pointHeightUV = new Vertex(.71f,.51f);
		Vertex pointCenterUV = new Vertex(.13f,.72f);
		Vertex pointWidthUV = new Vertex(.23f,.15f);

		List<Vertex> tcs = Arrays.asList(pointHeightUV, pointCenterUV, pointWidthUV);
		int[] vIds = new int[]{0,1,2};
		int[] tcIds = new int[]{0,1,2};
		Face f = new Face(vIds, tcIds, 0);
		adjustFaceByTC(f, tcs);

		/*
		Point2 pointHeightUV = new Point2(0,1);
		Point2 pointCenterUV = new Point2(0,0);
		Point2 pointWidthUV = new Point2(1,0);
		*/

		//Some problems :D
		/*Point2 pointHeightUV = new Point2(0,0);
		Point2 pointCenterUV = new Point2(1,0);
		Point2 pointWidthUV = new Point2(1,1);
		*/

		/*
		Point2 pointHeightUV = new Point2(1,0);
		Point2 pointCenterUV = new Point2(0,1);
		Point2 pointWidthUV = new Point2(0,0);
		*/

	}

	static void adjustFaceByTC(Face f, List<Vertex> tcs){
		Vertex pointHeightUV = tcs.get(f.tcIds[0]);
		Vertex pointCenterUV = tcs.get(f.tcIds[1]);
		Vertex pointWidthUV = tcs.get(f.tcIds[2]);

		double angleCenterHeight = turnPositiveAngle(Math.toDegrees(Math.atan2(pointHeightUV.y-pointCenterUV.y, pointHeightUV.x-pointCenterUV.x)));
		double angleCenterWidth = turnPositiveAngle(Math.toDegrees(Math.atan2(pointWidthUV.y-pointCenterUV.y, pointWidthUV.x-pointCenterUV.x)));
		double angleHeightCenter = turnPositiveAngle(Math.toDegrees(Math.atan2(pointCenterUV.y-pointHeightUV.y, pointCenterUV.x-pointHeightUV.x)));
		double angleHeightWidth = turnPositiveAngle(Math.toDegrees(Math.atan2(pointWidthUV.y-pointHeightUV.y, pointWidthUV.x-pointHeightUV.x)));
		double angleWidthCenter = turnPositiveAngle(Math.toDegrees(Math.atan2(pointCenterUV.y-pointWidthUV.y, pointCenterUV.x-pointWidthUV.x)));
		double angleWidthHeight = turnPositiveAngle(Math.toDegrees(Math.atan2(pointHeightUV.y-pointWidthUV.y, pointHeightUV.x-pointWidthUV.x)));

		//System.out.println("pointCenterUV.y-pointWidthUV.y:" + (pointCenterUV.y-pointWidthUV.y) + " - pointCenterUV.x-pointWidthUV.x:" + (pointCenterUV.x-pointWidthUV.x));
		double angleCenter = turnPositiveAngle(treatAngle(angleCenterHeight,angleCenterWidth));
		double angleHeight = turnPositiveAngle(treatAngle(angleHeightWidth,angleHeightCenter));
		double angleWidth = turnPositiveAngle(treatAngle(angleWidthCenter,angleWidthHeight));

		if (angleHeight > angleCenter && angleHeight >= angleWidth){
			//System.out.println("Height is greater:" + angleHeight);
			/*Point2 temp = pointHeightUV;
			pointHeightUV = pointWidthUV;
			pointWidthUV = pointCenterUV;
			pointCenterUV = temp;
			*/
			int temp = f.tcIds[0];
			f.tcIds[0] = f.tcIds[2];
			f.tcIds[2] = f.tcIds[1];
			f.tcIds[1] = temp;

			temp = f.vIds[0];
			f.vIds[0] = f.vIds[2];
			f.vIds[2] = f.vIds[1];
			f.vIds[1] = temp;
		}else if (angleWidth > angleCenter && angleWidth >= angleHeight){
			//System.out.println("Width is greater:" + angleWidth);
			/*
			Point2 temp = pointWidthUV;
			pointWidthUV = pointHeightUV;
			pointHeightUV = pointCenterUV;
			pointCenterUV = temp;
			*/
			int temp = f.tcIds[2];
			f.tcIds[2] = f.tcIds[0];
			f.tcIds[0] = f.tcIds[1];
			f.tcIds[1] = temp;

			temp = f.vIds[2];
			f.vIds[2] = f.vIds[0];
			f.vIds[0] = f.vIds[1];
			f.vIds[1] = temp;
		}

		pointHeightUV = tcs.get(f.tcIds[0]);
		pointCenterUV = tcs.get(f.tcIds[1]);
		pointWidthUV = tcs.get(f.tcIds[2]);

		//To avoid give zero in division
		if (Math.abs(pointHeightUV.y-pointCenterUV.y) == 0){
			pointCenterUV.y += .0000033f;
		}
		if (Math.abs(pointWidthUV.x-pointCenterUV.x) == 0){
			pointCenterUV.x += .0000033f;
		}
		//System.out.println("pointCenterUV(x,y):(" + pointCenterUV.x + "," + pointCenterUV.y + "): - vIdx:" + f.vIds[0] +  " - pointHeightUV(x,y):(" + pointHeightUV.x + "," + pointHeightUV.y + "): - vIdx:" + f.vIds[1] +  " - pointWidthUV(x,y):(" + pointWidthUV.x + "," + pointWidthUV.y + "): - vIdx:" + f.vIds[2]);
	}

	public static double turnPositiveAngle(double angle){
		if (angle < 0){
			return 360+angle;
		}else{
			return angle;
		}

	}

	public static double treatAngle(double a, double b){
		//System.out.println("a:" + a + " - b:" + b);
		if (a >= 270 && b <= 90){
			double increase = 360 - a;
			return b + increase;
		}else if (b >= 270 && a <= 90){
			double increase = 360 - b;
			return a + increase;
		}else if (b > a){
			return b - a;
		}else{
			return a - b;
		}
	}
}