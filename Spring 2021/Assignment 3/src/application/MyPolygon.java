// File: MyPolygon.java
package application;
import javafx.scene.canvas.GraphicsContext;
import java.lang.Math;

public class MyPolygon extends MyShape{
	int N, r;
	MyPoint pCenter;
	double [][] vertices;
	MyColor color;
	
	// Inputs are center point, radius, number of sides, and color (Polygon is inscribed in circle with radius: r)
	MyPolygon(MyPoint p, int N, int r, MyColor color) {
		super(p,color);
		this.pCenter = p;
		this.N = N;
		this.r = r;
		this.color = color;
		this.vertices = getVertices(p, N, r);
	}
	
	public MyPoint getCenter() { return pCenter; }
	public double getAngle() { return (180 * (N - 2)) / N; }
	
	public double getSide() { 
		double dx = vertices[0][1] - vertices[0][0];
		double dy = vertices[1][1] - vertices[1][0];
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	}

	public double [][] getVertices(MyPoint p, int n, int radius) {
		double [][] coords = new double[2][n];
		int x, y;
		double angle = 360 / n;
		for (int i = 0; i < n; i++) {
			x = (int) (p.getX() - radius * Math.sin(Math.toRadians((i + 1) * angle)));
			y = (int) (p.getY() - radius * Math.cos(Math.toRadians((i + 1) * angle)));
			coords[0][i] = x;
			coords[1][i] = y;
		}
		return coords;
	}
	
	@Override
	public double area() { return 0.25 * N * Math.pow(getSide(), 2) * (1 / Math.tan(Math.PI / N)); }
	@Override
	public double perimeter() { return N * getSide(); }
	
	@Override
	public String toString() { 
		return "MyPolygon object with center point (" + pCenter.getX() + " , " + pCenter.getY() + ") " + ", Number of Sides: " + N + ", Side Length: " + getSide() + 
		", Interior Angle: "  + getAngle() + "° (Total Interior Angle: " + getAngle() * N + "°), Perimeter: " + perimeter() + ", Area: " + area() + ", and Color: " + color; 
	}
	
	// Specifically for use in pointInMyShape
	public boolean rayIntercept(MyPoint p, MyLine side) { // CURRENTLY DOESN"T WORK
		int y;
		MyPoint sp1 = side.getLine()[0];
		MyPoint sp2 = side.getLine()[1];
		double slope = (double) (sp2.getY() - sp1.getY())/(sp2.getX() - sp1.getX());
		double intercept = (double) (sp1.getY() - (slope * sp1.getX()));
		y = (int) ((slope * p.getX()) + intercept);
		// For negative slopes, if the y from the intercept equation is greater than the point's y; 
		// there will be an intercept. The opposite happens for when the slope is positive
		if (slope < 0) { return p.getY() < y; } 
		else return p.getY() > y;
	}
	
	@Override
	public MyRectangle getMyBoundingRectangle() {
		MyPoint pTLC = new MyPoint(pCenter.getX() - r, pCenter.getY() - r);
		return new MyRectangle(pTLC, 2 * r, 2 * r, MyColor.getRandomColor());
	}
	
	@Override
	public boolean pointInMyShape(MyPoint p) {
		// Check if point is in bounding rectangle (might not be necessary depending on how we find out)
		MyRectangle boundRec = getMyBoundingRectangle();
		if (boundRec.pointInMyShape(p) == false) return false;
		// Ray method: draw a ray from the point and see how many times it intersects the edges
		int intersection = 0;	// Tracking variable
		MyLine side;
		MyPoint v1, v2;
		
		for (int i = 0; i < N; i++) {
			if (i + 1 == N) {
				v1 = new MyPoint((int) vertices[0][i],(int) vertices[1][i]);
				v2 = new MyPoint((int) vertices[0][0],(int) vertices[1][0]);
				side = new MyLine(v1, v2, color);
			} else {
				v1 = new MyPoint((int) vertices[0][i],(int) vertices[1][i]);
				v2 = new MyPoint((int) vertices[0][i + 1],(int) vertices[1][i + 1]);
				side = new MyLine(v1, v2, color);
			}
			// Check if the ray could go through line segment (if between the y values of segment)
			if (v1.getY() >= v2.getY() && (p.getY() > v1.getY() || p.getY() < v2.getY())) continue;
			if (v1.getY() <= v2.getY() && (p.getY() < v1.getY() || p.getY() > v2.getY())) continue;
			// Check if the ray could go through line segment (if before biggest x of segment)
			if (v1.getX() >= v2.getX() && p.getX() > v1.getX()) continue;
			if (v1.getX() <= v2.getX() && p.getX() > v2.getX()) continue;
			
			// Check if point is on the line
			if (side.pointInMyShape(p) == true) return true;
			// Check to see if the ray would intercept the side
			if (rayIntercept(p, side) == true) intersection++;
		}
		// Case for odd polygon: if point is on the y-level of the bottom 2 vertices
		if (N % 2 == 1 && p.getY() == vertices[1][(int) N/2]) { return false; }
		if (intersection % 2 == 1) return true;
		else return false;
	}
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		GC.fillPolygon(vertices[0], vertices[1], N);
	}
	@Override
	public void drawBoundary(GraphicsContext GC) {
		GC.setStroke(color.getJavaFXColor());
		MyRectangle bound = getMyBoundingRectangle();
		GC.strokeRect(bound.getTLC().getX(), bound.getTLC().getY(), bound.getWidth(), bound.getHeight());
	}
}
