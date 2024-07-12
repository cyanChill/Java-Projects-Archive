// File: MyCircle.java
package application;
import javafx.scene.canvas.GraphicsContext;
import java.lang.Math;

public class MyCircle extends MyOval {
	MyPoint pCenter;
	int radius;
	MyColor color;
	
	// Inputs are center point, radius, and color
	MyCircle(MyPoint p, int r, MyColor color) {
		super(p,r,r,color);
		this.pCenter = p;
		this.radius = r;
		this.color = color;
	}

	public int getRadius() { return radius; }
	@Override
	public double perimeter() { return 2 * Math.PI * radius; }
	@Override
	public double area() { return radius * radius * Math.PI; }
	
	@Override
	public String toString() { 
		return "MyCircle object with center point (" + pCenter.getX() + " , " + pCenter.getY() + ") " + ", Radius: " 
		+ radius + ", Perimeter: " + perimeter() + ", Area: " + area() + ", and Color: " + color; 
	}
	
	@Override
	public MyRectangle getMyBoundingRectangle() {	
		MyPoint pTLC = new MyPoint(pCenter.getX() - radius, pCenter.getY() - radius);
		return new MyRectangle(pTLC, 2 * radius, 2 * radius,color);
	}
	
	@Override
	public boolean pointInMyShape(MyPoint p) { return pCenter.distance(p) <= radius; }
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		GC.fillOval(pCenter.getX() - radius, pCenter.getY() - radius, 2 * radius, 2 * radius);
	}
	@Override
	public void drawBoundary(GraphicsContext GC) {
		GC.setStroke(color.getJavaFXColor());
		GC.strokeRect(pCenter.getX() - radius, pCenter.getY() - radius, 2 * radius, 2 * radius);
	}
}
