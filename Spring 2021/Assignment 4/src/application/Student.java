// File: Student.java
package application;

public class Student {
	int empID;
	String firstName, lastName, email;
	char gender;
	
	Student(int empID, String firstName, String lastName, String email, char gender) {
		this.empID = empID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.gender = gender;
	}
	
	public int getEmpID() { return empID; }
	public String getFirstName() { return firstName; }
	public String getLastName() { return lastName; }
	public String getFullName() { return firstName + " " + lastName; }
	public String getEmail() { return email; }
	public char getGender() { return gender; }
}
