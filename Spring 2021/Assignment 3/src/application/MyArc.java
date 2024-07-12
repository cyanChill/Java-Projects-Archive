// File: MyArc.java
package application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType; // Values for the closure parameter in fillArc
import java.lang.Math;

public class MyArc extends MyShape {
	MyPoint pCenter, eP1, eP2;
	int a, b;
	double startAngle, dAngle, rAngle;	// We treat degrees as the angle unit in an oval
	MyColor color;
	MyOval O;
	
	// Takes in center point, the abscissas a and b, starting angle (in degrees), arc angle (in degrees), and color
	MyArc(MyPoint center, int a, int b, double startingAngle, double angle, MyColor color) {
		super(center, color);
		this.pCenter = center;
		this.a = a;
		this.b = b;
		this.startAngle = startAngle(startingAngle);
		this.dAngle = arcAngle(angle);
		this.rAngle = Math.toRadians(dAngle);
		this.color = color;
		this.eP1 = endPoint(startAngle);
		this.eP2 = endPoint(startAngle + dAngle);
		this.O = new MyOval(center, a, b, color);
	}
	
	// Takes in an oval, the 2 endpoints of the arc, and color
	MyArc(MyOval O, MyPoint eP1, MyPoint eP2, MyColor color) {
		super(eP1, color);
		this.pCenter = O.getCenter();
		this.a = O.getA();
		this.b = O.getB();
		this.eP1 = eP1;
		this.eP2 = eP2;
		this.O = O;
		this.startAngle = angleFrom0Deg(eP1);
		this.dAngle = arcAngle(eP1, eP2);
		this.rAngle = Math.toRadians(dAngle);
		this.color = color;
	}
	
	public int getA() { return a; }
	public int getB() { return b; }
	public MyPoint getCenter() { return pCenter; }
	// Arc Length = PI/2sqrt(2) * chord length (From "ARC LENGTH of an ELLIPTICAL CURVE" (Mohammad Farooque Khan), IJSRP, Volume 3, Issue 8, August 2013)
	// However, doesn't work for arc angles greater than 180° because the chord will end up reverting back to 0
	// after reaching it's max length at 180°
	public double getArcLength() { 
		if (dAngle <= 180) return (double) 0.5 * Math.PI / Math.sqrt(2) * eP1.distance(eP2);
		return (double) O.perimeter() - (0.5 * Math.PI / Math.sqrt(2) * eP1.distance(eP2)); 
	}
	public double getArcAngle() { return dAngle; }
	public double getStartAngle() { return startAngle; }
	public MyPoint[] getEndPoints() {
		MyPoint[] ePs = new MyPoint[2];
		ePs[0] = eP1;
		ePs[1] = eP2;
		return ePs;
	}

	// Method to calculate the endpoint of the arc
	// Formula from: https://math.stackexchange.com/questions/493104/evaluating-int-ab-frac12-r2-mathrm-d-theta-to-find-the-area-of-an-ellips/687384#687384
	public MyPoint endPoint(double angle) {
		if (angle > 360) angle -= 360;
		int x= 0, y = 0, xc = pCenter.getX(), yc = pCenter.getY();
		if (angle == 90) {
			x = xc;
			y = yc + b;
		} else if (angle == 270) {
			x = xc;
			y = yc - b;
		} else if ((angle >= 0 && angle < 90) || (angle > 270 && angle <= 360)) {
			x = (int) Math.round(xc + (double) a / Math.sqrt((Math.pow((double) a * Math.tan(Math.toRadians(angle)), 2) / Math.pow(b, 2)) + 1));
			y = (int) Math.round(yc + (double) (a * Math.tan(Math.toRadians(angle))) / Math.sqrt((Math.pow((double) a * Math.tan(Math.toRadians(angle)), 2) / Math.pow(b, 2)) + 1));
		} else if (angle > 90 && angle < 270) {
			x = (int) Math.round(xc - (double) a / Math.sqrt((Math.pow((double) a * Math.tan(Math.toRadians(angle)), 2) / Math.pow(b, 2)) + 1));
			y = (int) Math.round(yc - (double) (a * Math.tan(Math.toRadians(angle))) / Math.sqrt((Math.pow((double) a * Math.tan(Math.toRadians(angle)), 2) / Math.pow(b, 2)) + 1));
		}
		return new MyPoint(x,y, MyColor.getRandomColor());
	};
	
