// File: Slice.java
package application;
import java.lang.Math;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

public class Slice {
	MyPoint pCenter;
	int radius;
	double startAngle, degAngle, radAngle;
	MyColor color;
	String label;

	// Takes in center point, radius, starting angle (in degrees), angle (in degrees), and color
	Slice(MyPoint center, int r, double startingAngle, double angle, MyColor color, String s) {
		this.pCenter = center;
		this.radius = r;
		this.startAngle = startAngle(startingAngle);
		this.degAngle = arcAngle(angle);
		this.radAngle = Math.toRadians(degAngle);
		this.color = color;
		this.label = s;
	}

	public int getRadius() { return radius; }
	public MyPoint getCenter() { return pCenter; }
	public double getArcLength() { return radius * radAngle; }
	public double getArcAngle() { return degAngle; }
	public double getStartAngle() { return startAngle; }
	public MyColor getColor() { return color; }
	public String getLabel() { return label; }
	
	public void setLabel(String l) { this.label = l; }
	public void setStartAngle(double a) { this.startAngle = startAngle(a); }
	public void setArcAngle(double a) {
		this.degAngle = arcAngle(a);
		this.radAngle = Math.toRadians(degAngle);
	}
	
	// Making sure the start angle value is a friendly value
	private double startAngle(double startingAngle) {
		while (startingAngle > 360) { startingAngle -= 360; }
		while (startingAngle < 0) { startingAngle += 360; }
		return startingAngle;
	}
	
	// Making sure the arc angle value is a friendly value
	private double arcAngle(double angle) {
		if (angle > 360 || angle < -360) angle = 360;
		else if (angle < 0) {
			angle = -angle;
			this.startAngle = startAngle(startAngle - angle);
		}
		return angle;
	}
	
	// The perimeter is the arcLength + 2 * radius
	public double perimeter() { return getArcLength() + 2 * radius; }
	// The area is 1/2 * radius^2 * arcAngle (in radians)
	public double area() { return 0.5 * radAngle * Math.pow(radius, 2); }

	@Override
	public String toString() { 
		return "Slice object centered at (" + pCenter.getX() + ", " + pCenter.getY() + ") representing event " + label 
		+ ", Radius: " + radius + ", Area: " + area() + ", Perimeter: " + perimeter() + ", ArcLength: " + getArcLength() 
		+ ", Start Angle: " + getStartAngle() + "°, ArcAngle: " + degAngle + "°, and Color: " + color; 
	}

	public void draw(GraphicsContext GC) {
		GC.setFill(color.getJavaFXColor());
		// The angle values goes clockwise with respect to the way the way the coordinates are on the canvas
		GC.fillArc(pCenter.getX() - radius, pCenter.getY() - radius, 2 * radius, 2 * radius, -startAngle, -degAngle, ArcType.ROUND);
	}
	
	public void drawText(GraphicsContext gc) {
		// Get the angle where the middle of the slice will be
		double sliceCenter = Math.toRadians(startAngle + 0.5 * degAngle);
		while (sliceCenter > 2 * Math.PI) { sliceCenter = sliceCenter - 2 * Math.PI; } 
		// Getting the probability from the arc angle in percentage (ArcAngle = 360 * probability of key (decimal)); 5/18 is 100/360
		double sliceProbability = degAngle * 5 / 18;
		String str;
		if (String.valueOf(sliceProbability).length() > 5) str = label + " (" + String.valueOf(sliceProbability).substring(0,5) + "%)";
		else str = label + " (" + String.valueOf(sliceProbability) + "%)";
		int x = 0, y = 0;
		double distanceScale = 1.0 / 48.0 * Math.min(gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		if ((-0.5 * Math.PI < sliceCenter && sliceCenter < 0.5 * Math.PI) || 1.5 * Math.PI < sliceCenter) {
			x = (int) (pCenter.getX() + (radius + distanceScale) * Math.cos(sliceCenter));
			y = (int) (pCenter.getY() + (radius + distanceScale) * Math.sin(sliceCenter));
		} else if (0.5 * Math.PI < sliceCenter && sliceCenter < 1.5 * Math.PI) {
			x = (int) (pCenter.getX() + (radius + distanceScale) * Math.cos(sliceCenter) - 0.48 * distanceScale * str.length());
			y = (int) (pCenter.getY() + (radius + distanceScale) * Math.sin(sliceCenter));
		} else if (sliceCenter == -0.5 * Math.PI) {
			x = (int) (pCenter.getX() - 0.2 * distanceScale * str.length());
			y = (int) (pCenter.getY() - radius - distanceScale);
		} else if (sliceCenter == 0.5 * Math.PI) {
			x = (int) (pCenter.getX() - 0.2 * distanceScale * str.length());
			y = (int) (pCenter.getY() + radius + distanceScale);
		} 
		gc.setFont(new Font(distanceScale));	// Font of the slice label scales with the canvas size
		gc.setFill(MyColor.BLACK.getJavaFXColor());
		gc.fillText(str, x, y);
	}
	
}
