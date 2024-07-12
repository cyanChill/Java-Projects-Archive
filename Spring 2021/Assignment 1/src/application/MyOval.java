//File: MyOval.java
package application;
import javafx.scene.canvas.GraphicsContext;
import java.lang.Math;

public class MyOval extends MyShape {
	int x, y, w, h;
	MyColor color;
	
	MyOval(int x, int y, int w, int h, MyColor color) {
		super(x, y, color);
		
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.color = color;
	}
	@Override
	public int getX() { return x + w/2; }
	@Override
	public int getY() { return y + h/2; }
	public int getA() { return w/2; }
	public int getB() { return h/2; }
	
	@Override
	public double perimeter() { return 2 * Math.PI * Math.sqrt((Math.pow(getA(),2)+Math.pow(getB(),2))/2); }
	
	@Override
	public double area() { return getA() * getB() * Math.PI; }
	
	@Override
	public String toString() { 
		return "This is the MyOval object with the center of (" + getX() + " , " + getY() + ") " + ", X-Axis Length: " 
	    + w + ", Y-Axis Length: " + h + ", Perimeter of " + perimeter() + ", and Area of " + area(); 
	}
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		GC.fillOval(x, y, w, h);
	}
}
