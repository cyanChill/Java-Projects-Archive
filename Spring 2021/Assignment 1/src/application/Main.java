// File: Main.java
package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import java.lang.Math;
import java.util.Scanner;
import java.util.Date;	// For Console Logging

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scanner input = new Scanner(System.in);	
		BorderPane bP = new BorderPane();
		System.out.print("Enter the canvas width: ");
		int cw = input.nextInt();
		System.out.print("Enter the canvas height: ");
		int ch = input.nextInt();
			
		Canvas cv = addCanvas(cw,ch); 
		Pane p = new Pane();
		p.getChildren().add(cv);
		bP.setCenter(p);
		
		Scene scene = new Scene(bP,cw,ch);
		primaryStage.setTitle("Assignment 1");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public Canvas addCanvas(int w, int h) {
		Canvas cv = new Canvas(w,h);
		GraphicsContext gc = cv.getGraphicsContext2D();
		
		// Draw the graph
		double scalar = 0.35;
		int width = (int) Math.ceil(w*scalar), height = (int) Math.ceil(h*scalar), 
				x = (int) Math.ceil((w*(1-scalar))/2), y = (int) Math.ceil((h*(1-scalar))/2);
		
		MyRectangle R;
		MyOval O;
		
		while (width > (int) w*0.1 && height > (int) h*0.1) {
			R = new MyRectangle(x,y,width,height,MyColor.getRandomColor());
			R.draw(gc);
			O = new MyOval(x,y,width,height,MyColor.getRandomColor());
			O.draw(gc);
			scalar = scalar - 0.3*scalar;
			width = (int) Math.ceil(w*scalar);
			height = (int) Math.ceil(h*scalar);
			x = (int) Math.ceil((w*(1-scalar))/2);
			y = (int) Math.ceil((h*(1-scalar))/2);
			System.out.println(R);
			System.out.println(O);
		}
		
		MyLine L = new MyLine(0,0,w,h,MyColor.BLACK);
		L.draw(gc);;
		System.out.println(L);
		
		return cv;
	}
	
	public static void main(String[] args) {
		System.out.println("-".repeat(76) + "\n\t\t-=-=-\t" + new Date() + "\t-=-=-\n");
		launch(args);
	}
}
