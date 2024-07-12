// File: Courses.java
package application;

public class Courses {
	String courseID, courseTitle, department;
	
	Courses(String courseID, String courseTitle, String department) {
		this.courseID = courseID;
		this.courseTitle = courseTitle;
		this.department = department;
	}

	public String getCourseID() { return courseID; }
	public String getCourseTitle() { return courseTitle; }
	public String getDepartment() { return department; }
}
