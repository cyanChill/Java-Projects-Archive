// File: Classes.java
package application;

public class Classes {
	int studentID, year;
	String courseID, semester, sectionNo;
	char grade;
	
	Classes(int studentID, String courseID, String sectionNo, int year, String semester, char grade) {
		this.studentID = studentID;
		this.courseID = courseID;
		this.sectionNo = sectionNo;
		this.year = year;
		this.semester = semester;
		this.grade = grade;
	}
	
	public int getStudentID() { return studentID; }
	public String getCourseID() { return courseID; }
	public String getSectionNo() { return sectionNo; }
	public int getYear() { return year; }
	public String getSemester() { return semester; }
	public char getGrade() { return grade; }
	
	public void setGrade(char grade) { this.grade = grade; }
}
