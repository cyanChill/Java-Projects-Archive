// File: MyOval.java
package application;
import javafx.scene.canvas.GraphicsContext;
import java.lang.Math;

public class MyOval extends MyShape {
	MyPoint pCenter;
	int a, b;
	MyColor color;
	
	// Inputs are center point, abscissas, and color
	MyOval(MyPoint p, int a, int b, MyColor color) {
		super(p, color);
		this.pCenter = p;
		this.a = a;
		this.b = b;
		this.color = color;
	}
	
	public int getA() { return a; }
	public int getB() { return b; }
	public MyPoint getCenter() { return pCenter; }
	
	@Override
	public double perimeter() { return 2.0 * Math.PI * Math.sqrt((Math.pow(getA(),2) + Math.pow(getB(),2))/2); }
	@Override
	public double area() { return getA() * getB() * Math.PI; }
	
	@Override
	public String toString() { 
		return "MyOval object with center point (" + pCenter.getX() + " , " + pCenter.getY() + ") " + ", Oval Width: " + 2 * a + 
	    ", Oval Height: " + 2 * b + ", Perimeter: " + perimeter() + ", Area: " + area() + ", and Color: " + color; 
	}
	
	public MyRectangle getMyBoundingRectangle() {
		MyPoint pTLC = new MyPoint(pCenter.getX() - a, pCenter.getY() - b);
		return new MyRectangle(pTLC, 2 * a, 2 * b,color);
	}
	
	public boolean pointInMyShape(MyPoint p) { 
		int dx = pCenter.getX() - p.getX();
		int dy = pCenter.getY() - p.getY();
		// Check if oval is a circle; point should be within the radius
		if (a == b) return pCenter.distance(p) <= a;
		// After rewriting the formula to finding if a point is in an ellipse/oval:
		// dx^2/a^2 + dy^2/b^2 = 1  <-> (b*dx)^2 + (a*dy)^2 = (a*b)^2
		return Math.pow(b * dx, 2) + Math.pow(a * dy, 2) <= Math.pow(a * b, 2);
	}
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		GC.fillOval(pCenter.getX() - a, pCenter.getY() - b, 2 * a, 2 * b);
	}
	@Override
	public void drawBoundary(GraphicsContext GC) {
		GC.setStroke(color.getJavaFXColor());
		GC.strokeRect(pCenter.getX() - a, pCenter.getY() - b, 2 * a, 2 * b);
	}
}
