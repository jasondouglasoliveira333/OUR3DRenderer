package com.jdo.OUR3d;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ScanLinesFrame extends JPanel{
	private static final long serialVersionUID = -5900190066834239787L;
	private BufferedImage bi;

	private float angleYAxis;
	private float cosy = 1;
	private float siny;
	private float angleXAxis;
	private float cosx = 1;
	private float sinx;
	private float angleZAxis;
	private float cosz = 1;
	private float sinz;
	private boolean invertDestination = false;
	@SuppressWarnings("unused")
	private int deslocX;
	@SuppressWarnings("unused")
	private int deslocY;
	private boolean clear = false;

	int middleScreenX = 800/2;
	int middleScreenY = 600/2;

	Point2 pointHeightUV = new Point2(0,1);
	Point2 pointCenterUV = new Point2(0,0);
	Point2 pointWidthUV =  new Point2(1,0);
	Point2 pointCenterUV2 = new Point2(1,1);

	//private Point2[] tcs = new Point2[]{pointHeightUV,pointCenterUV, pointWidthUV, pointWidthUV,pointCenterUV2,pointHeightUV};
	private Point2[] tcs = new Point2[]{pointHeightUV,pointCenterUV, pointWidthUV};

	Point2 pointHeight = new Point2(0, 33*3, 0);
	Point2 pointCenter = new Point2(0, 0 , 0);
	Point2 pointWidth = new Point2(33*3, 0, 0);

	Point2 pointCenter2 = new Point2(33*3, 33*3, 0);

	Face2 f = new Face2(new Point2[]{pointHeight, pointCenter, pointWidth}, 0);
	Face2 f2 = new Face2(new Point2[]{pointWidth, pointCenter2, pointHeight}, 0);
	private List<Face2> faces = new ArrayList<>(Arrays.asList(f));//, f2));

	List<int[][]> textureImageCache = new ArrayList<>();

	public static void main(String...string){
		System.out.println(99f / 100);
		ScanLinesFrame frame = new ScanLinesFrame();
		frame.loadImage();
		frame.setSize(800,600);

		Frame mframe = new Frame();
		mframe.setLayout(null);
		mframe.add(frame);
		mframe.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		mframe.setSize(800,600);
		mframe.setVisible(true);


		frame.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_RIGHT){
					frame.changeAngleYAxis(3);
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_LEFT){
					frame.changeAngleYAxis(-3);
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_UP){
					frame.changeAngleXAxis(3);
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_DOWN){
					frame.changeAngleXAxis(-3);
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_T){
					frame.changeAngleZAxis(3/3);
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_G){
					frame.changeAngleZAxis(-3/3);
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_R){
					frame.reset();
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_Q){
					frame.invertDestination = !frame.invertDestination;
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_D){
					//frame.debug = !frame.debug;
					Texturer.debug = !Texturer.debug;
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_I){
					frame.deslocY -= 3;
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_M){
					frame.deslocY += 3;
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_J){
					frame.deslocX -= 3;
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_K){
					frame.deslocX += 3;
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_S){
					frame.swap();
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_W){
					frame.swapVertices();
					frame.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_Z){
					frame.clear = !frame.clear;
					frame.repaint();
				}
			}
		});
		frame.requestFocus();
		frame.requestFocus();

	}

	private void swap(){
		Point2 tempTC = tcs[0];
		tcs[0] = tcs[1];
		tcs[1] = tcs[2];
		tcs[2] = tempTC;
	}

	private void swapVertices(){
		Face2 f = faces.get(0);
		Point2 temp = f.vertices[0];
		f.vertices[0] = f.vertices[1];
		f.vertices[1] = f.vertices[2];
		f.vertices[2] = temp;
	}

	private void loadImage(){
		try {
			bi = ImageIO.read(new File("C:\\jason\\generic_workspace\\javatest\\com\\jdo\\OUR3d\\OURimage.png"));
			textureImageCache.add(generateImageIntArray(bi));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private int[][] generateImageIntArray(Image image) throws Exception{
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		PixelGrabber pg = new PixelGrabber(image, 0, 0, imageWidth, imageHeight, true);
		pg.grabPixels();
		int[] localImageData = (int[])pg.getPixels();
		int[][] newImageData = new int[imageWidth][imageHeight];
		for (int x=0; x < localImageData.length; x++){
			int imageX = x % imageHeight;
			int imageY = x / imageWidth;
			newImageData[imageX][imageY] = localImageData[x];
		}
		return newImageData;
	}


	private void changeAngleYAxis(float increase){
		angleYAxis += increase;
		cosy = (float)Math.cos(Math.toRadians(angleYAxis));
		siny = (float)Math.sin(Math.toRadians(angleYAxis));
	}

	private void changeAngleXAxis(float increase){
		angleXAxis += increase;
		cosx = (float)Math.cos(Math.toRadians(angleXAxis));
		sinx = (float)Math.sin(Math.toRadians(angleXAxis));
	}

	private void changeAngleZAxis(float increase){
		angleZAxis += increase;
		cosz = (float)Math.cos(Math.toRadians(angleZAxis));
		sinz = (float)Math.sin(Math.toRadians(angleZAxis));
	}

	private Point2[] rotatePolygon(Point2[] vertexs){
		Point2[] pointCalculated = new Point2[vertexs.length];
		for (int x = 0; x < vertexs.length; x++){
			Point2 p = vertexs[x];
			Point2 np = new Point2();
			//z axis
			float newX = cosz * p.x - sinz * p.y;
			float newY = cosz * p.y + sinz * p.x;

			//y axis
			float newX_Y = cosy * newX - siny * p.z;
			float newZ_Y = cosy * p.z + siny * newX;

			//x axis
			float newY_X = cosx * newY - sinx * newZ_Y;
			float newZ_X = cosx * newZ_Y + sinx * newY;
			np.x = newX_Y;
			np.y = newY_X;
			np.z = newZ_X;
			pointCalculated[x] = np;
		}

		//generate cos and sin from polygon
		return pointCalculated;
	}

	@SuppressWarnings("unused")
	private Point2[] rotateTC(){
		Point2[] tcsCentered = new Point2[tcs.length];
		for (int x=0; x < tcs.length; x++){
			Point2 tc = tcs[x];
			tcsCentered[x] = new Point2(tc.x - .5f, tc.y - .5f);
			//tcsCentered[x] = new Point2(tc.x - .37f, tc.y - .47f);
		}
		Point2[] tcsCalculated = new Point2[tcsCentered.length];
		for (int x = 0; x < tcsCentered.length; x++){
			Point2 p = tcsCentered[x];
			Point2 np = new Point2();
			//z axis
			float newX = cosz * p.x - sinz * p.y;
			float newY = cosz * p.y + sinz * p.x;

			//y axis
			float newX_Y = cosy * newX - siny * p.z;
			float newZ_Y = cosy * p.z + siny * newX;

			//x axis
			float newY_X = cosx * newY - sinx * newZ_Y;
			float newZ_X = cosx * newZ_Y + sinx * newY;
			np.x = newX_Y + .5f;
			np.y = newY_X + .5f;
			/*np.x = newX_Y + .37f;
			np.y = newY_X + .47f;*/
			np.z = newZ_X;
			tcsCalculated[x] = np;
		}

		//generate cos and sin from polygon
		return tcsCalculated;
	}



	private void reset(){
		angleXAxis = 0;
		cosx = 1;
		sinx = 0;
		angleYAxis = 0;
		cosy = 1;
		siny = 0;
		angleZAxis = 0;
		cosz = 1;
		sinz = 0;
		deslocX = 0;
		deslocY = 0;
		repaint();
	}

	public void paint(Graphics g){
		super.paint(g);
		g.drawImage(bi, 13, 33, null);
		int[][] newIntImage = new int[800][600];
		List<Face2> facesRotated = new ArrayList<Face2>();
		for (Face2 f : faces){
			Point2[] verticesRotated = rotatePolygon(f.vertices);
			Face2 fRotated = new Face2(verticesRotated, 0);
			facesRotated.add(fRotated);
		}
		//Point2[] newTcs = rotateTC();
		Point2[] newTcs = tcs;
		Texturer2.fillRasterizedImage(newIntImage, facesRotated, textureImageCache, middleScreenX, middleScreenY, g, newTcs);
		Image image = ImageHelper22.generateImage(newIntImage);
		g.drawImage(image, 0, 0, null);
	}



}

class Texturer2{
	static boolean debug = true;
	static boolean first = true;
	static boolean second = true;
	private Texturer2(){}

	public static void fillRasterizedImage(int[][] newIntImage, List<Face2> faces, List<int[][]> textureImageCache, int middleScreenX, int middleScreenY, Graphics g, Point2[] tcs){
		int tcCount =0;
		for (Face2 f : faces){

			int textureId = 0;
			Point2 pointHeightUVOrig = tcs[tcCount++];
			Point2 pointCenterUVOrig = tcs[tcCount++];
			Point2 pointWidthUVOrig = tcs[tcCount++];

			int[][] imageDataUsed = textureImageCache.get(textureId);
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

			Point2 pointHeightUV = new Point2(pointHeightUVx, pointHeightUVy);
			Point2 pointCenterUV = new Point2(pointCenterUVx, pointCenterUVy);
			Point2 pointWidthUV = new Point2(pointWidthUVx, pointWidthUVy);

			Point2[] vertexs = f.vertices;
			Point2 pointHeight = vertexs[0];
			Point2 pointCenter = vertexs[1];
			Point2 pointWidth = vertexs[2];

			//System.out.println(pointCenter.x + " " + pointCenter.y + " " + pointHeight.x + " " + pointHeight.y);


			List<Point2> origins = generateLineCoordinates(pointCenter, pointWidth);
			List<Point2> destinations = generateLineCoordinates(pointHeight, pointWidth);

			List<Point2> originsUV = generateLineCoordinates(pointCenterUV, pointWidthUV);
			List<Point2> destinationsUV = generateLineCoordinates(pointHeightUV, pointWidthUV);

			double mainSlope = (pointHeight.y-pointCenter.y)/(pointHeight.x-pointCenter.x);
			double mainSlopeUV = (pointHeightUV.y-pointCenterUV.y)/(pointHeightUV.x-pointCenterUV.x);

			//System.out.println("Math.abs(mainSlope):" + Math.abs(mainSlope));// + " " + pointCenter.x + " " + pointCenter.y + " " + pointHeight.x + " " + pointHeight.y);

			g.drawLine((int)(pointCenterUV.x + middleScreenX + 100), (int)(-pointCenterUV.y + middleScreenY + 100), (int)(pointHeightUV.x + middleScreenX + 100), (int)(-pointHeightUV.y + middleScreenY + 100));
			g.drawLine((int)(pointHeightUV.x + middleScreenX + 100), (int)(-pointHeightUV.y + middleScreenY + 100), (int)(pointWidthUV.x + middleScreenX + 100), (int)(-pointWidthUV.y + middleScreenY + 100));
			g.drawLine((int)(pointWidthUV.x + middleScreenX + 100), (int)(-pointWidthUV.y + middleScreenY + 100), (int)(pointCenterUV.x + middleScreenX + 100), (int)(-pointCenterUV.y + middleScreenY + 100));
			//g.drawLine((int)(pointWidthUVAux.x * 100 + middleScreenX + 100), (int)(-pointWidthUVAux.y * 100 + middleScreenY + 100), (int)(pointCenterUV.x * 100 + middleScreenX + 100), (int)(-pointCenterUV.y * 100 + middleScreenY + 100));
			g.drawString("JC",(int)(pointCenterUV.x + middleScreenX + 100), (int)(-pointCenterUV.y + middleScreenY + 100));
			g.drawString("JH", (int)(pointHeightUV.x + middleScreenX + 100), (int)(-pointHeightUV.y + middleScreenY + 100));
			g.drawString("JW", (int)(pointWidthUV.x + middleScreenX + 100), (int)(-pointWidthUV.y + middleScreenY + 100));
			g.drawString("DC", (int)(.36 * 100 + middleScreenX + 100), (int)(-.48 * 100 + middleScreenY + 100));

			/*System.out.println("origins.size():" + origins.size() + " - destinations.size():" + destinations.size());
			System.out.println("originsUV.size():" + originsUV.size() + " - destinationsUV.size():" + destinationsUV.size());
			*/
			double lineSlope = 0;
			double factorOD = (float)destinations.size() / origins.size();
			double factorODUV = (float)destinationsUV.size() / originsUV.size();
			double factorOO = (float)originsUV.size() / origins.size();

			double tX = Math.abs(pointHeight.x-pointCenter.x);
			double tY = Math.abs(pointHeight.y-pointCenter.y);
			double tLength = Math.pow(tX*tX + tY*tY, .5);

			double tXUV = Math.abs(pointHeightUV.x-pointCenterUV.x);
			double tYUV = Math.abs(pointHeightUV.y-pointCenterUV.y);
			double tLengthUV = Math.pow(tXUV*tXUV + tYUV*tYUV, .5);

			float jump = (float)(tLengthUV/tLength);
			int count = 0;
			System.out.println("jump:" + jump + "  - factorOO:" + factorOO);
			//System.out.println("jump:" + jump);
			first = true;
			for (int o=0; o < origins.size(); o++){
				int od = (int)Math.round(factorOD * o);
				Point2 po = origins.get(o);
				if (od >= destinations.size()){
					System.out.println("od >= destinations.size()");
					break;
				}
				Point2 pd =  destinations.get(od);
				int oUV = (int)Math.round(o * factorOO);
				int odUV = (int)Math.round(factorODUV * oUV);
				if (oUV >= originsUV.size() || odUV >= destinationsUV.size()){
					System.out.println("oUV >= originsUV.size() || odUV >= destinationsUV.size()");
					break;
				}
				Point2 poUV = originsUV.get(oUV);
				Point2 pdUV =  destinationsUV.get(odUV);
				//System.out.println("po.x:" + po.x + " - po.y:" + po.y + "poUV.x:" + poUV.x + " - poUV.y:" + poUV.y);
				//System.out.println("pd.x:" + pd.x + " - pd.y:" + pd.y + "pdUV.x:" + pdUV.x + " - pdUV.y:" + pdUV.y);
				double vX = Math.abs(pd.x-po.x);
				double vY = Math.abs(pd.y-po.y);
				double vLength = Math.pow(vX*vX + vY*vY, .5);
				//System.out.println("jump:" + jump + " - vLength:" + vLength + " - vLengthUV:" + vLengthUV);

				List<Integer> texels = getTexels(poUV, pdUV, imageDataUsed, jump, mainSlopeUV);
				if (count++ < 10){
					System.out.println("texels.size():" + texels.size());
				}
				int texelIndex = 0;
				if (Math.abs(mainSlope) >= 1){
					lineSlope = (pd.x-po.x)/(pd.y-po.y);
					//lineSlope = (pointHeight.x-pointCenter.x)/(pointHeight.y-pointCenter.y);
					float increment = (float)(vY/vLength);
					if (increment == 0){
						System.out.println("zero! hahahah");
						continue;
					}
					if ((int)po.y < (int)pd.y){
						for (double y=po.y; y <= pd.y; ){
							int imageX = (int)Math.round(po.x + (y-po.y) * lineSlope);
							int imageY = (int)Math.round(y);
							try {
								newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels.get(texelIndex++);//0xFFFF0000;
							}catch (Exception e){
								//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
							}
							y+=increment;
						}
					}else /*if ((int)po.y > (int)pd.y) */ {
						for (double y=po.y; y >= pd.y; ){
							int imageX = (int)Math.round(po.x + (y-po.y) * lineSlope);
							int imageY = (int)Math.round(y);
							try {
								newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels.get(texelIndex++);
							}catch (Exception e){
								//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
							}
							y-=increment;
						}
					}
				}else{
					lineSlope = (pd.y-po.y)/(pd.x-po.x);
					float increment = (float)(vX/vLength);
					if (increment == 0){
						System.out.println("zero! hahahah");
						continue;
					}
					if (po.x < pd.x){
						for (double x=po.x; x <= pd.x; ){
							int imageX = (int)Math.round(x);
							int imageY = (int)Math.round(po.y  + (x-po.x) * lineSlope);
							try {
								newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels.get(texelIndex++);//0xFFFF0000;
							}catch (Exception e){
								//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
							}
							x+=increment;
						}
					}else{
						for (double x=po.x; x >= pd.x; ){
							int imageX = (int)Math.round(x);
							int imageY = (int)Math.round(po.y  + (x-po.x) * lineSlope);
							try{
								newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels.get(texelIndex++);//0xFFFF0000;
							}catch (Exception e){
								//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
							}
							x-=increment;
						}
					}
				}
			}
		}
		System.out.println("--->");
	}

	private static List<Integer> getTexels(Point2 po, Point2 pd, int[][] imageDataUsed, float increment, double mainSlopeUV){
		List<Integer> texels = new ArrayList<>();
		double lineSlope = 0;

		int imageHeight = imageDataUsed[0].length;
		if (Math.abs(mainSlopeUV) >= 1){
			lineSlope = (pd.x-po.x)/(pd.y-po.y);
			if (first){
				System.out.println("po(x,y):" + po.x + "," + po.y + " - pd(x,y):" + pd.x + "," + pd.y);
				System.out.println("(int)Math.round((pd.y + increment - po.y) / increment):" + (int)Math.round((pd.y + increment - po.y) / increment));
				first = false;
			}
			if ((int)po.y < (int)pd.y){
				for (double y=po.y; y <= pd.y; ){
					int imageX = (int)Math.round(po.x + (y-po.y) * lineSlope);
					int imageY = (int)Math.round(y);
					try{
						texels.add(imageDataUsed[imageX][imageHeight-1-imageY]);
					}catch (Exception e){
					}
					//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels[texelndex++]//0xFFFF0000;
					y+=increment;
				}
			}else /*if ((int)po.y > (int)pd.y) */ {
				for (double y=po.y; y >= pd.y; ){
					int imageX = (int)Math.round(po.x + (y-po.y) * lineSlope);
					int imageY = (int)Math.round(y);
					try{
						texels.add(imageDataUsed[imageX][imageHeight-1-imageY]);
					}catch (Exception e){}
					//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = 0xFFFF0000;
					y-=increment;
				}
			}
		}else{
			lineSlope = (pd.y-po.y)/(pd.x-po.x);
			if (po.x < pd.x){
				for (double x=po.x; x <= pd.x; ){
					int imageX = (int)Math.round(x);
					int imageY = (int)Math.round(po.y  + (x-po.x) * lineSlope);
					try{
						texels.add(imageDataUsed[imageX][imageHeight-1-imageY]);
					}catch (Exception e){}
					//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels.get(texelndex++);//0xFFFF0000;
					x+=increment;
				}
			}else{
				for (double x=po.x; x >= pd.x; ){
					int imageX = (int)Math.round(x);
					int imageY = (int)Math.round(po.y  + (x-po.x) * lineSlope);
					try{
						texels.add(imageDataUsed[imageX][imageHeight-1-imageY]);
					}catch (Exception e){}
					//newIntImage[imageX + middleScreenX][-imageY + middleScreenY] = texels.get(texelndex++);//0xFFFF0000;
					x-=increment;
				}
			}
		}
		return texels;
	}

	private static List<Point2> generateLineCoordinates(Point2 a, Point2 b){
		List<Point2> scanCoords = new ArrayList<>();
		Point2 lesser = null;
		Point2 greater = null;
		boolean invertCoords = false;
		float slope = (b.y-a.y)/(b.x-a.x);

		double vX = Math.abs(a.x-b.x);
		double vY = Math.abs(a.y-b.y);
		double vLength = Math.pow(vX*vX + vY*vY, .5);
		if (a.x != b.x){
			if (a.x <= b.x){
				lesser = a;
				greater = b;
			}else{
				lesser = b;
				greater = a;
				invertCoords = true;
			}
			for (double x=lesser.x; x < greater.x;){
				float y = (float)(lesser.y + (x-lesser.x) * slope);
				scanCoords.add(new Point2((float)x, y));
				x+=vX/vLength/2;
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
			for (double y=lesser.y; y < greater.y;){
				float x = (float)(lesser.x + (y-lesser.y) * slope);
				scanCoords.add(new Point2(x, (float)y));
				y+=vY/vLength/2;
			}
		}
		if (invertCoords){
			Collections.reverse(scanCoords);
		}
		return scanCoords;
	}
}

class ImageHelper22{
	private ImageHelper22(){}
	public static Image generateImage(int[][] newIntImage){
		int imageWidth = newIntImage.length;
		int imageHeight = newIntImage[0].length;
		int imaCounter = 0;
		int[] newImageDataOneDimension =  new int[imageWidth*imageHeight];
		for (int y=0; y < imageHeight; y++){
			for (int x=0; x < imageWidth; x++){
				try {
					newImageDataOneDimension[imaCounter++] = newIntImage[x][y];
				}catch(Exception e){}
			}
		}
		MemoryImageSource mis = new MemoryImageSource(imageWidth, imageHeight, newImageDataOneDimension, 0, imageWidth);
		Image newImage = Toolkit.getDefaultToolkit().createImage(mis);
		return newImage;
	}
}
class ScanInfo2{
	List<Point2> coords;
	boolean invertCoords;
	float jumpXFactor;
	float jumpYFactor;
}


class Point2 {
	float x;
	float y;
	float z;

	Point2(){}

	Point2(float x, float y){
		this(x,y,0);
	}

	Point2(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

class Face2 {
	Point2[] vertices;
	int textureId;
	Face2(){}

	Face2(Point2[] vertices, int textureId){
		this.vertices = vertices;
		this.textureId = textureId;
	}
}

class TCComparator implements Comparator<Point2>{
	public int compare(Point2 o1, Point2 o2){
		double o1Length = Math.pow(o1.x * o1.x + o1.y * o1.y, .5);
		double o2Length = Math.pow(o2.x * o2.x + o2.y * o2.y, .5);
		if (o1Length < o2Length){
			return -1;
		}else{
			return 1;
		}
	}
}
