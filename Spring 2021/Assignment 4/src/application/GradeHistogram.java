// File: GradeHistogram.java
package application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import java.util.Map;
import java.util.LinkedHashMap;

public class GradeHistogram {
	Map<Character, Integer> frequency = new LinkedHashMap<Character, Integer>();
	String freqLabel;

	GradeHistogram(Map<Character,Integer> map, String s) { updateFrequencyMap(map, s); }
	
	public Map<Character, Integer> getFrequency() { return frequency; }
	
	// Frequency of all events
	public Integer getCumulativeFrequency() { return frequency.values().stream().reduce(0, Integer::sum); }
	
	@Override
	public String toString() {
		String output = "Frequency of Grades\n\n";
		for (Character K : frequency.keySet()) { output += K + ": " + frequency.get(K) + "\n"; }
		return output;
	}
	
	public void updateFrequencyMap(Map<Character,Integer> map, String s) { 
		frequency = map; 
		this.freqLabel = s;
	}
	
	class MyPieChart {
		Map<Character, Double> probability = new LinkedHashMap<Character, Double>();
		Map<Character, Slice> pieChart = new LinkedHashMap<Character, Slice>();
		int radius;
		MyPoint pCenter;
		
		MyPieChart(MyPoint p, int r) {
			this.pCenter = p;
			this.radius = r;
			probability = getProbability();
			// -90 because we want the pieChart to start from above the center (and not from the right)
			pieChart = getMyPieChart(-90);
		}
		
		public MyPoint getCenter() { return pCenter; }
		public int getRadius() { return radius; }
		public Double getSumofProbability() { return probability.values().stream().reduce(0.0, Double::sum); }
		
		// Returns a Probability Map sorted by the key
		public Map<Character, Double> getProbability() {
			for (Character key : frequency.keySet()) {
				probability.put(key, (double) frequency.get(key) / getCumulativeFrequency());
			}
			return probability;
		}
		
		public void updateContents() {
			getProbability();
			updatePChart(-90);
		}
		
		// Creates a map with slices of a pie chart representing the probabilities of all the keys ordered by the keys
		public Map<Character, Slice> getMyPieChart(double startingAngle) {
			while (startingAngle > 360) { startingAngle = startingAngle - 360; }
			while (startingAngle < -360) { startingAngle = startingAngle + 360; }
			Object[] keyArray = probability.keySet().toArray();
			MyColor[] usedColors = new MyColor[probability.keySet().size()];
			usedColors[0] = MyColor.getRandomColor();
			pieChart.put(String.valueOf(keyArray[0]).charAt(0), new Slice(pCenter, radius, startingAngle, 360 * probability.get(keyArray[0]), usedColors[0],
					String.valueOf(keyArray[0]) + ", " + frequency.get(keyArray[0])));
			double startAngle = startingAngle + 360 * probability.get(keyArray[0]);
			for (int i = 1; i < probability.keySet().size(); i++) {
				usedColors[i] = MyColor.getRandomColor();
				// To make sure all slices have a unique color
				while (isUsedColor(i, usedColors,usedColors[i]) == true) { usedColors[i] = MyColor.getRandomColor(); }
				// Gets the arcAngle in relation to it's frequency
				double angle = 360 * probability.get(keyArray[i]);
				if (String.valueOf(keyArray[i]).charAt(0) != 'N') pieChart.put(String.valueOf(keyArray[i]).charAt(0), new Slice(pCenter, radius, startAngle, angle, usedColors[i],
						                                             String.valueOf(keyArray[i]) + ", " + frequency.get(keyArray[i])));
				// 'N' denotes "No Grade"
				else pieChart.put(String.valueOf(keyArray[i]).charAt(0), new Slice(pCenter, radius, startAngle, angle, usedColors[i], "No Grade, " + frequency.get(keyArray[i])));
				startAngle += angle;
			}
			return pieChart;
		}
		
		// Updates the pie chart contents
		private void updatePChart(double startingAngle) {
			while (startingAngle > 360) { startingAngle = startingAngle - 360; }
			while (startingAngle < -360) { startingAngle = startingAngle + 360; }
			Object[] keyArray = probability.keySet().toArray();
			
			Slice s = pieChart.get(keyArray[0]);
			s.setStartAngle(startingAngle);
			s.setArcAngle((double) 360 * probability.get(keyArray[0]));
			s.setLabel(String.valueOf(keyArray[0]) + ", " + frequency.get(keyArray[0]));
			
			double startAngle = startingAngle + 360 * probability.get(keyArray[0]);
			for (int i = 1; i < probability.keySet().size(); i++) {
				double angle = 360 * probability.get(keyArray[i]);
				s = pieChart.get(keyArray[i]);
				s.setStartAngle(startAngle);
				s.setArcAngle(angle);
				if (String.valueOf(keyArray[i]).charAt(0) != 'N') s.setLabel(String.valueOf(keyArray[i]) + ", " + frequency.get(keyArray[i]));
				else s.setLabel("No Grade, " + frequency.get(keyArray[i]));	// 'N' denotes "No Grade"
				startAngle += angle;
			}
		}
		
		private boolean isUsedColor(int index, MyColor[] usedArray, MyColor color) {
			for (int i = 0; i < index; i++) { if (usedArray[i] == color) return true; }
			return false;
		}
		
		public void draw(GraphicsContext gc) {
			for (Character s : probability.keySet()) { pieChart.get(s).draw(gc); }
			drawLegend(gc);
		}
		
		// Draw the legend for the pie chart
		private void drawLegend(GraphicsContext gc) {
			double distanceScale = 1.0 / 30.0 * Math.min(gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
			// gc.getCanvas().getWidth()
			gc.setFont(new Font(distanceScale));	// Font of the slice label scales with the canvas size
			Slice s1 = pieChart.get(probability.keySet().toArray()[0]);
			double x = s1.getCenter().getX() + s1.getRadius() + 20, y = 20;
			if (gc.getCanvas().getWidth() < (double) (x + 0.65 * distanceScale * freqLabel.length())) {
				gc.getCanvas().setWidth(x + 0.65 * distanceScale * freqLabel.length());;
			}
			gc.setFill(MyColor.BLACK.getJavaFXColor());
			gc.fillText(freqLabel, x + distanceScale + 5, y + distanceScale);	// Pie chart label
			y = y + distanceScale + 5;
			// Go through the keys objects
			for (Character s : probability.keySet()) {
				Slice sl = pieChart.get(s);
				double sliceProb = probability.get(s);
				if (Double.isNaN(sliceProb)) sliceProb = 0;
				String label = sl.getLabel() + " (" + String.valueOf(sliceProb * 100) + "%)";
				if (String.valueOf(sliceProb * 100).length() > 5) label = sl.getLabel() + " (" + String.valueOf(sliceProb * 100).substring(0,5) + "%)";
				MyColor sliceColor = sl.getColor();
				
				// If legend goes off the canvas
				if (gc.getCanvas().getWidth() < (double) (x + 0.65 * distanceScale * label.length())) {
					gc.getCanvas().setWidth(x + 0.65 * distanceScale * label.length());;
				}
				gc.setFill(sliceColor.getJavaFXColor());
				gc.fillText(label, x + distanceScale + 5, y + distanceScale);
				y = y + distanceScale + 5;
			}	
		}
		
	}
}
