// File: Main.java
package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import java.lang.Math;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.lang.IllegalStateException;
import java.util.NoSuchElementException;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.util.Date;	// For Console Logging


public class Main extends Application {
	String fileName, text = "";
	Boolean errors = false, doneOnce = false;
	Scanner input;
	int cw = 0, ch = 0, N = 0;
	HistogramAlphaBet.MyPieChart piechart;
	Canvas drawingCanvas;
	Scene drawCanvas;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Pane sP = new Pane(), p = new Pane();	// Starting Pane & Pane for Drawing Canvas
		BorderPane bP = new BorderPane();	// Drawing Canvas Border Pane
		GridPane grid = new GridPane(), dgrid = new GridPane();	// For the starting screen & drawing canvas update stuff
		HBox hb = new HBox();	// For the component to change the number of events to display on the drawing canvas 
		hb.setPadding(new Insets(5));	// Padding of 10 on all 4 sides
		hb.setSpacing(5); // Padding of 5 between items in hbox
		grid.setPadding(new Insets(10));	// Padding of 10 on all 4 sides
		dgrid.setPadding(new Insets(5));	// Padding of 10 on all 4 sides
		grid.getColumnConstraints().add(new ColumnConstraints(160));	// Column 1 Width
		grid.getColumnConstraints().add(new ColumnConstraints(350));	// Column 2 Width
		grid.setVgap(5);	// Gap between rows is 5	
		grid.setHgap(5);	// Gap between columns is 5
		dgrid.setVgap(5);	// Gap between rows is 5	
		dgrid.setHgap(5);	// Gap between columns is 5
				
		Label lF = new Label("Text File Location:");	// lF - label file
		Label lW = new Label("Canvas Width:");	// lW - label width
		Label lH = new Label("Canvas Height:");	// lH - label height
		Label lE = new Label("Number of Events: ");	// lE - label events
		Label error = new Label();
		Label fileError = new Label();
		error.setTextFill(MyColor.RED.getJavaFXColor());
		fileError.setTextFill(MyColor.RED.getJavaFXColor());
		GridPane.setConstraints(lF, 0, 0);	// 1st row & 1st column
		GridPane.setConstraints(lW, 0, 1);	// 2nd row & 1st column
		GridPane.setConstraints(lH, 0, 2);	// 3rd row & 1st column
		GridPane.setConstraints(lE, 0, 3);	// 4th row & 1st column
		GridPane.setConstraints(error, 1, 4);	// 5th row & 2nd column
		GridPane.setConstraints(fileError, 1, 5);	// 6th row & 2nd column
		
		TextField tFF = new TextField();	// tFF - text field file
		tFF.setPromptText("Text File Location");	
		TextField tFW = new TextField();	// tFW - text field width
		tFW.setPromptText("(Enter Positive Integer Value)");
		TextField tFH = new TextField();	// tFH - text field height
		tFH.setPromptText("(Enter Positive Integer Value)");
		TextField tFE = new TextField();	// tFE - text field events
		tFE.setPromptText("(Enter Positive Integer Value)");
		GridPane.setConstraints(tFF, 1, 0);	// 1st row & 2st column
		GridPane.setConstraints(tFW, 1, 1);	// 2nd row & 2st column
		GridPane.setConstraints(tFH, 1, 2);	// 3rd row & 2st column
		GridPane.setConstraints(tFE, 1, 3);	// 4st row & 2st column
		
		Button fileSelection = new Button("...");	// For file selection
		Button submit = new Button("Submit");	// For initializing the drawing canvas
		Button update = new Button("Update");	// For updating the number of slices to display
		Button selectNewFile = new Button("Select New Text File"); // To change the what file we want to view the pie chart for
		GridPane.setConstraints(fileSelection, 2, 0);	// 1th row & 3rd column
		GridPane.setConstraints(submit, 0, 4);	// 5th row & 1st column
		
		grid.getChildren().addAll(lF, lW, lH, lE, error, fileError, tFF, tFW, tFH, tFE, fileSelection, submit);	
		sP.getChildren().addAll(grid);
		primaryStage.setTitle("Assignment 3");
		Scene selection = new Scene(sP,575,200);
		primaryStage.setScene(selection);
		primaryStage.show();
		
