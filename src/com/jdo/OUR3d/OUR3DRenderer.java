package com.jdo.OUR3d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jdo.OUR3d.test.OURMouseListener;

public class OUR3DRenderer extends JPanel{
	private static final long serialVersionUID = -3598512700073680976L;

	private float yradius = 0;
	private float cosy = 1;
	private float siny = 0;
	private float xradius = 0;
	private float cosx = 1;
	private float sinx = 0;
	private float zradius = 0;
	private float cosz = 1;
	private float sinz = 0;
	private float characterZPosition = 0;
	private float characterXPosition = 0;
	private float scale =  0.1f;
	private String OURDefaultImage = "OURDefaultImage";
	int middleScreenX = 1034 / 2;//3
	int middleScreenY = 433;
	private boolean rendereThreadStarted = false;

	private static List<Polygon> polygons = new ArrayList<>();
	
	private RendererThread rt1 = new RendererThread();
	@SuppressWarnings("unused")
	private RendererThread rt2 = new RendererThread();
	@SuppressWarnings("unused")
	private RendererThread rt3 = new RendererThread();
	@SuppressWarnings("unused")
	private RendererThread rt4 = new RendererThread();
	

	private class FaceComparator implements Comparator<Face>{
		public int compare(Face o1, Face o2){
			if (o1.maxZ < o2.maxZ){
				return -1;
			}else if (o1.maxZ > o2.maxZ){
				return 1;
			}else{
				return 0;
			}
		}
	}

