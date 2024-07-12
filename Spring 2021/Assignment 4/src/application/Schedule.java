// File: Schedule.java
package application;

public class Schedule {
	int year;
	String courseID, courseTitle, sectionNo, semester, instructor, department, program;
	
	Schedule(String courseID, String sectionNo, String courseTitle, int year, String semester, String instructor, String department, String program) {
		this.courseID = courseID;
		this.sectionNo = sectionNo;
		this.courseTitle = courseTitle;
		this.year = year;
		this.semester = semester;
		this.instructor = instructor;
		this.department = department;
		this.program = program;
	}
	
	public String getCourseID() { return courseID; }
	public String getSectionNo() { return sectionNo; }
	public String getCourseTitle() { return courseTitle; }
	public int getYear() { return year; }
	public String getSemester() { return semester; }
	public String getInstructor() { return instructor; }
	public String getDepartment() { return department; }
	public String getProgram() { return program; }
}
