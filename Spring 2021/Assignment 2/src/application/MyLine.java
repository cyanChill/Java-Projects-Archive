// File: MyLine.java
package application;
import javafx.scene.canvas.GraphicsContext;
import java.lang.Math;

public class MyLine extends MyShape {
	MyPoint p1, p2;
	MyPoint [] line = new MyPoint[2]; // Contains the 2 end points of the line
	MyColor color;
	
	// Input are the 2 end points and color
	MyLine(MyPoint p1, MyPoint p2, MyColor color) {
		super(p1, color);
		this.p1 = p1;
		this.p2 = p2;
		this.line[0] = p1;
		this.line[1] = p2;
		this.color = color;
	}
	
	public MyPoint [] getLine() { return line; }
	public double length() { return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2)); }
	
	public double xAngle() { 
		if (p2.getX() - p1.getX() == 0) return 90; // Case with vertical line
		return Math.toDegrees(Math.atan((double) (p2.getY() - p1.getY()) / (double) (p2.getX() - p1.getX()))); 
	}
	
	@Override
	public double area() { return 0; }
	@Override
	public double perimeter() { return length(); }
	
	@Override
	public String toString() { return "MyLine object with endpoints (" + p1.getX() + ", " + p1.getY() + ") & (" + 
	                           p2.getX() + ", " + p2.getY() + "), Length: " + length() + ", Angle: " + xAngle() + 
	                           ", and Color: " + color; }
	
	public MyRectangle getMyBoundingRectangle() {
		MyPoint pTLC = new MyPoint(Math.min(p1.getX(),p2.getX()), Math.min(p1.getY(), p2.getY()));
		return new MyRectangle(pTLC,Math.abs(p1.getX() - p2.getX()),Math.abs(p1.getY() - p2.getY()),color);
	}
	
	public boolean pointInMyShape(MyPoint p) {
		// Find the equation of the line and then plug in the point; if both sides of the equation are equal, then the point is one the line 
		if (p.getX() < p1.getX() || p.getX() > p2.getX() || p.getY() < p1.getY() || p.getY() > p2.getY()) return false;
		// Special case for vertical line
		if (p1.getX() == p2.getX()) {
			if (p.getX() == p1.getX()) return Math.min(p1.getY(),p2.getY()) <= p.getY() && p.getY() <= Math.max(p1.getY(),p2.getY());
			return false;
		}
		double slope = (double) (p2.getY() - p1.getY())/(p2.getX() - p1.getX());
		double intercept = (double) (p1.getY() - (slope * p1.getX()));
		return slope * p.getX() + intercept == p.getY();
	}
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setStroke(color.getJavaFXColor());
		GC.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	@Override
	public void drawBoundary(GraphicsContext GC) {
		GC.setStroke(color.getJavaFXColor());
		MyRectangle bound = getMyBoundingRectangle();
		GC.strokeRect(bound.getTLC().getX(), bound.getTLC().getY(), bound.getWidth(), bound.getHeight());
	}
}
