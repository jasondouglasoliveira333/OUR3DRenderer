package com.jdo.OUR3d.test;

public class ThreadTest extends Thread{
	public static void main(String...strings){
		ThreadTest t = new ThreadTest();
		t.start();
		long start = System.currentTimeMillis();
		while (true){
			long now = System.currentTimeMillis();
			if (now - start > 1000){
				synchronized(t){
					t.notify();
				}
				start = now; 
			}
		}
	}
	
	public void run(){
		while(true){
			System.out.println("Here");
			try {
				synchronized (this){
					wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
}
