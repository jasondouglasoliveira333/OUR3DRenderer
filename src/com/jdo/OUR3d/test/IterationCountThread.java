package com.jdo.OUR3d.test;

public class IterationCountThread extends Thread{
	long start = System.currentTimeMillis();

	public static void main(String...strings){
		IterationCountThread t = new IterationCountThread();
		//t.setPriority(MAX_PRIORITY);
		t.start();
		IterationCountThread t2 = new IterationCountThread();
		//t2.setPriority(MAX_PRIORITY);
		t2.start();
		IterationCountThread t3 = new IterationCountThread();
		//t3.setPriority(MAX_PRIORITY);
		t3.start();
		IterationCountThread t4 = new IterationCountThread();
		//t4.setPriority(MAX_PRIORITY);
		t4.start();
	}
	
	public void run2(){
		int count = 0;
		while(true){
			long now = System.currentTimeMillis();
			count++;
			if (now - start > 1000){
				System.out.println("tName:" + getName() + "count:" + count);
				count = 0;
				start = now; 
			}
		}
	}
	public void run(){
		long count = 0;
		while(true){
			count++;
			if (count > 2200000000L){
				long now = System.currentTimeMillis();
				System.out.println("tName:" + getName() + " - time:" + (now-start));
				start = now;
				count = 0;
			}
		}
	}
}
