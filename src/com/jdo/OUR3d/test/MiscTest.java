package com.jdo.OUR3d.test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class MiscTest extends JFrame{
	public static void main(String...strings){
		for (int x=0; x < 10; x++){
			System.out.println("x:" + x + " - tail:" + (10-1-x));
		}
		MiscTest mt = new MiscTest();
		mt.addKeyListener(new KeyListener() {
			long start = System.currentTimeMillis();
			int count = 0;
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				long now = System.currentTimeMillis();
				count++;
				if (now - start > 1000){
					System.out.println("count:" + count);
					count = 0;
					start = now;
				}
			}
		});
		mt.setSize(800, 600);
		mt.setVisible(true);
	}
	

}
