// File: MyRectangle.java
package application;
import javafx.scene.canvas.GraphicsContext;

public class MyRectangle extends MyShape {
	MyPoint pTLC;
	int w, h;
	MyColor color;
	
	// Input is top left corner point, width, height, and color
	MyRectangle(MyPoint p, int w, int h, MyColor color) {
		super(p, color);
		this.pTLC = p;
		this.w = w;
		this.h = h;
		this.color = color;
	}

	public int getWidth() { return w; }
	public int getHeight() { return h; }
	public MyPoint getTLC() { return pTLC; }

	@Override
	public double perimeter() { return 2 * (w + h); }
	@Override
	public double area() { return w * h; }
	
	@Override
	public String toString() { 
		return "MyRectangle object with top left point (" + pTLC.getX() + " , " + pTLC.getY() + ") " + ", Width: " + 
		w + ", Height: " + h + ", Perimeter: " + perimeter() + ", Area: " + area() + ", and Color: " + color; 
	}
	
	public MyRectangle getMyBoundingRectangle() { return new MyRectangle(p,w,h,color); }
	
	public boolean pointInMyShape(MyPoint p) {
		if (p.getX() < pTLC.getX() || p.getX() > pTLC.getX() + w) return false;
		if (p.getY() < pTLC.getY() || p.getY() > pTLC.getY() + h) return false;
		return true;
	}
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		GC.fillRect(pTLC.getX(), pTLC.getY(), w, h);
	}
	@Override
	public void drawBoundary(GraphicsContext GC) {
		GC.setStroke(color.getJavaFXColor());
		GC.strokeRect(pTLC.getX(), pTLC.getY(), w, h);
	}
}
