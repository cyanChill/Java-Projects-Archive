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
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.text.Font;
import javafx.scene.control.PasswordField;
import java.util.Date;	// For Console Logging

public class Main extends Application {
	String fileLocation;
	Boolean errors = false;
	Scanner input;
	int cw = 0, ch = 0;
	GradeHistogram histogram;
	GradeHistogram.MyPieChart piechart;
	Canvas drawingCanvas;
	Scene drawCanvas;
	DBQueries dbq;
	Map<Character,Integer> gradeFreq;
	String freqLabel = "Grade Distribution for All Classes"; // Either "Grade Distribution for All Classes" or "Grade Distribution for 22100 Sections" 
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Pane sP = new Pane(), p = new Pane();	// Starting Pane & Pane for Drawing Canvas
		BorderPane bP = new BorderPane();	// Drawing Canvas Border Pane
		GridPane grid = new GridPane(), cgrid = new GridPane(), qgrid = new GridPane();	// For the starting screen & view table buttons & updating query
		HBox hb = new HBox();	// For the component to update a grade of a student
		VBox vb = new VBox();
		hb.setPadding(new Insets(10));	// Padding of 10 on all 4 sides
		vb.setPadding(new Insets(5));	// Padding of 10 on all 4 sides
		grid.setPadding(new Insets(10));	// Padding of 10 on all 4 sides
		grid.getColumnConstraints().add(new ColumnConstraints(255));	// Column 1 Width
		grid.getColumnConstraints().add(new ColumnConstraints(350));	// Column 2 Width
		grid.setVgap(5);	// Gap between rows is 5	
		grid.setHgap(5);	// Gap between columns is 5
				
		// Initial Screen Related Components
			// Text File Input Component
				Label lF = new Label("Enter scheduleSpring2021.txt Text File Location:");
				GridPane.setConstraints(lF, 0, 0);	// 1st row & 1st column
				TextField tFF = new TextField();
				// TODO: Put path to `springSchedule2021.txt` file between the two "".
				tFF.setText("");
				tFF.setPromptText("(scheduleSpring2021.txt Text File Location)");
				GridPane.setConstraints(tFF, 1, 0);	// 1st row & 2st column
			// Canvas Width Input Component
				Label lW = new Label("Canvas Width:");
				GridPane.setConstraints(lW, 0, 1);	// 2nd row & 1st column
				TextField tFW = new TextField();
				tFW.setText("500");
				tFW.setPromptText("(Enter Positive Integer Value)");
				GridPane.setConstraints(tFW, 1, 1);	// 2nd row & 2st column
			// Canvas Height Input Component
				Label lH = new Label("Canvas Height:");	
				GridPane.setConstraints(lH, 0, 2);	// 3rd row & 1st column
				TextField tFH = new TextField();	
				tFH.setPromptText("(Enter Positive Integer Value)");
				tFH.setText("500");
				GridPane.setConstraints(tFH, 1, 2);	// 3rd row & 2st column
			// Regular Error Label Component
				Label error = new Label();
				error.setTextFill(MyColor.RED.getJavaFXColor());
				GridPane.setConstraints(error, 1, 3);	// 4th row & 2nd column
			// File Error Label Component
				Label fileError = new Label();
				fileError.setTextFill(MyColor.RED.getJavaFXColor());
				GridPane.setConstraints(fileError, 1, 4);	// 5th row & 2nd column
			// File Selection Button Component
				Button fileSelection = new Button("...");	// For file selection
				GridPane.setConstraints(fileSelection, 2, 0);	// 1th row & 3rd column
			// Submit Button Component
				Button submit = new Button("Submit");	// For initializing the drawing canvas
				GridPane.setConstraints(submit, 0, 8);	// 9th row & 1st column
		