	// Method to calculate the starting angle from an endpoint
	public double angleFrom0Deg(MyPoint eP) {
		double angle = Math.toDegrees(Math.atan2((double) eP.getY() - pCenter.getY(), eP.getX() - pCenter.getX()));
		if (angle < 0) { angle += 360; }
		return angle;
	}
	
	// Making sure the start angle value is a friendly value
	public double startAngle(double startingAngle) {
		while (startAngle > 360) { startingAngle -= 360; }
		while (startAngle < 0) { startingAngle += 360; }
		return startingAngle;
	}
	
	// Making sure the arc angle value is a friendly value
	public double arcAngle(double angle) {
		if (angle > 360 || angle < -360) angle = 360;
		else if (angle < 0) {
			angle = -angle;
			this.startAngle = startAngle(startAngle - angle);
		}
		return angle;
	}

	// Method to calculate the arc angle from 2 endpoints
	public double arcAngle(MyPoint eP1, MyPoint eP2) {
		double startAngle = angleFrom0Deg(eP1);
		double endAngle = angleFrom0Deg(eP2);
		double arcAngle = endAngle - startAngle;
		if (startAngle > endAngle) arcAngle = 360 - startAngle + endAngle;
		if (startAngle == endAngle) arcAngle = 360;
		return arcAngle;
	}
	
	@Override
	// Since we define the area of an arc as a slice, the perimeter is the arc length plus the side lengths of the slice
	public double perimeter() { return getArcLength() + eP1.distanceFromOrigin() + eP2.distanceFromOrigin(); }
	
	@Override
	// From "The Area of Intersecting Ellipses" (David Eberly) September 2008
	public double area() {
		double rStartAngle = Math.toRadians(startAngle), rEndAngle = Math.toRadians(angleFrom0Deg(eP2));
		double bPa = (double) (b + a), bMa = (double) (b - a);
		
		return (double) (0.5 * a * b * (rAngle - (Math.atan((bMa * Math.sin(2.0 * rEndAngle)) / (bPa + bMa * Math.cos(2.0 * rEndAngle))) -
						 Math.atan((bMa * Math.sin(2.0 * rStartAngle)) / (bPa + bMa * Math.cos(2.0 * rStartAngle))))));
	}
	
	
	@Override
	public String toString() {
		return "MyArc object centered at (" + pCenter.getX() + ", " + pCenter.getY() + "), Oval Width: " 
			   + 2 * a + ", Oval Height: " + 2 * b + ", ArcLength: " + getArcLength() + ", Starting Angle: " 
			   + startAngle + ", Arc Angle: " + dAngle + "°, Area: "+ area() + ", Perimeter: " + 
			   perimeter() + ", and Color: " + color; 
	}
	
	@Override
	// The arc is on an oval so the bounding rectangle of the arc is the bounding rectangle of the oval
	public MyRectangle getMyBoundingRectangle() { return O.getMyBoundingRectangle(); }

	@Override 
	// Since the arc is considered the slice in an oval, we can check to see if the point is inside the oval and check to see
	// if it's angle is between the starting angle and ending angle of the arc
	public boolean pointInMyShape(MyPoint p) {
		boolean inOval = O.pointInMyShape(p); 
		double pAngle = angleFrom0Deg(p);
		
		return inOval && pAngle >= startAngle && pAngle <= startAngle + dAngle;
	}
	
	@Override
	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		// The angle values goes clockwise with respect to the way the way the coordinates are on the canvas
		GC.fillArc(pCenter.getX() - a, pCenter.getY() - b, 2 * a, 2 * b, -startAngle, -dAngle, ArcType.ROUND);
	}
	
	// This will draw the arc (the curve and not fill the inside)
	 public void drawArc(GraphicsContext GC) {
	 	GC.setStroke(color.getJavaFXColor());
	 	//The angle values goes clockwise with respect to the way the way the coordinates are on the canvas
	 	GC.strokeArc(pCenter.getX() - a, pCenter.getY() - b, 2 * a, 2 * b, -startAngle, -dAngle, ArcType.OPEN);
	 }
	
	@Override
	public void drawBoundary(GraphicsContext GC) {
		GC.setStroke(color.getJavaFXColor());
		MyRectangle bound = getMyBoundingRectangle();
		GC.strokeRect(bound.getTLC().getX(), bound.getTLC().getY(), bound.getWidth(), bound.getHeight());
	}
}