		// File Chooser Button Action
		fileSelection.setOnAction((ActionEvent f) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().addAll( new ExtensionFilter("Text Files", "*.txt"));
			try { 
				File file = fileChooser.showOpenDialog(primaryStage);
				tFF.setText(file.toString());
			} catch (Exception err) { }	// If no files are selected, an error will occur
		});
		
		// Submit Button Action
		submit.setOnAction((ActionEvent s) -> {
			String errMessage = "Make sure all fields are filled & contain the correct information";
			// Canvas Width Text Field
			if (tFW.getText() != null && !tFW.getText().isEmpty()) {
				if (isInteger(tFW.getText())) { cw = Integer.parseInt(tFW.getText()); }
				else errors = true;
			} else { 
				error.setText(errMessage); 
				errors = true;
			}
			
			// Canvas Height Text Field
			if (tFH.getText() != null && !tFH.getText().isEmpty()) {
				if (isInteger(tFH.getText())) { ch = Integer.parseInt(tFH.getText()); }
				else errors = true;
			} else { 
				error.setText(errMessage); 
				errors = true;
			}
			
			// Number of Events Text Field
			if (tFE.getText() != null && !tFE.getText().isEmpty()) {
				if (isInteger(tFE.getText())) { N = Integer.parseInt(tFE.getText()); }
				else errors = true;
			} else { 
				error.setText(errMessage); 
				errors = true;
			}
			
			if (errors == true) error.setText(errMessage);
			
			// File Text Field
			if (tFF.getText() != null && !tFF.getText().isEmpty()) {
				// Remove the quotations from if we paste in the file location with "Copy as Path"
				fileName = tFF.getText().replaceAll("[\"]","");
				try {
					boolean success = openFile();
					fileError.setText("");
					// Checks if other fields contain errors given that the file was able to be opened
					if (success == true && errors == false && doneOnce == false) {
						// When drawing the pie chart for the first time
						readFile();
						closeFile();
						doneOnce = true;
						// Initializing Drawing Canvas
						drawingCanvas = addCanvas(cw,ch); 
						p.getChildren().add(drawingCanvas);
						error.setText("");
						hb.getChildren().addAll(lE, tFE); // Move these components to this position
						GridPane.setConstraints(hb, 0, 0);	// 1st row & 1st column
						GridPane.setConstraints(update, 1, 0); // 1st row & 2nd column
						GridPane.setConstraints(error, 2, 0); // 1st row & 3rd column
						GridPane.setConstraints(selectNewFile, 0, 1); // 2nd row & 1st column
						dgrid.getChildren().addAll(hb, update, error, selectNewFile);
						bP.setCenter(p);
						bP.setBottom(dgrid);
						drawCanvas = new Scene(bP, cw, ch + 75); // +75 to height for the component to change the number of slices
						primaryStage.setScene(drawCanvas);
						primaryStage.show();
					} else if (success == true && errors == false && doneOnce == true) {
						// For when we change the file
						readFile();
						closeFile();
						// Initializing Drawing Canvas
						GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
						gc.setFill(MyColor.WHITE.getJavaFXColor());
						gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight()); // Clear Canvas
						drawingCanvas = addCanvas(cw,ch); // Make a new canvas with the new data
						p.getChildren().add(drawingCanvas);
						error.setText("");
						hb.getChildren().addAll(lE, tFE); // Move these components back to this position
						GridPane.setConstraints(error, 2, 0); // 1st row & 3rd column
						GridPane.setConstraints(selectNewFile, 0, 1); // 2nd row & 1st column
						dgrid.getChildren().addAll(error); // Move these components back to this position
						primaryStage.setScene(drawCanvas);
						primaryStage.show();
					} else { errors = false; }	// Reset Errors Tracking (If success != true && errors == false)
					if (success == false) fileError.setText("Invalid File");
				} catch (Exception err){
					fileError.setText("Invalid File");
				}
			}  else { error.setText(errMessage); }
		});
		
		// Update Button Action
		update.setOnAction((ActionEvent s2) -> {
			// Number of Events Text Field
			if (tFE.getText() != null && !tFE.getText().isEmpty()) {
				if (isInteger(tFE.getText())) { 
					error.setText("");
					N = Integer.parseInt(tFE.getText()); 
					GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
					piechart.setN(N);	// Updating number of slices to display
					gc.setFill(MyColor.WHITE.getJavaFXColor());
					gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight()); // Clear canvas
					piechart.draw(gc);
				} else { error.setText(" Integer Value Required"); }
			} else { error.setText(" Integer Value Required"); }			
		});
		
		// selectNewFile Button Action
		selectNewFile.setOnAction((ActionEvent s3) -> {
			// Set Canvas to selection screen
			error.setText("");
			// Moving these components where they should be in the selection scene
			GridPane.setConstraints(lE, 0, 3);	// 4th row & 1st column
			GridPane.setConstraints(tFE, 1, 3);	// 4st row & 2st column
			GridPane.setConstraints(error, 1, 4);	// 5th row & 2nd column
			grid.getChildren().addAll(lE,tFE, error);
			errors = false;
			primaryStage.setScene(selection);
			primaryStage.show();
		});
	}
	
	public Canvas addCanvas(int w, int h) {
		Canvas cv = new Canvas(w,h);
		GraphicsContext gc = cv.getGraphicsContext2D();
		int radius = (int) ((double) 1 / 3 * Math.min(w, h));
		MyPoint pC = new MyPoint((int) w / 2, (int) h / 2);

		HistogramAlphaBet histogram = new HistogramAlphaBet(text);
		piechart = histogram.new MyPieChart(pC, N, radius);
		piechart.draw(gc);
		
		return cv;
	}

	public boolean isInteger(String s) {
		for (int i = 0; i < s.length(); i++) { if (Character.digit(s.charAt(i),10) < 0 ) return false; }
		return true;
	}
	
	public boolean openFile() {
		try {
			input = new Scanner(Paths.get(fileName));
			return true;
		} catch (IOException ioException) {
			System.err.println("File not found");
			return false;
		}
	}
	
	public void readFile() {
		try {
			while (input.hasNext()) { text += input.nextLine(); }
		} catch (NoSuchElementException elementException) {
			System.err.println("Invalid input! Terminating....");
		} catch (IllegalStateException stateException) {
			System.err.println("Error processing file! Terminating....");
		}
	}
	
	public void closeFile() { if (input != null) input.close(); }
	
	public static void main(String[] args) {
		System.out.println("-".repeat(76) + "\n\t\t-=-=-\t" + new Date() + "\t-=-=-\n");
		launch(args);
	}
}


