package com.jdo.OUR3d;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ScanLinesFrameNew extends JPanel{
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

	Vertex pointHeightUV = new Vertex(0,1);
	Vertex pointCenterUV = new Vertex(0,0);
	Vertex pointWidthUV =  new Vertex(1,0);
	Vertex pointCenterUV2 = new Vertex(1,1);

	//private Vertex[] tcs = new Vertex[]{pointHeightUV,pointCenterUV, pointWidthUV, pointWidthUV,pointCenterUV2,pointHeightUV};
	private Vertex[] tcs = new Vertex[]{pointHeightUV,pointCenterUV, pointWidthUV};

	Vertex pointHeight = new Vertex(0, 33*3, 0);
	Vertex pointCenter = new Vertex(0, 0 , 0);
	Vertex pointWidth = new Vertex(33*3, 0, 0);

	Vertex[] vertices = new Vertex[]{pointHeight,pointCenter, pointWidth}; 
	//Vertex pointCenter2 = new Vertex(33*3, 33*3, 0);

	Face f = new Face(new int[]{0,1,2}, new int[]{0,1,2}, "OURDefaultImage");
	private List<Face> faces = new ArrayList<>(Arrays.asList(f));//, f2));

	List<int[][]> textureImageCache = new ArrayList<>();

	public static void main(String...string){
		ScanLinesFrameNew frame = new ScanLinesFrameNew();
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
		Vertex tempTC = tcs[0];
		tcs[0] = tcs[1];
		tcs[1] = tcs[2];
		tcs[2] = tempTC;
	}

	private void swapVertices(){
		/*Face f = faces.get(0);
		Vertex temp = f.vertices[0];
		f.vertices[0] = f.vertices[1];
		f.vertices[1] = f.vertices[2];
		f.vertices[2] = temp;
		*/
	}

	private void loadImage(){
		try {
			ImageHelper.add("OURDefaultImage", new File("C:\\jason\\generic_workspace\\javatest\\com\\jdo\\OUR3d\\OURimage.png"));
			bi = ImageIO.read(new File("C:\\jason\\generic_workspace\\javatest\\com\\jdo\\OUR3d\\OURimage.png"));
			//textureImageCache.add(generateImageIntArray(bi));
		}catch (Exception e){
			e.printStackTrace();
		}
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

	private Vertex[] rotatePolygon(Vertex[] vertexs){
		Vertex[] pointCalculated = new Vertex[vertexs.length];
		for (int x = 0; x < vertexs.length; x++){
			Vertex p = vertexs[x];
			Vertex np = new Vertex();
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
	private Vertex[] rotateTC(){
		Vertex[] tcsCentered = new Vertex[tcs.length];
		for (int x=0; x < tcs.length; x++){
			Vertex tc = tcs[x];
			tcsCentered[x] = new Vertex(tc.x - .5f, tc.y - .5f);
			//tcsCentered[x] = new Vertex(tc.x - .37f, tc.y - .47f);
		}
		Vertex[] tcsCalculated = new Vertex[tcsCentered.length];
		for (int x = 0; x < tcsCentered.length; x++){
			Vertex p = tcsCentered[x];
			Vertex np = new Vertex();
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
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		int[][] newIntImage = new int[windowHeight][windowHeight];
		float[][] depth = new float[windowWidth][windowHeight];
		fill(depth);

		Vertex[] newVertexs = new Vertex[vertices.length];
		for (Face f : faces){//For while
			Vertex[] vs = new Vertex[]{vertices[f.vIds[0]], vertices[f.vIds[1]], vertices[f.vIds[2]]};
			Vertex[] verticesRotated = rotatePolygon(vs);
			newVertexs[f.vIds[0]] = verticesRotated[f.vIds[0]];
			newVertexs[f.vIds[1]] = verticesRotated[f.vIds[1]];
			newVertexs[f.vIds[2]] = verticesRotated[f.vIds[2]];
		}
		//Vertex[] newTcs = rotateTC();
		Vertex[] newTcs = tcs;
		//TexturerScanLine.fillRasterizedImage(newIntImage, faces, newVertexs, newTcs, ImageHelper.textureImageCache, middleScreenX, middleScreenY, depth, g);
		//Texturer.fillRasterizedImage(newIntImage, faces, newVertexs, newTcs, ImageHelper.textureImageCache, middleScreenX, middleScreenY, depth);
		TexturerFillRedArray.fillRasterizedImage(newIntImage, faces, newVertexs, newTcs, ImageHelper.textureImageCache, middleScreenX, middleScreenY, depth);
		Image image = ImageHelper.generateImage(newIntImage);
		g.drawImage(image, 0, 0, null);
	}

	private void fill(float[][] depth) {
		for (int x=0; x < depth.length; x++){
			for (int y=0; y < depth[x].length; y++){
				depth[x][y] = -3333333;
			}
		}
	}
}

