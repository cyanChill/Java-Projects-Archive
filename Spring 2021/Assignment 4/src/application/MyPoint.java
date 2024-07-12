// File: MyPoint.java
package application;
import javafx.scene.canvas.GraphicsContext;
import java.lang.Math;

public class MyPoint {
	int x, y;
	MyColor color;
	
	MyPoint() { 
		setPoint(0, 0); 
		setColor(MyColor.BLACK);
	}
	MyPoint(int x, int y) { 
		setPoint(x, y); 
		setColor(MyColor.BLACK);
	}
	MyPoint(int x, int y, MyColor color) { 
		setPoint(x, y); 
		setColor(color);
	}
	MyPoint(MyPoint p) {  setPoint(p); }
	
	public void setPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void setPoint(MyPoint p) {
		this.x = p.getX();
		this.y = p.getY();
		this.color = p.getColor();
	}
	public void setColor(MyColor color) { this.color = color; }
	
	public int getX() { return x; }
	public int getY() { return y; }
	public MyPoint getPoint() { return new MyPoint(x, y, color); }
	public MyColor getColor() { return color; }
	
	public void translation(int dx, int dy) { setPoint(x + dx, y + dy); }
	public double distanceFromOrigin() { return Math.sqrt(x * x + y * y); }
	public double distance(MyPoint p) { return Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2)); }
	
	public void draw(GraphicsContext GC) { 
		GC.setFill(color.getJavaFXColor());
		GC.fillOval(x, y, 3, 3);
	}
	
	@Override
	public String toString() { return "Point (" + x + ", " + y + ") with Color = " + color; }
}
