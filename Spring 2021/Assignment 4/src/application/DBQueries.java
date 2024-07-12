// File: DBQuerires.java
package application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;


public class DBQueries {
    // Initialize database constants
	String url, username, password;

	Connection con;	// Initialize the connection
    PreparedStatement ps;	// Initialize the prepared statement
    ResultSet rs;	// Initialize the resultSet
    ResultSetMetaData metaData;	// Initialize the resultSet metadata variable
    
    // Lists to hold the data from the database
    List<Schedule> scheduleList = new ArrayList<Schedule>(); // Holds information from 'Schedule' table
    List<Student> studentsList = new ArrayList<Student>(); // Hold each student
    List<Courses> coursesList = new ArrayList<Courses>();
    List<Classes> classesList = new ArrayList<Classes>(); // Hold the classes the students are in
    List<String> usedNames = new ArrayList<String>(); // Holds unique names
    
    // Constants for initializing the database
	String [] databaseInit = { 
		// Makes sure the setting to allow loading data from local file is enabled
		"SET GLOBAL local_infile=1",
		// Clear all existing tables
		"DROP TABLE IF EXISTS Classes, Courses, Schedule, Students, GradeAggregate",
		// Create a 'Schedule' table to hold the contents of the springSchedule2021.txt file
		"CREATE TABLE Schedule(courseID CHAR(12) NOT NULL UNIQUE, sectionNo VARCHAR(8) NOT NULL UNIQUE, courseTitle VARCHAR(64), year INT, semester CHAR(6), instructor VARCHAR(32), " +
    	"department CHAR(16), program VARCHAR(48), PRIMARY KEY(courseID, sectionNo))",
        // Load the data from the springSchedule2021.txt file into the 'Schedule' table
				// TODO: Put path to `springSchedule2021.txt` file between the two ''.
        "LOAD DATA LOCAL INFILE '' "
    	+ "INTO TABLE Schedule COLUMNS TERMINATED BY '\t' LINES TERMINATED BY '\n' IGNORE 1 LINES",
    	// Create a 'Course' table to hold information on each course
        "CREATE TABLE Courses(courseID CHAR(12) PRIMARY KEY, courseTitle VARCHAR(64), department CHAR(16))",
        // Insert the data for each available course found in the 'Schedule' table into the 'Course' table
        "INSERT INTO Courses SELECT courseID, courseTitle, department FROM Schedule",
        // Create a 'Students' table to hold information on each student
        "CREATE TABLE Students(empID INT PRIMARY KEY, firstName VARCHAR(16), lastName VARCHAR(16), email VARCHAR(128), " +
        "gender CHAR CHECK(gender = 'F' OR gender = 'M' OR gender = 'U'))",
        // Create a 'Classes' table to hold information on each class (a student's grade in the class)
        "CREATE TABLE Classes(studentID INT REFERENCES Students(empID), courseID CHAR(12), sectionNo VARCHAR(8), year INT, semester CHAR(6), " +
        "grade CHAR CHECK(grade = 'A' OR grade = 'B' OR grade = 'C' OR grade = 'D' OR grade = 'F' OR grade = 'W'), PRIMARY KEY(studentID, courseID, sectionNo), " +
        "FOREIGN KEY(courseID) REFERENCES Courses(courseID))",
        // Create a table that will group the grades of all Students in a CSC 22100 course
        "CREATE TABLE GradeAggregate(grade CHAR PRIMARY KEY REFERENCES Classes(grade), numStudents INT)"};
	
