// File: HistogramAlphaBet.java
package application;

import javafx.scene.canvas.GraphicsContext;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.stream.Collectors;

public class HistogramAlphaBet {
	Map<Character, Integer> frequency = new HashMap<Character, Integer>();
	
	HistogramAlphaBet() { }
	HistogramAlphaBet(Map<Character,Integer> map) { frequency.putAll(map); }
	HistogramAlphaBet(String text) {
		String letters = text.replaceAll("[^a-zA-Z]", "").toLowerCase();
		
		for (int i = 0; i < letters.length(); i++) {
			Character key = letters.charAt(i);
			frequency.putIfAbsent(key, 0);
			frequency.put(key, frequency.get(key) + 1);
		}	
	}
	
	public Map<Character, Integer> getFrequency() { return frequency; }
	// Frequency of all events
	public Integer getCumulativeFrequency() { return frequency.values().stream().reduce(0, Integer::sum); }
	
	// Returns a Frequency Map sorted from lowest value to highest value (increasing order)
	public Map<Character, Integer> sortUpFrequency() {
		return frequency.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}
	
	// Returns a Frequency Map sorted from highest value to lowest value (decreasing order)
	public Map<Character, Integer> sortDownFrequency() {
		return frequency.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	
	@Override
	public String toString() {
		String output = "Frequency of Characters\n\n";
		for (Character K : frequency.keySet()) { output += K + ": " + frequency.get(K) + "\n"; }
		return output;
	}
	
	class MyPieChart {
		Map<Character, Double> probability = new HashMap<Character, Double>();
		Map<Character, Slice> pieChart = new HashMap<Character, Slice>();
		int N, radius;
		MyPoint pCenter;
		
		MyPieChart(MyPoint p, int n, int r) {
			this.pCenter = p;
			this.radius = r;
			probability = getProbability();
			// -90 because we want the pieChart to start from above the center (and not from the right)
			pieChart = getMyPieChart(-90);
			setN(n);
		}
		
		public MyPoint getCenter() { return pCenter; }
		public int getRadius() { return radius; }
		public int getN() { return N; }
		public void setN(int n) { 
			if (n > probability.keySet().size()) this.N = probability.keySet().size();
			else this.N = n;
		}
		public Double getSumofProbability() { return probability.values().stream().reduce(0.0, Double::sum); }
		
		// Returns a Probability Map sorted from highest value to lowest value (decreasing order)
		public Map<Character, Double> getProbability() {
			for (Character key : frequency.keySet()) {
				probability.put(key, (double) frequency.get(key) / getCumulativeFrequency());
			}
			return probability.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
					.collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		}
		
		// Creates a map with slices of a pie chart representing the probabilities of all the keys ordered by the keys
		public Map<Character, Slice> getMyPieChart(double startingAngle) {
			while (startingAngle > 360) { startingAngle = startingAngle - 360; }
			while (startingAngle < -360) { startingAngle = startingAngle + 360; }
			Object[] keyArray = probability.keySet().toArray();
			MyColor[] usedColors = new MyColor[probability.keySet().size()];
			usedColors[0] = MyColor.getRandomColor();
			pieChart.put((char) keyArray[0], new Slice(pCenter, radius, startingAngle, 360 * probability.get(keyArray[0]), usedColors[0], keyArray[0].toString()));
			double startAngle = startingAngle + 360 * probability.get(keyArray[0]);
			for (int i = 1; i < probability.keySet().size(); i++) {
				usedColors[i] = MyColor.getRandomColor();
				// To make sure all slices have a unique color
				while (isUsedColor(i, usedColors,usedColors[i]) == true) { usedColors[i] = MyColor.getRandomColor(); }
				// Gets the arcAngle in relation to it's frequency
				double angle = 360 * probability.get(keyArray[i]);
				pieChart.put((char) keyArray[i], new Slice(pCenter, radius, startAngle, angle, usedColors[i], keyArray[i].toString()));
				startAngle += angle;
			}
			return pieChart;
		}
		
		public boolean isUsedColor(int index, MyColor[] usedArray, MyColor color) {
			for (int i = 0; i < index; i++) { if (usedArray[i] == color) return true; }
			return false;
		}
		
		public void draw(GraphicsContext gc) {
			// An array of the keys based on the decreasing order of their value (largest to smallest)
			Object[] keyArray = probability.keySet().toArray();
			for (int n = 0; n < N; n++) {
				pieChart.get(keyArray[n]).draw(gc);
				pieChart.get(keyArray[n]).drawText(gc);
			}
			// Case if "All Other Letters" exist
			if (N < probability.keySet().size()) {
				MyColor finalColor = pieChart.get(keyArray[0]).getColor();	// If N = 0
				if (N != 0) finalColor = pieChart.get(keyArray[N]).getColor();
				double restStartAngle = pieChart.get(keyArray[0]).getStartAngle();	// If N = 0
				if (N != 0) restStartAngle = pieChart.get(keyArray[N-1]).getStartAngle() + pieChart.get(keyArray[N-1]).getArcAngle();
				double restArcAngle = 360 + pieChart.get(keyArray[0]).getStartAngle() - restStartAngle;	// Arc angle of the slice containing the remaining letters
				if (restArcAngle > 360) restArcAngle = restArcAngle - 360;
				Slice lastSlice = new Slice(pCenter, radius, restStartAngle, restArcAngle, finalColor, "Other");
				lastSlice.draw(gc);
				lastSlice.drawText(gc);
			}
		}
	}
}
