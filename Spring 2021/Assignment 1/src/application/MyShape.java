//File: MyShape.java
package application;
import javafx.scene.canvas.GraphicsContext;

public class MyShape extends Object {
	int x, y;
	MyColor color;
	
	MyShape(int x, int y, MyColor color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	public double area() { return 0; }
	public double perimeter() { return 0; }
	
	@Override
	public String toString() { return "This is the MyShape object with point (" + x + ", " + y + ") and color = " + color; }
	
	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		GC.fillRect(0, 0, GC.getCanvas().getWidth(), GC.getCanvas().getHeight());
	}
	
}