		// Query Related Components
			// For Inputting Queries
				Label lEQ = new Label("Enter Query:");
				GridPane.setConstraints(lEQ, 0, 0);	// 1st row & 1st column
				TextField tFEQ = new TextField();
				tFEQ.setPromptText("Enter Query");
				GridPane.setConstraints(tFEQ, 1, 0);	// 1st row & 2nd column
				Button updateQuery = new Button("Update Query");
				GridPane.setConstraints(updateQuery, 0, 1);		// 2nd row & 1st column
			// Showing the 'Schedule' Table From Database
				Button showSchedule = new Button("View Schedule Table");
				GridPane.setConstraints(showSchedule, 0, 0);	// 1th row & 1st column
			// Showing the 'Students' Table From Database
				Button showStudents = new Button("View Students Table");
				GridPane.setConstraints(showStudents, 0, 1);	// 2nd row & 1st column
			// Showing the 'Courses' Table From Database
				Button showCourses = new Button("View Courses Table");
				GridPane.setConstraints(showCourses, 0, 2);		// 3rd row & 1st column
			// Showing the 'Classes' Table From Database
				Button showClasses = new Button("View Classes Table");
				GridPane.setConstraints(showClasses, 0, 3);		// 4th row & 1st column
			// Shows a Table Showing the Grade Distribution In All 22100 Sections
				Button show221Grades = new Button("View 22100 Grades");
				GridPane.setConstraints(show221Grades, 0, 4);	// 5th row & 1st column
			// Randomize All Grades in the 'Classes' Table
				Button randomizeGrades = new Button("Randomize Grades");
				GridPane.setConstraints(randomizeGrades, 0, 8);	// 9th row & 1st column
			// Show the Pie Chart Representing the Grade Distribution of All 22100 Sections
				Button display221PC = new Button("22100 Pie Chart");
				GridPane.setConstraints(display221PC, 0, 9);	// 10th row & 1st column
			// Show the Pie Chart Representing the Grade Distribution for All Classes
				Button displayGradesPC = new Button("All Grades Pie Chart");
				GridPane.setConstraints(displayGradesPC, 0, 9);	// 10th row & 1st column
			// Connects to the Database
				Button connect = new Button("Connect");
				GridPane.setConstraints(connect, 0, 11);		// 12th row & 1st column
			// Disconnects From the Database
				Button disconnect = new Button("Disconnect");
				GridPane.setConstraints(disconnect, 0, 12);		// 13th row & 1st column	
				
		// Database Related Components
			// Database Username Components
				Label lUser = new Label("Database Username:");
				GridPane.setConstraints(lUser, 0, 5);	// 6th row & 1st column
				TextField tFUser = new TextField();
				tFUser.setPromptText("Enter Database Username");
				GridPane.setConstraints(tFUser, 1, 5);	// 6th row & 2nd column
			// Database Password Components
				Label lPass = new Label("Database Password:");
				GridPane.setConstraints(lPass, 0, 6);	// 7th row & 1st column
				PasswordField pw = new PasswordField();
				pw.setPromptText("Enter Database Password");
				GridPane.setConstraints(pw, 1, 6);	// 7th row & 2nd column
			// Database URL Components
				TextField tFUrl = new TextField();	
				tFUrl.setPromptText("Enter Database Url");
				GridPane.setConstraints(tFUrl, 1, 7);	// 8th row & 2nd column
				Label lUrl = new Label("Database Url:");
				GridPane.setConstraints(lUrl, 0, 7);	// 8th row & 1st column
			// Error Label for Database Related Components
				Label connectionError = new Label();
				connectionError.setTextFill(MyColor.RED.getJavaFXColor());
				GridPane.setConstraints(connectionError, 1, 8);	// 9th row, 2nd column
			// Default entries for these fields
				tFUser.setText("root");
				// TODO: Put localhost URL of (MySQL) database. Include "?allowLoadLocalInfile=true" query at end of URL
				tFUrl.setText("");
				// TODO: Put database password..
				pw.setText("");
	

