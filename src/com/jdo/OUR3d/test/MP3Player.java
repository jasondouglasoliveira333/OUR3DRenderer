//package com.jdo.OUR3d.test;
//
//import java.io.File;
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.layout.VBox;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.scene.paint.Color;
//import javafx.stage.Stage;
//
//public class MP3Player extends Application{
//	public static void main(String...strings){
//		launch(strings);
//	}
//
//	@Override
//	public void start(Stage stage) throws Exception {
//		VBox layout = new VBox();
//	    File bip = new File("C:\\jason\\hinos\\01 - 128 - irmãos amados.mp3");
//		System.out.println("bip.toURI().toString():" + bip.toURI().toString());
//		Media hit = new Media(bip.toURI().toString());
//		MediaPlayer mediaPlayer = new MediaPlayer(hit);
//		mediaPlayer.play();
//				
//	    Scene scene = new Scene(layout, Color.CORNSILK);
//	    stage.setScene(scene);
//	    stage.show();
//
//	}
//}
