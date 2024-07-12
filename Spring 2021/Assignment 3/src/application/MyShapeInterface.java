// File: MyShapeInterface.java
package application;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;

public interface MyShapeInterface {

	public abstract MyRectangle getMyBoundingRectangle();
	public abstract boolean pointInMyShape(MyPoint p);
	
	public static MyRectangle rectangleIntersection(MyRectangle r1, MyRectangle r2) {
		int x1 = r1.getTLC().getX();
		int y1 = r1.getTLC().getY();
		int w1 = r1.getWidth();
		int h1 = r1.getHeight();
		
		int x2 = r2.getTLC().getX();
		int y2 = r2.getTLC().getY();
		int w2 = r2.getWidth();
		int h2 = r2.getHeight();
		
		if (x1 < x2 - w1 || x1 > x2 + w2) return null;
		if (y1 < y2 - h1 || y1 > y2 + h2) return null;
		
		// (xTLC,yTLC) is TLC point of intersecting rectangle while (xBRC,yBRC) is BRC point of intersecting rectangle
		int xTLC = Math.max(x1, x2);
		int yTLC = Math.max(y1, y2);
		int xBRC = Math.min(x1 + w1, x2 + w2);
		int yBRC = Math.min(y1 + h1, y2 + h2);
		
		MyPoint p = new MyPoint(xTLC, yTLC);
		return new MyRectangle(p, xBRC - xTLC, yBRC - yTLC, MyColor.getRandomColor());
	}
	
	public static MyRectangle intersectMyShapes(MyShape s1, MyShape s2) {
		MyRectangle r1 = s1.getMyBoundingRectangle();
		MyRectangle r2 = s2.getMyBoundingRectangle();
		return rectangleIntersection(r1, r2);
	}
	
	// Returns the set of points of the intersection of 2 MyShape objects
	public static List<MyPoint> shapeIntersection(MyShape s1, MyShape s2) {
		MyRectangle r1 = s1.getMyBoundingRectangle();
		MyRectangle r2 = s2.getMyBoundingRectangle();
		MyRectangle r = rectangleIntersection(r1, r2);
		
		if (r == null) return null;
		int x = r.getTLC().getX();
		int y = r.getTLC().getY();
		int w = r.getWidth();
		int h = r.getHeight();
		
		List<MyPoint> intersect = new ArrayList<MyPoint>();
		for (int i = 0; i < w; i++) {
			int xi = x + i;
			for (int j = 0; j < h; j++) {
				MyPoint p = new MyPoint(xi, y + j);
				if (s1.pointInMyShape(p) && s2.pointInMyShape(p)) {
					p.setColor(MyColor.RED);
					intersect.add(p);
				}
			}
		}
		return intersect;
	}
	
	// Draws the intersection area of 2 MyShape objects
	public default Canvas drawShapeIntersction(MyShape s1, MyShape s2) {
		List<MyPoint> intersect = shapeIntersection(s1, s2);
		
		if (intersect == null) return null;
		Canvas cv = new Canvas();
		GraphicsContext gc = cv.getGraphicsContext2D();
		
		for (MyPoint p : intersect) {
			if (cv.getWidth() < p.getX()) cv.setWidth(p.getX() + 5);
			if (cv.getHeight() < p.getY()) cv.setHeight(p.getY() + 5);
			p.draw(gc);
		}
		
		return cv;
	}
	
}