		grid.getChildren().addAll(lF, lW, lH, error, fileError, connectionError, lUser, lPass, lUrl, tFF, tFW, tFH, tFUser, tFUrl, pw, fileSelection, submit);
		cgrid.getChildren().addAll(showSchedule, showStudents, showCourses, showClasses, show221Grades, display221PC, randomizeGrades, connect, disconnect);
		qgrid.getChildren().addAll(lEQ, tFEQ, updateQuery);
		sP.getChildren().addAll(grid);
		vb.getChildren().addAll(cgrid);
		
		primaryStage.setTitle("Assignment 4");
		primaryStage.setScene(new Scene(sP,665,300));
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
			
			if (errors == true) error.setText(errMessage);
			
			// File Text Field
			if (tFF.getText() != null && !tFF.getText().isEmpty()) {
				// Remove the quotations from if we paste in the file location with "Copy as Path"
				fileLocation = tFF.getText().replaceAll("[\"]","").replace("\\", "/");
				try {
					fileError.setText("");
					connectionError.setText("");
					boolean success = openFile(); // Checks to see if the file can be opened
					// Checks if other fields contain errors given that the file was able to be opened
					if (success == true && errors == false) {
						closeFile();
						// Connect to the database
						dbq = new DBQueries(tFUrl.getText(),tFUser.getText(), pw.getText());
						dbq.initialization(fileLocation);

						gradeFreq = dbq.getClassesGradeFrequency();
						
						drawingCanvas = addCanvas(cw,ch); 
						p.getChildren().add(drawingCanvas);
						int newW = (int) drawingCanvas.getWidth();
						
						GridPane.setConstraints(error, 1, 1);	// 2nd row & 2nd column
						error.setText("");
						qgrid.getChildren().addAll(error);
						
						cgrid.setPadding(new Insets((double) Math.min(cw,ch)/50));
						cgrid.setVgap((double) ch/50);
						qgrid.setPadding(new Insets((double) Math.min(cw,ch)/50));
						qgrid.setVgap((double) ch/50);
						qgrid.setHgap((double) cw/50);
						
						lEQ.setFont(new Font((double) Math.min(cw, ch)/45));
						tFEQ.setFont(new Font((double) Math.min(cw, ch)/45));
						updateQuery.setFont(new Font((double) Math.min(cw, ch)/45));
						showSchedule.setFont(new Font((double) Math.min(cw, ch)/45));
						showStudents.setFont(new Font((double) Math.min(cw, ch)/45));
						showCourses.setFont(new Font((double) Math.min(cw, ch)/45));
						showClasses.setFont(new Font((double) Math.min(cw, ch)/45));
						show221Grades.setFont(new Font((double) Math.min(cw, ch)/45));
						randomizeGrades.setFont(new Font((double) Math.min(cw, ch)/45));
						display221PC.setFont(new Font((double) Math.min(cw, ch)/45));
						displayGradesPC.setFont(new Font((double) Math.min(cw, ch)/45));
						connect.setFont(new Font((double) Math.min(cw, ch)/45));
						disconnect.setFont(new Font((double) Math.min(cw, ch)/45));
						error.setFont(new Font((double) Math.min(cw, ch)/45));
						tFEQ.setPrefWidth((double) 0.9 * newW);
						
						bP.setCenter(p);
						bP.setBottom(qgrid);
						bP.setLeft(vb);
						primaryStage.setScene(new Scene(bP,(double) newW * 1.15, ch* 1.15)); // 1.15 * new canvas width and height for the extra components
						primaryStage.show();
					} else { errors = false; }	// Reset Errors Tracking
					if (success == false) fileError.setText("Invalid File");
				} 	catch (IllegalStateException e) {
					connectionError.setText("Failed to Connect to Database Server");
				}	catch (Exception err){
					fileError.setText("Invalid File");
				}
			}  else { error.setText(errMessage); }
		});

		// Button to print 'Schedule' table to console
		showSchedule.setOnAction((ActionEvent a) -> {
			if (dbq.getConnectionStatus() != null) {
				List<Schedule> scheduleList = dbq.getScheduleList();
				System.out.println("\n\nSchedule Table:");
				System.out.printf("%-9s\t%-9s\t%-49s\t%-4s\t%-8s\t%-21s\t%-16s\t%s\n", "courseID", "sectionNo", "courseTitle", "year", "semester", "instructor", "department", "program");
				for (Schedule s : scheduleList) {
					System.out.printf("%-9s\t%-9s\t%-49s\t%-4s\t%-8s\t%-21s\t%-16s\t%s\n", s.getCourseID(), s.getSectionNo(), s.getCourseTitle(), 
					                  s.getYear(), s.getSemester(), s.getInstructor(), s.getDepartment(), s.getProgram());
				}
			}
		});
		
		// Button to print 'Students' table to console
		showStudents.setOnAction((ActionEvent a) -> {
			if (dbq.getConnectionStatus() != null) {
				List<Student> studentsList = dbq.getStudentsList();
				System.out.println("\n\nStudents Table:");
				System.out.printf("%-5s\t%-11s\t%-9s\t%-43s\t%s\n", "empID", "firstName", "lastName", "email", "gender");
				for (Student s : studentsList) { System.out.printf("%-5s\t%-11s\t%-9s\t%-43s\t%s\n", s.getEmpID(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getGender()); }
			}
		});
		
		// Button to print 'Courses' table to console
		showCourses.setOnAction((ActionEvent a) -> {
			if (dbq.getConnectionStatus() != null) {
				List<Courses> coursesList = dbq.getCoursesList();
				System.out.println("\n\nCourses Table:");
				System.out.printf("%-9s\t%-49s\t%s\n", "courseID", "courseTitle", "department");
				for (Courses c : coursesList) { System.out.printf("%-9s\t%-49s\t%s\n", c.getCourseID(), c.getCourseTitle(), c.getDepartment()); }
			}
		});
		
		// Button to print 'Classes' table to console
		showClasses.setOnAction((ActionEvent a) -> {
			if (dbq.getConnectionStatus() != null) {
				List<Classes> classesList = dbq.getClassesList();
				System.out.println("\n\nClasses Table:");
				System.out.printf("%-9s\t%-9s\t%-9s\t%-4s\t%-8s\t%s\n", "studentID", "courseID", "sectionNo", "year", "semester", "grade");
				for (Classes c : classesList) {
					if (c.getGrade() == 'N') System.out.printf("%-9s\t%-9s\t%-9s\t%-4s\t%-8s\t%s\n", c.getStudentID(), c.getCourseID(), c.getSectionNo(), c.getYear(), 
					          c.getSemester(), "null");
					else System.out.printf("%-9s\t%-9s\t%-9s\t%-4s\t%-8s\t%s\n", c.getStudentID(), c.getCourseID(), c.getSectionNo(), c.getYear(), 
							               c.getSemester(), c.getGrade());
				}
			}
		});
		
		// Button to print list of number of students for each letter grade in a 22100 course console
		show221Grades.setOnAction((ActionEvent a) -> {
			if (dbq.getConnectionStatus() != null) {
				Map<Character, Integer> SDLGradeList = dbq.get221GradeFrequency();
				System.out.println("\n\n22100 Grades Distribution Table:");
				System.out.printf("%-8s\t%s\n", "grade", "number of students");
				for (Character g : SDLGradeList.keySet()) { 
					if (g != 'N') System.out.printf("%-8s\t%s\n", g, SDLGradeList.get(g));
					else System.out.printf("%-8s\t%s\n", "No Grade", SDLGradeList.get(g));
				}
			}
		});
		
		// Button to randomize the grades in the 'Classes' table
		randomizeGrades.setOnAction((ActionEvent a) -> {
			if (dbq.getConnectionStatus() != null) {
				dbq.initializeRandomGrades();
				// Depending on which pie chart type we're displaying currently
				if (freqLabel == "Grade Distribution for All Classes") gradeFreq = dbq.getClassesGradeFrequency();	// Update frequencies map to show all grades
				else gradeFreq = dbq.get221GradeFrequency();	// Update frequency map to show 22100 grades
				histogram.updateFrequencyMap(gradeFreq, freqLabel);
				updatePieChart();
			}
		});
		
		// Button to send a query
		// Example: INSERT INTO Classes Values(100,'22100 F','32131',2021,'Spring','A')
		updateQuery.setOnAction((ActionEvent a) -> {
			if (dbq.getConnectionStatus() != null) {
				error.setText("");
				if (tFEQ.getText() != null && !tFEQ.getText().isEmpty()) {
					boolean update = dbq.queryUpdate(tFEQ.getText());
					if (update == true) { 
						// Depending on which pie chart type we're displaying currently
						if (freqLabel == "Grade Distribution for All Classes") gradeFreq = dbq.getClassesGradeFrequency();	// Update frequencies map to show all grades
						else gradeFreq = dbq.get221GradeFrequency();	// Update frequency map to show 22100 grades
						histogram.updateFrequencyMap(gradeFreq, freqLabel);
						updatePieChart();
					}
				} else error.setText("Enter in a Query");
			}
		});
		
		// Button to show pie chart representing grade distribution of 22100 sections
		display221PC.setOnAction((ActionEvent a) -> {
			if (dbq.getConnectionStatus() != null) {
				freqLabel = "Grade Distribution for 22100 Sections";
				cgrid.getChildren().remove(display221PC);
				cgrid.getChildren().add(displayGradesPC);	// Swaps the option of which pie chart to display
				gradeFreq = dbq.get221GradeFrequency();	// Update frequencies map to show all grades
				histogram.updateFrequencyMap(gradeFreq, freqLabel);
				updatePieChart();
			}
		});
		
		// Button to show pie chart representing grade distribution for all classes
		displayGradesPC.setOnAction((ActionEvent a) -> { 
			if (dbq.getConnectionStatus() != null) {
				freqLabel = "Grade Distribution for All Classes";
				cgrid.getChildren().remove(displayGradesPC);
				cgrid.getChildren().add(display221PC);	// Swaps the option of which pie chart to display
				gradeFreq = dbq.getClassesGradeFrequency();	// Update frequencies map to show all grades
				histogram.updateFrequencyMap(gradeFreq, freqLabel);
				updatePieChart();
			}
		});
		
		// Button to connect to the database
		connect.setOnAction((ActionEvent a) -> { dbq.connect(); });
		
		// Button to disconnect from the database
		disconnect.setOnAction((ActionEvent a) -> { dbq.disconnect(); });

	}
	
	public Canvas addCanvas(int w, int h) {
		Canvas cv = new Canvas(w,h);
		GraphicsContext gc = cv.getGraphicsContext2D();
		int radius = (int) ((double) 1 / 3 * Math.min(w, h));
		MyPoint pC = new MyPoint((int) (w / 2 - 0.4 * radius), (int) h / 2);
		histogram = new GradeHistogram(gradeFreq, freqLabel);
		piechart = histogram.new MyPieChart(pC, radius);
		piechart.draw(gc);
		
		return cv;
	}
	
	public void updatePieChart() {
		GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
		gc.setFill(MyColor.CANVAS_WHITE.getJavaFXColor());	// Find a color to match the current background
		gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight()); // Clear canvas
		piechart.updateContents();
		piechart.draw(gc);
	}

	public boolean isInteger(String s) {
		for (int i = 0; i < s.length(); i++) { if (Character.digit(s.charAt(i),10) < 0 ) return false; }
		return true;
	}
	
	public boolean openFile() {	// Still have this method to check if the thing entered into the file location field is a file
		try {
			input = new Scanner(Paths.get(fileLocation));
			return true;
		} catch (IOException ioException) {
			System.err.println("File not found");
			return false;
		}
	}
	
	public void closeFile() { if (input != null) input.close(); }
	
	public static void main(String[] args) {
		System.out.println("-".repeat(76) + "\n\t\t-=-=-\t" + new Date() + "\t-=-=-\n");
		launch(args);
	}
}


