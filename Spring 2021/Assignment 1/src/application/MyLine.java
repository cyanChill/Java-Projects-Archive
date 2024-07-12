//File: MyLine.java
package application;
import javafx.scene.canvas.GraphicsContext;
import java.lang.Math;

public class MyLine extends MyShape{
	int x1, y1, x2, y2;
	MyColor color;
	
	MyLine(int x1, int y1, int x2, int y2, MyColor color) {
		super(x1, y1, color);
		
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
	}
	
	public int getX2() { return x2; }
	public int getY2() { return y2; }

	public double length() { return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)); }
	
	public double xAngle() { 
		if (x2 - x1 == 0) return 90; // Case with vertical line
		return Math.toDegrees(Math.atan((double) (y2 - y1) / (double) (x2 - x1))); 
	}
	
	@Override
	public double perimeter() { return length(); }
	
	@Override
	public String toString() { return "This is the MyLine object with endpoints (" + x1 + ", " + y1 + ") & (" + 
	                           x2 + ", " + y2 + "), Length of " + length() + ", Angle of " + xAngle() + 
	                           ", and color = " + color; }
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setStroke(color.getJavaFXColor());
		GC.strokeLine(x1, y1, x2, y2);
	}
}

	