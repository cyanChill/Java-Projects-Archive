// File: MyShape.java
package application;
import javafx.scene.canvas.GraphicsContext;

abstract class MyShape extends Object implements MyShapeInterface {
	MyPoint p;
	MyColor color;
	
	// Input is a point and color
	MyShape(MyPoint p, MyColor color) {
		this.p = p;
		this.color = color;
	}
	
	public MyPoint getPoint() { return p; }
	public MyColor getColor() { return color; }
	
	public abstract double area();
	public abstract double perimeter();
	public abstract void draw(GraphicsContext GC);
	public abstract void drawBoundary(GraphicsContext GC);
	
	@Override
	public String toString() { return "MyShape object with point (" + p.getX() + ", " + p.getY() + ") and Color = " + color; }
}