	public static void main(String...string){
		float scale = 1;
		if (string.length > 0){
			scale = Float.parseFloat(string[0]);
		}
		OUR3DRenderer OUR3DTest = new OUR3DRenderer();
		OUR3DTest.scale = scale;
		OUR3DTest.loadImage();
		OUR3DTest.setSize(1333, 833);
		OUR3DTest.changeXradius(5);
		//OUR3DTest.changeradiusY(3);
		OUR3DTest.loadVertexs();
		//OURMouseListener OURml = p -> System.out.println("Hi PAI");
		//OUR3DTest.addMouseListener(OURml);

		OUR3DTest.addMouseListener((OURMouseListener)p -> System.out.println("Hi PAI"));
		
		JFrame f = new JFrame();
		f.setSize(1334, 834);
		f.setLayout(null);
		f.add(OUR3DTest);

		f.setVisible(true);

		f.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.out.println("In windowClosing");
				System.exit(0);
			}
		});

		OUR3DTest.setLayout(null);
		OUR3DTest.requestFocus();
		OUR3DTest.requestFocus();

		OUR3DTest.addKeyListener(new KeyAdapter(){
			//long start = System.currentTimeMillis();
			@Override
			public void keyReleased(KeyEvent e){
			}

			@Override
			public void keyPressed(KeyEvent e){
				//long now = System.currentTimeMillis();
				//System.out.println("keyInterval:" + (now - start));
				//start = now;
				if (e.getKeyCode() == 0x27){ // right
					OUR3DTest.changeradiusY(3/3/3f);
				}else if (e.getKeyCode() == 0x25){ //left
					OUR3DTest.changeradiusY(-3/3/3f);
				}else if (e.getKeyCode() == 0x26){ //up
					OUR3DTest.changeZ(33);
				}else if (e.getKeyCode() == 0x28){ //down
					OUR3DTest.changeZ(-33);
				}else if (e.getKeyCode() == KeyEvent.VK_1){ //
					OUR3DTest.rotateZAxis(1);
				}else if (e.getKeyCode() == KeyEvent.VK_2){ //
					OUR3DTest.rotateZAxis(-1);
				}else if (e.getKeyCode() == KeyEvent.VK_J){ //J
					//OUR3DTest.showValues();
				}else if (e.getKeyCode() == KeyEvent.VK_R){ //reset
					OUR3DTest.reset();
				}else if (e.getKeyCode() == KeyEvent.VK_S){
				}else if (e.getKeyCode() == KeyEvent.VK_L){
					OUR3DTest.loadVertexs();
				}else if (e.getKeyCode() == KeyEvent.VK_I){
					System.out.println("size:" + polygons.size());
				}else if (e.getKeyCode() == KeyEvent.VK_P){
				}else if (e.getKeyCode() == KeyEvent.VK_Q){
					OUR3DTest.alterX(3);
				}else if (e.getKeyCode() == KeyEvent.VK_A){
					OUR3DTest.alterX(-3);
				}else if (e.getKeyCode() == KeyEvent.VK_W){
					OUR3DTest.alterY(3);
				}else if (e.getKeyCode() == KeyEvent.VK_E){
					OUR3DTest.alterY(-3);
				}else if (e.getKeyCode() == KeyEvent.VK_T){
					OUR3DTest.changeXradius(3/3f);
				}else if (e.getKeyCode() == KeyEvent.VK_G){
					OUR3DTest.changeXradius(-3/3f);
				}else if (e.getKeyCode() == KeyEvent.VK_D){
					Texturer.debug = !Texturer.debug;
					OUR3DTest.repaint();
				}else if (e.getKeyCode() == KeyEvent.VK_F){
					Texturer.wireframe = !Texturer.wireframe;
					OUR3DTest.repaint();
				}
			}
		});
	}

	private void alterX(float amount){
		for (Polygon polygon : polygons){
			for (Vertex v : polygon.vertices){
				v.x += amount;
			}
		}
		repaint();
	}

	private void alterY(float amount){
		for (Polygon polygon : polygons){
			for (Vertex v : polygon.vertices){
				v.y += amount;
			}
		}
		repaint();
	}

	private void loadImage(){
		try {

			//Image bi = ImageIO.read(new File("C:\\jason\\generic_workspace_new\\javatest\\com\\jdo\\OUR3d\\OURimage.png"));
			//Image bi = ImageIO.read(new File("C:\\jason\\generic_workspace_new\\javatest\\com\\jdo\\OUR3d\\Cottage Texture.jpg"));
			ImageHelper.add("OURDefaultImage", new File("C:\\jason\\generic_workspace_new\\javatest\\model_temp\\temp\\Farmhouse Texture.jpg"));//OURimage.png
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void reset(){
		yradius = 0;
		cosy = 1;
		siny = 0;
		xradius = 0;
		cosx = 1;
		sinx = 0;
		characterZPosition = 0;
		characterXPosition = 0;
		polygons.clear();
		repaint();
	}

	private void loadVertexs(){
		scale = 33.3f/3;//33;//53;//133 + 33*313;//;
		String workingLine = null;
		try {
			File[] files = new File("C:\\jason\\generic_workspace_new\\javatest\\model_temp\\temp").listFiles(new OBJFileFilter());
			for (File f : files){
				//Search for a mtl file
				File mtlFile = new File(f.getParent(), f.getName().replaceAll("\\.obj", "\\.mtl"));
				//System.out.println("mtlFile.getCanonicalPath():" + mtlFile.getCanonicalPath());
				if (mtlFile.exists()){
					ImageHelper.loadMTL(mtlFile);
					System.out.println("mtl loaded :" + ImageHelper.textureImageCache.size());
				}
				List<String> lines = Files.readAllLines(f.toPath());
				List<Vertex> pointsList = new ArrayList<>();
				List<Vertex> tcList = new ArrayList<>();
				List<Face> facesList = new ArrayList<>();
				String currentTextureKey = OURDefaultImage;
J:				for (String line : lines){
					workingLine = line;
					if (!line.equals("")){
						StringTokenizer st = new StringTokenizer(line);
						String type = st.nextToken();
						if (type != null && type.equalsIgnoreCase("v")){
							pointsList.add(new Vertex(Float.parseFloat(st.nextToken()) * scale, Float.parseFloat(st.nextToken()) * scale, Float.parseFloat(st.nextToken()) * scale));
						}else if (type != null && type.equalsIgnoreCase("vt")){
							tcList.add(new Vertex(Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken())));
						}else if (type != null && type.equalsIgnoreCase("f")){
							List<Integer> vIds = new ArrayList<>();
							List<Integer> tcIds = new ArrayList<>();
							while (st.hasMoreTokens()){
								String vtc = st.nextToken();
								int vSlash = vtc.indexOf("/");
								if (vSlash != -1){
									String vId = vtc.substring(0, vSlash);
									vIds.add(Integer.parseInt(vId)-1);
									int nextSlash = vtc.indexOf("/", vSlash+1);
									if (nextSlash != -1){
										String tcId = vtc.substring(vSlash+1, nextSlash);
										//System.out.println("tcId:" + tcId);
										if (!tcId.equals("")){ // vixi! face without texture coordinate
											tcIds.add(Integer.parseInt(tcId)-1);
										}else{
											continue J;
										}
									}else{
										String tcId = vtc.substring(vSlash+1);
										tcIds.add(Integer.parseInt(tcId)-1);
									}
								}else{
									vIds.add(Integer.parseInt(vtc)-1);
								}
								//System.out.println("line:" + line + "vId:" + vId);
							}
							int [] vIdsArray = new int[vIds.size()];
							for (int x=0; x < vIds.size(); x++){
								vIdsArray[x] = vIds.get(x);
							}
							int [] tcIdsArray = new int[tcIds.size()];
							for (int x=0; x < tcIds.size(); x++){
								tcIdsArray[x] = tcIds.get(x);
							}
							if (vIdsArray.length > 3){
								facesList.addAll(generateNewFaces(vIdsArray, tcIdsArray, currentTextureKey));
							}else{
								facesList.add(new Face(vIdsArray, tcIdsArray, currentTextureKey));
							}
						}else if (type != null && type.equalsIgnoreCase("usemtl") && mtlFile.exists()){
							currentTextureKey = st.nextToken();
						}
					}

				}
				for (Face face : facesList){
					TextureCoordinateHelper.adjustFaceByTC(face, tcList);
				}
				facesList.addAll(facesList);
				//facesList.addAll(facesList);
				//facesList.addAll(facesList);
				//facesList.addAll(facesList);
				Polygon p = new Polygon(facesList.toArray(new Face[facesList.size()]), f.getName(), pointsList.toArray(new Vertex[pointsList.size()]), tcList.toArray(new Vertex[tcList.size()]));
				//List<Polygon> ps  = PolygonHelper.split(p);
				polygons.add(p);
			}
			System.out.println("polygons.size():" + polygons.size());
		}catch (Exception e){
			System.out.println("Error workingLine:" + workingLine);
			e.printStackTrace();
		}
		repaint();
	}

	private List<Face> generateNewFaces(int[] vIdsArray, int[]tcIdsArray, String textureKey){
		List<Face> newFaces = new ArrayList<>();
		int firstvId = vIdsArray[0];
		int firsttcId = tcIdsArray[0];
		for (int x=0; x < vIdsArray.length-2; x++){
			int[] newvIdsArray = new int[3];
			int[] newtcIdsArray = new int[3];
			newvIdsArray[0] = firstvId;
			newvIdsArray[1] = vIdsArray[x+1];
			newvIdsArray[2] = vIdsArray[x+2];
			newtcIdsArray[0] = firsttcId;
			newtcIdsArray[1] = tcIdsArray[x+1];
			newtcIdsArray[2] = tcIdsArray[x+2];
			newFaces.add(new Face(newvIdsArray, newtcIdsArray, textureKey));
		}
		//System.out.println("newFaces.size():" + newFaces.size());
		return newFaces;
	}

	@SuppressWarnings("unused")
	private List<Face> generateNewFacesOld(int[] vIdsArray, int[]tcIdsArray, String textureKey){
		List<Face> newFaces = new ArrayList<>();
		for (int x=0; x <= vIdsArray.length - 3; x++){
			int[] newvIdsArray = new int[3];
			int[] newtcIdsArray = new int[3];
			if (x == 0){
				newvIdsArray[0] = vIdsArray[x];
				newvIdsArray[1] = vIdsArray[x+1];
				newvIdsArray[2] = vIdsArray[x+2];
			}else{
				newvIdsArray[0] = vIdsArray[x-1];
				newvIdsArray[1] = vIdsArray[x+1];
				newvIdsArray[2] = vIdsArray[x+2];
			}
			if (x == 0){
				newtcIdsArray[0] = tcIdsArray[x];
				newtcIdsArray[1] = tcIdsArray[x+1];
				newtcIdsArray[2] = tcIdsArray[x+2];
			}else{
				newtcIdsArray[0] = tcIdsArray[x-1];
				newtcIdsArray[1] = tcIdsArray[x+1];
				newtcIdsArray[2] = tcIdsArray[x+2];
			}
			newFaces.add(new Face(newvIdsArray, newtcIdsArray, textureKey));
		}
		return newFaces;
	}


	private synchronized void changeZ(int increase){
		characterZPosition += cosy * -increase; //must be inverted. translation from trigonometric to device pixels coordinates
		characterXPosition += siny * -increase; //must be inverted. translation from trigonometric to device pixels coordinates
		repaint();
	}

	public void changeradiusY(float increase){
		increase = -increase; //must be inverted. translation from trigonometric to device pixels coordinates
		if (yradius+increase == 360){
			yradius = 0;
		}else if(yradius == 0 && increase < 0){
			yradius = 357;
		}else{
			yradius += increase;
		}
		//System.out.println("increaseY:" + increase + " - yradius:" + yradius);
		cosy = (float)Math.cos(Math.toRadians(yradius));
		siny = (float)Math.sin(Math.toRadians(yradius));
		repaint();
	}

	private void changeXradius(float increase){
		xradius += increase;
		cosx = (float)Math.cos(Math.toRadians(xradius));
		sinx = (float)Math.sin(Math.toRadians(xradius));
		repaint();
	}

	private void rotateZAxis(float increase){
		zradius += increase;
		cosz = (float)Math.cos(Math.toRadians(zradius));
		sinz = (float)Math.sin(Math.toRadians(zradius));
		repaint();
	}

	class RendererThread extends Thread{
		private List<Polygon> polygons;
		private int[][] newIntImage;
		private float[][] depth;
		boolean done = true;
		
		public void setWork(List<Polygon> polygons, int[][] newIntImage, float[][] depth){
			this.polygons = polygons;
			this.newIntImage = newIntImage;
			this.depth = depth; 
			done = false;
		}
		@Override
		public void run(){
			while (true){
				for (Polygon polygon : polygons){
					Vertex[] newVertexs = new Vertex[polygon.vertices.length];
					for (int vi=0; vi < polygon.vertices.length; vi++){
						Vertex v = polygon.vertices[vi];
						float realX = v.x-characterXPosition;
						float realY = v.y;
						float realZ = v.z-characterZPosition;
	
						float x = cosy * realX - siny * realZ;
						float z = cosy * realZ + siny * realX;
						if (z > 3300){ //to clipping
							continue;
						}
	
						//rotate in x axis after y axis
						float y = cosx * realY - sinx * z;
						z = cosx * z + sinx * realY;
	
						//rotate in z to test
						float x_temp = cosz * x - sinz * y;
						float y_temp = cosz * y + sinz * x;
						x = x_temp;
						y = y_temp;
	
		 				if(y < -233){//to clipping
							continue;
						}
		 				
						float distance = 3300; // distance from projection plane

		 				if (z > distance * .85){//to clipping
		 					continue;
		 				}
		 				
		 				x = x / (-z / distance + 1);
						y = y / (-z / distance + 1);
	
						newVertexs[vi] = new Vertex(x,y,z);
					}
					List<Face> newFaces = new ArrayList<>(polygon.faces.length);
					for (int x=0; x < polygon.faces.length; x++){
						Face face = polygon.faces[x];
						Face newFace = null;
						float maxZ = -3333333;
						int verticessToClip  = 0;
						for (int vId : face.vIds){
							Vertex v = newVertexs[vId];
							if (v != null){
								if (v.x < -middleScreenX || v.x > middleScreenX){//to clipping
									verticessToClip++;
									continue;
								}
								if (v.z > maxZ){
									maxZ = v.z;
								}
							}
						}
						if (verticessToClip < face.vIds.length){
							newFace = new Face();
							newFace.maxZ = maxZ;
							newFace.vIds = face.vIds;
							newFace.tcIds = face.tcIds;
							newFace.textureId = face.textureId;
							newFaces.add(newFace);
						}
					}
	
					//Collections.sort(newFaces, new FaceComparator());
	
					long start = System.currentTimeMillis();
					Texturer.fillRasterizedImage(newIntImage, newFaces, newVertexs, polygon.textureCoordinates, ImageHelper.textureImageCache, middleScreenX, middleScreenY, depth);
					//TexturerTCCached.fillRasterizedImage(newIntImage, newFaces, newVertexs, polygon.textureCoordinates, ImageHelper.textureImageCache, middleScreenX, middleScreenY, depth);
					//TexturerFillRedArray.fillRasterizedImage(newIntImage, newFaces, newVertexs, polygon.textureCoordinates, ImageHelper.textureImageCache, middleScreenX, middleScreenY, depth);
					//TexturerFillRedPatternOriginal.fillRasterizedImage(newIntImage, newFaces, newVertexs, polygon.textureCoordinates, ImageHelper.textureImageCache, middleScreenX, middleScreenY, depth, null);
					long elapsed = (System.currentTimeMillis() - start);
					System.out.println("fillRasterizedImage.time:" + elapsed  + " faces:" + newFaces.size());
				}
				done = true;
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	
	public void paint(Graphics g){
		super.paint(g);
		long start = System.currentTimeMillis();
		g.drawString ("J", middleScreenX, middleScreenY);
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		int[][] newIntImage = new int[windowWidth][windowHeight];
		float[][] depth = new float[windowWidth][windowHeight];
		fill(depth);
		//One object
		rt1.setWork(polygons, newIntImage, depth);
		if (!rendereThreadStarted){
			rt1.start();
			rendereThreadStarted = true;
		}
		synchronized (rt1) {
			rt1.notify();
		}
		while (true){
			if (rt1.done){
				break;
			}else{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		
		/*
		rt1.setWork(polygons, newIntImage);
		rt2.setWork(polygons, newIntImage);
		rt3.setWork(polygons, newIntImage);
		rt4.setWork(polygons, newIntImage);
		if (!rendereThreadStarted){
			rt1.start();
			rt2.start();
			rt3.start();
			rt4.start();
			rendereThreadStarted = true;
		}
		synchronized (rt1) {
			rt1.notify();
		}
		synchronized (rt2) {
			rt2.notify();
		}
		synchronized (rt3) {
			rt3.notify();
		}
		synchronized (rt4) {
			rt4.notify();
		}
		while (true){
			if (rt1.done && rt2.done && rt3.done && rt4.done){
				break;
			}else{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		*/
		//for (int D=0; D < 15; D++){
		Image image = ImageHelper.generateImage(newIntImage);
		g.drawImage(image, 0, 0, null);
		g.setColor(Color.red);
		g.drawString("yradius:" + yradius + " - xradius:" + xradius + " cosy:" + cosy + " siny:" + siny, 13, 33);
		g.drawString ("J", middleScreenX, middleScreenY);
		System.out.println("elapsed:" + (System.currentTimeMillis() - start));
	}

	private void fill(float[][] depth) {
		for (int x=0; x < depth.length; x++){
			for (int y=0; y < depth[x].length; y++){
				depth[x][y] = -3333333;
			}
		}
	}
}