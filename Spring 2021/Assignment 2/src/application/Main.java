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
import java.util.Date;
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
		primaryStage.setTitle("Assignment 2");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public Canvas addCanvas(int w, int h) {
		Canvas cv = new Canvas(w,h);
		GraphicsContext gc = cv.getGraphicsContext2D();

		MyPoint [] points = new MyPoint[13];
		MyShape [] shapes = new MyShape[7];
		// Create 13 random points within the canvas
		for (int i = 0; i < 13; i++) {
			points[i] = new MyPoint((int) ((w * 1/5) + Math.random() * w * 3/5), (int) ((h * 1/5) + Math.random() * h * 3/5));
		}

		shapes[0] = new MyRectangle(points[0], (int) (w * 0.125), (int) (h * 0.15), MyColor.getRandomColor());
		shapes[1] = new MyOval(points[1], (int) (w * 0.075), (int) (h * 0.1), MyColor.getRandomColor());
		shapes[2] = new MyCircle(points[2],  (int) (w * 0.125), MyColor.getRandomColor());
		shapes[3] = new MyPolygon(points[3], 3, (int) (w * 0.125), MyColor.getRandomColor());
		shapes[4] = new MyPolygon(points[4], 5, (int) (w * 0.115), MyColor.getRandomColor());
		shapes[5] = new MyPolygon(points[5], 7, (int) (w * 0.1), MyColor.getRandomColor());
		shapes[6] = new MyLine(points[6], points[7], MyColor.getRandomColor());
		
		for (int i = 0; i < 7; i++) { shapes[6 - i].draw(gc); }
		for (int i = 0; i < 7; i++) { shapes[6 - i].drawBoundary(gc); }
		// Draw the points that we'll be checking to see if they're in a shape
		for (int i = 8; i < 13; i++) { points[i].draw(gc); }
		
		// Comparisons
		System.out.println("Shape 1: Rectangle\nShape 2: Oval\nShape 3: Circle\nShape 4: Triangle\nShape 5: Pentagon\nShape 6: Heptagon\nShape 7: Line");
		for (int i = 0; i < 7; i++) {
			System.out.println("Shape " + (i + 1) + " : " + shapes[i]);
			System.out.println("Bounding Rectangle: " + shapes[i].getMyBoundingRectangle());
			System.out.println("Shapes that Intersect: ");
			// Shape Intersection Check
			for (int j = 0; j < 7; j++) {
				if (i == j) continue;
				System.out.println("Shape " + (i + 1) + " & Shape " + (j + 1) + " Intersection Rectangle: " + MyShapeInterface.intersectMyShapes(shapes[i], shapes[j]));
			}
			System.out.println("Points in Shape: ");
			// Points in Shape Check
			for (int j = 8; j < 13; j++) {
				if (shapes[i].pointInMyShape(points[j]) == true)
				System.out.println("Point (" + points[j].getX() + ", " + points[j].getY() + ") is in Shape " + (i + 1));
				else System.out.println("Point (" + points[j].getX() + ", " + points[j].getY() + ") is not in Shape " + (i + 1));
			}
		}
		return cv;
	}
	
	public static void main(String[] args) {
		System.out.println("-".repeat(76) + "\n\t\t-=-=-\t" + new Date() + "\t-=-=-\n");
		launch(args);
	}
}
