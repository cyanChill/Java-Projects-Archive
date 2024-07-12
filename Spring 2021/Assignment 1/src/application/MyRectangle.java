//File: MyRectangle.java
package application;
import javafx.scene.canvas.GraphicsContext;

public class MyRectangle extends MyShape{
	int x, y, width, height;
	MyColor color;
	
	MyRectangle(int x, int y, int w, int h, MyColor color) {
		super(x, y, color);
		
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.color = color;
	}

	public int getWidth() { return width; }
	public int getHeight() { return height; }

	@Override
	public double perimeter() { return 2 * (width + height); }
	
	@Override
	public double area() { return width * height; }
	
	@Override
	public String toString() { 
		return "This is the MyRectangle object with the top left point of (" + x + " , " + y + ") " + ", Width of "
		+ width + ", Height of " + height + ", Perimeter of " + perimeter() + ", and Area of " + area(); 
	}
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		GC.fillRect(x, y, width, height);
	}
}

	