	// Constants for the generation of names
	// 25 of the most common male and female first names (https://www.ssa.gov/oact/babynames/decades/century.html)
    String [] firstName = {"James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Charles", "Christopher", "Daniel", "Matthew", "Anthony",
    		               "Donald", "Mark", "Paul", "Steven", "Andrew", "Kenneth", "Joshua", "Kevin", "Brian", "George", "Edward", "Mary", "Patricia", "Jennifer", "Linda",
    		               "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen", "Nancy", "Lisa", "Margaret", "Betty", "Sandra", "Ashley", "Dorothy", "Kimberly",
    		               "Emily", "Donna", "Michelle", "Carol", "Amanda", "Melissa", "Deborah"}; 
    // 50 of the most common last names (https://www.al.com/news/2019/10/50-most-common-last-names-in-america.html)
    String [] lastName = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodgrigues", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson",
    		              "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis",
    		              "Robinson", "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores", "Green", "Adams", "Nelson", "Baker", "Hall",
    		              "Rivera", "Campbell", "Mitchell", "Carter", "Roberts"};
    
    char [] gender = {'M', 'F', 'U'}, grade = {'A', 'B', 'C', 'D', 'F', 'W'};
	String [][] SDLCourses = {{"22100 F", "32131"}, {"22100 P", "32132"}, {"22100 R", "32150"}};
    
    // -------------------------------------------------------------------------------------------------------------------------------
    
	DBQueries(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		connect();
	}
	
    // Connect database
    public Connection connect() {
        if (con == null) {
            try {
                con = DriverManager.getConnection(url, username, password);
                System.out.println("Successfully connected to database");
            } catch (SQLException e) {
    	        throw new IllegalStateException("Cannot connect the database!", e);
            }
        }
        return con;
    }

    // Disconnect database
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                con = null;
    			System.out.println("Database connection closed!");
            } catch (SQLException e) {
            	System.out.println(e.getMessage());
            }
        }
    }
	
	// Checks to see if an string is in a list containing strings
	private static boolean isUsed(List<String> usedList, String name) {
		for (String nm : usedList) { if (nm.equals(name)) return true; } // Use "equals" to check for same contents instead of objects with "=="
		return false;
	}
	
	private boolean isInteger(String s) {
		for (int i = 0; i < s.length(); i++) { if (Character.digit(s.charAt(i),10) < 0 ) return false; }
		return true;
	}
	
	public void initialization(String fileLocation) {
		if (con != null) 
			try {
				databaseInit[3] = "LOAD DATA LOCAL INFILE '" + fileLocation + "' INTO TABLE Schedule COLUMNS TERMINATED BY '\t' LINES TERMINATED BY '\n' IGNORE 1 LINES";
				initializeTables();
				initializeStudents();
				initializeClasses();
			} catch (Error e){
				System.out.println("Failed initialization");
			}
	}
	
	// Removes any existing tables and initialize all the tables necessary along with loading in the data
	private void initializeTables() {
		if (con != null)
			try {
		    	// Execute all the initialization statements in this for-loop (.execute() will execute DML and SQL statements
		    	for (int i = 0; i < 9; i++) { con.prepareStatement(databaseInit[i]).execute(); }
		    	updateScheduleList();	// Update the lists in which we inserted data into
		    	updateCoursesList();	// Update the lists in which we inserted data into
		    	System.out.println("Tables initialized & data loaded into 'Schedule' table");
			} catch (SQLException e) {
				throw new IllegalStateException("Failed to initialize tables", e);
			}
	}
	
	// Generates 100 students for the 'Students' table
	private void initializeStudents() {
		if (con != null)
			try {
				ps = con.prepareStatement("INSERT INTO students VALUES (?, ?, ?, ?, ?)");
				for (int i = 1; i < 101; i++) {
		    		// Random number between 0 and 49 for first & last name and random number between 0 and 2 for gender
		    		int num1 = (int) (Math.random() * 50), num2 = (int) (Math.random() * 50), num3 = (int) (Math.random() * 3);	
		    		String name = firstName[num1] + ' ' + lastName[num2];
		    		// Makes sure names are unique (Doesn't really matter as each student has an unique empID)
		    		while(isUsed(usedNames, name) == true) { 
		    			num1 = (int) (Math.random() * 50); 
		    			num2 = (int) (Math.random() * 50);
			    		name = firstName[num1] + ' ' + lastName[num2];
		    		}
		    		usedNames.add(name);	// Add to our list of used names
		    		ps.setString(1, String.valueOf(i)); // Convert the integer i to a string
		    		ps.setString(2, firstName[num1]);
		    		ps.setString(3, lastName[num2]);
		    		String email = firstName[num1].toLowerCase() + lastName[num2].toLowerCase() + i + "@citymail.cuny.edu"; // Generate an email
		    		ps.setString(4, email);
		    		ps.setString(5, String.valueOf(gender[num3]));
		    		ps.executeUpdate();	// Add the student to the 'Student' table
		    	}
				updateStudentsList();	// Update the list as we just added 100 students to the database
				System.out.println("'Students' table initialized");
			} catch (SQLException e) {
				throw new IllegalStateException("Failed to initialize students", e);
			}
	}
	
	// Initializes the 'Classes' table where the grade of the classes each student take is NULL
	private void initializeClasses() {
		if (con != null)
			try {
		    	ps = con.prepareStatement("INSERT INTO Classes VALUES (?, ?, ?, ?, ?, NULL)");
		    	
		    	for (int i = 0; i < studentsList.size(); i++) {
		    		// Deciding on how many classes a student will take
		    		int numClass = (int) (Math.random() * 4) + 1; // Up to 4 classes can be taken, minimum 1
		    		List<String> takenClasses = new ArrayList<String>(); // Holds taken classes
		    		
		    		// Guarantee at least 75 students to be in a CSC 22100 section
		    		if (i < 75) {
		    			int randSection = (int) (Math.random() * 3); // Returns 0 - 2 (Used for selection between the 3 221 sections
		    			ps.setString(1, String.valueOf(studentsList.get(i).getEmpID()));
		    			ps.setString(2, SDLCourses[randSection][0]);
		    			ps.setString(3, SDLCourses[randSection][1]);
		    			ps.setString(4, "2021");
		    			ps.setString(5, "Spring");
		    			ps.executeUpdate();
		    			takenClasses.add("22100");	// Keep track that this student is taking a 22100 course 
		    		}
		    		
		    		// Adding the rest of the classes the student will take
		    		for (int j = 0; j < numClass; j++) { 
		    			int numCourses = scheduleList.size();
		    			int selectCourseNum = (int) (Math.random() * numCourses);
		    			Schedule selectCourse = scheduleList.get(selectCourseNum);	// Select a random course
		    			String courseNum = selectCourse.getCourseID().substring(0,5);	// "Course Number" is the first 5 characters in the courseID
		    			// Make sure course number is unique
		    			while (isUsed(takenClasses,courseNum) == true) {
		    				selectCourseNum = (int) (Math.random() * numCourses);
			    			selectCourse = scheduleList.get(selectCourseNum);
			    			courseNum = selectCourse.getCourseID().substring(0,5);
		    			}
		    			takenClasses.add(courseNum); // Add this class to the list of taken classes by the student
		    			ps.setString(1, String.valueOf(studentsList.get(i).getEmpID()));
		    			ps.setString(2, selectCourse.getCourseID());
		    			ps.setString(3, String.valueOf(selectCourse.getSectionNo()));
		    			ps.setString(4, String.valueOf(selectCourse.getYear()));	
		    			ps.setString(5, selectCourse.getSemester());
		    			ps.executeUpdate();
		    		}
		    	}
		    	updateClassesList();	// Update the list as we inserted values into the 'Classes' database table
		    	System.out.println("'Classes' table initialized");
			} catch (SQLException e) {
				throw new IllegalStateException("Failed to initialize classes", e);
			}
	}
	
	// To give a random grade to every student
	public void initializeRandomGrades() {
		if (con != null)
			try { 
		    	ps = con.prepareStatement("UPDATE Classes SET grade = ? WHERE studentID = ? AND courseID = ? AND sectionNo = ?");
		    	for(Classes stdClass : classesList) {
		    		int gradeIndex = (int) (Math.random() * 6); // Get a number between 0 and 5
		    		stdClass.setGrade(grade[gradeIndex]);	// Update the grade of the class the student is currently taking
		    		ps.setString(1, String.valueOf(grade[gradeIndex]));
		    		ps.setString(2, String.valueOf(stdClass.getStudentID()));
		    		ps.setString(3, stdClass.getCourseID());
		    		ps.setString(4, String.valueOf(stdClass.getSectionNo()));
		    		ps.executeUpdate();	// Update the grade of the class the student is currently taking
		    	}
		    	System.out.println("\nAll grades randomized");
			} catch (SQLException e) {
				throw new IllegalStateException("Failed to randomize grades", e);
			}
	}

	// Checks to see if a student has taken a course already (in a section of the course) [Used for manual queries instead of initialization]
	private boolean takenClass(String s, int studentID) {
		if (con == null) return false;
		try {
			List<String> courseIDs = new ArrayList<String>();
			Set<String> coursePrefix = new HashSet<String>();
			rs = con.prepareStatement("SELECT courseID FROM Classes WHERE studentID = " + studentID).executeQuery();
			while (rs.next()) { 
				courseIDs.add(rs.getString("courseID"));
				coursePrefix.add(rs.getString("courseID").substring(0,5));	// All the courses are 5 characters long excluding the section indicator
			};
			ps = con.prepareStatement("SELECT courseID FROM Courses WHERE courseID LIKE ?");
			for (String cp : coursePrefix) {	// Find all the other sections of the course
				ps.setString(1, cp + "%");
				rs = ps.executeQuery();
				while (rs.next()) { if (!courseIDs.contains(rs.getString("courseID"))) courseIDs.add(rs.getString("courseID")); }
			}
			for (String cID : courseIDs) { if (s.contains(cID)) return true; }
			return false;
		} catch (SQLException e) {
			System.out.println("Failed to execute query");
			return false;
		}
	}
	
	// Checks if an integer is a studentID value [Helper method to the takenClass method as to get the studentID from a manually inputted query]
	private boolean isStudentID(int id) {
		if (con == null) return false;
		try {
			List<Integer> studentIDs = new ArrayList<Integer>();
			rs = con.prepareStatement("SELECT empID FROM Students").executeQuery();
			while (rs.next()) { studentIDs.add(rs.getInt("empID")); };
			for (int sID : studentIDs) { if (id == sID) return true; }
			return false;
		} catch (SQLException e) {
			System.out.println("Failed to execute query");
			return false;
		}
	}
	
	// Accepts a query; if the query updates any of the 4 databases, we update the lists and return true (will indicate that we need to update our pie chart)
	// If the query doesn't update the database, return false as the database wasn't changed thus the lists don't need to be changed
	public boolean queryUpdate(String s) {
		if (con == null) return false;
		try {
			// Database treats 'a' as the same as 'A' where we only want 'A'
			// We replace \" (double quote) with \' (single quote) since queries us single quotes for strings/characters and such
			s = s.replace("'a'", "'A'").replace("'b'", "'B'").replace("'c'", "'C'").replace("'d'", "'D'").replace("'f'", "'F'").replace("'w'", "'W'").replace("\"", "\'");
			boolean rc = false;
			
			if (s.toLowerCase().contains("insert into classes")) {	// Only care about classes table; don't want duplicate courses for a student
				String [] sIDArray, sSplit;
			  	List<Integer> sID = new ArrayList<Integer>();	// List of potential studentIDs from the string
			  	if (s.toLowerCase().contains("studentid")) {	// Means the query may not initialize all the attributes
			  		sSplit = s.split("\\(", 3);	// Split by the '(' that's guaranteed to be in the query
			  		if (sSplit.length == 3) {	// Make sure the structure is what is to be believed
			  			sIDArray = sSplit[2].replace(")","").replace(" ", "").split(",");	// Get rid of leftover ')', remove spaces, split by comma for the "values" section of the query
			  			for (String str : sIDArray) { if (isInteger(str) == true) sID.add(Integer.valueOf(str)); } // Find the int values (these are potential studentID values)
			  			for (int studID : sID) {  // Go through the list of potential studentIDs
			  				if (isStudentID(studID) == true) {	// Check if the int is a studentID
			  					// Check if the student taken the course
			  					if (takenClass(s, studID) == false) { rc = con.prepareStatement(s).execute(); }
			  				}
			  			}
			  		}
			  	} else {	// Means it's an entry of all attributes
			  		sSplit = s.split("\\(",2);
			  		if (sSplit.length == 2) {	// Make sure that the structure is what is to be believed
			  			sIDArray = sSplit[1].replace(")","").replace(" ", "").split(",");	// Get rid of leftover ')', remove spaces, split by comma
			  			for (String str : sIDArray) { if (isInteger(str) == true) sID.add(Integer.valueOf(str)); } // Find the int values (these are potential studentID values)
			  			for (int studID : sID) { // Go through the list of potential studentIDs
			  				if (isStudentID(studID) == true) {	// Check if the int is a studentID
			  					// Check if the student taken the course
			  					if (takenClass(s, studID) == false) { rc = con.prepareStatement(s).execute(); }	 
			  				}
			  			}
			  		}
			  	}
			  } else if (s.toLowerCase().contains("alter table") || s.toLowerCase().contains("drop table")) {
				  System.out.println("Query not allowed");
				  return false;
			  } else {
			  		ps = con.prepareStatement(s);
			 		rc = ps.execute();	// True if a result set is returned
			  }
			
			if (rc == true) {	// If a result set is returned, it means we haven't changed the database
				rs = ps.executeQuery();
				metaData = rs.getMetaData();	// Just to get the number of attributes & attribute names
				int numCol = metaData.getColumnCount();
				System.out.println("\n\nQuery Result:");
				for (int i = 1; i <= numCol; i++) { System.out.printf("%-12s\t", metaData.getColumnName(i)); }
				System.out.println();
				while (rs.next()) {
					for (int i = 1; i <= numCol; i++) { System.out.printf("%-12s\t", rs.getObject(i)); }
					System.out.println();
				}	
				return false;
			} else {	// If no result set was returned, some change to the database was made
				String qKey = s.toLowerCase();	// qKey - query key words
				// Check for keywords in the string and do updates based on that
				if (qKey.contains("insert into schedule") || qKey.contains("update schedule") || qKey.contains("delete from schedule")) updateScheduleList();
				if (qKey.contains("insert into student") || qKey.contains("update student") || qKey.contains("delete from student")) updateStudentsList();
				if (qKey.contains("insert into classes") || qKey.contains("update classes") || qKey.contains("delete from classes")) updateClassesList();
				if (qKey.contains("insert into courses") || qKey.contains("update courses") || qKey.contains("delete from courses")) updateCoursesList();
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Failed to execute query:\n\t>> " + s);
			return false;
		}
	}
	
	private void updateScheduleList() {
		if (con != null)
			try {
				List<Schedule> sList = new ArrayList<Schedule>();
		    	rs = con.prepareStatement("SELECT * FROM Schedule").executeQuery();
		    	while (rs.next()) { sList.add(new Schedule(rs.getString("courseID"), rs.getString("sectionNo"), rs.getString("courseTitle"), rs.getInt("year"), 
		    				                      rs.getString("semester"), rs.getString("instructor"), rs.getString("department"), rs.getString("program")));
		    	}
		    	scheduleList = sList;
			} catch (SQLException e) {
				throw new IllegalStateException("Failed to update schedule list", e);
			}
	}
	
	private void updateStudentsList() {
		if (con != null)
			try {
				List<Student> sList = new ArrayList<Student>();
		    	rs = con.prepareStatement("SELECT * FROM Students").executeQuery();
		    	while (rs.next()) { sList.add(new Student(rs.getInt("empID"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("email"), rs.getString("gender").charAt(0))); }
		    	studentsList = sList;
			} catch (SQLException e) {
				throw new IllegalStateException("Failed to update students List", e);
			}
	}
	
	private void updateClassesList() {
		if (con != null)
			try {
				List<Classes> cList = new ArrayList<Classes>();
		    	rs = con.prepareStatement("SELECT * FROM Classes").executeQuery();
		    	while (rs.next()) { 
		    		if (rs.getString("grade") == null) cList.add(new Classes(rs.getInt("studentID"), rs.getString("courseID"), rs.getString("sectionNo"), rs.getInt("year"), 
	                        												 rs.getString("semester"), 'N')); 
		    		else cList.add(new Classes(rs.getInt("studentID"), rs.getString("courseID"), rs.getString("sectionNo"), rs.getInt("year"), rs.getString("semester"), 
		    				                   rs.getString("grade").charAt(0))); 
		    	}
		    	classesList = cList;
			} catch (SQLException e) {
				throw new IllegalStateException("Failed to update classes list", e);
			}
	}
	
	private void updateCoursesList() {
		if (con != null)
			try {
				List<Courses> cList = new ArrayList<Courses>();
		    	rs = con.prepareStatement("SELECT * FROM Schedule").executeQuery();
		    	while (rs.next()) { cList.add(new Courses(rs.getString("courseID"), rs.getString("courseTitle"), rs.getString("department"))); }
		    	coursesList = cList;
			} catch (SQLException e) {
				throw new IllegalStateException("Failed to update courses list", e);
			}
	}
	
	public Connection getConnectionStatus() { return con; }
	public List<Schedule> getScheduleList() { return scheduleList; }
	public List<Student> getStudentsList() { return studentsList; }
	public List<Classes> getClassesList() { return classesList; }
	public List<Courses> getCoursesList() { return coursesList; }
	
	public Map<Character,Integer> get221GradeFrequency() {
		if (con == null) return null;
		try {
			Map<Character, Integer> frequency = new LinkedHashMap<Character, Integer>();
			con.prepareStatement("TRUNCATE TABLE GradeAggregate").executeUpdate();	// Deletes all data from the table
			con.prepareStatement("INSERT INTO GradeAggregate SELECT grade, count(grade) FROM Classes WHERE courseID LIKE '22100%' AND NOT grade = 'null' GROUP BY grade").executeUpdate();
			rs = con.prepareStatement("SELECT * FROM GradeAggregate").executeQuery();
			// Make all available grades (Including "No Grade" denoted as 'N') into frequency map
			for (Character gradeletter : grade) { frequency.put(gradeletter, 0); }
			while (rs.next()) {
				char key = rs.getString("grade").charAt(0);
				frequency.put(key, rs.getInt("numStudents"));
			}
			int numLetterGrades = 0, totalNumGrades = 0;
			rs = con.prepareStatement("SELECT COUNT(*) AS total FROM Classes WHERE courseID LIKE '22100%'").executeQuery();	// Count the number of rows
			while (rs.next()) { totalNumGrades = rs.getInt(1); }
			rs = con.prepareStatement("SELECT COUNT(*) AS total FROM Classes WHERE courseID LIKE '22100%' AND NOT grade = 'null'").executeQuery();	// Count the number letter grades for 22100
			while (rs.next()) { numLetterGrades = rs.getInt(1); }
			frequency.put('N', (totalNumGrades - numLetterGrades));
			return frequency;
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to get 22100 grade frequency", e);
		}
	}
	
	public Map<Character,Integer> getClassesGradeFrequency() {
		if (con == null) return null;
		try {
			Map<Character, Integer> frequency = new LinkedHashMap<Character, Integer>();
			con.prepareStatement("TRUNCATE TABLE GradeAggregate").executeUpdate();	// Deletes all data from the table
			con.prepareStatement("INSERT INTO GradeAggregate SELECT grade, count(grade) FROM Classes WHERE NOT grade = 'null' GROUP BY grade").executeUpdate();
			rs = con.prepareStatement("SELECT * FROM GradeAggregate").executeQuery();
			int numLetterGrades = 0, totalNumGrades = 0;
			// Make all available grades (including no grades denoted as 'N') into frequency map
			for (Character gradeletter : grade) { frequency.put(gradeletter, 0); }
			while (rs.next()) {
				char key = rs.getString("grade").charAt(0);
				frequency.put(key, rs.getInt("numStudents"));
			}
			rs = con.prepareStatement("SELECT COUNT(*) FROM Classes").executeQuery();	// Count the number of rows
			while (rs.next()) { totalNumGrades = rs.getInt(1); }
			rs = con.prepareStatement("SELECT COUNT(*) AS total FROM Classes WHERE NOT grade = 'null'").executeQuery();	// Count the number of letter grades in the classes table
			while (rs.next()) { numLetterGrades = rs.getInt(1); }
			frequency.put('N', (totalNumGrades - numLetterGrades));
			return frequency;
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to get the grade frequency of all classes", e);
		}
	}
	
}
