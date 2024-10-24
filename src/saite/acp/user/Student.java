package saite.acp.user;

import saite.acp.course.Course;

import java.util.HashMap;

public class Student extends User {
    // CourseName -> Course
    private HashMap<String, Course> courses;

    public Student(UserID id, String name, byte[] passwordDigestWithSalt, byte[] salt) {
        super(id, name, passwordDigestWithSalt, salt, UserRole.Student);

        this.courses = new HashMap<String, Course>();
    }

    public HashMap<String, Course> getCourses() {
        return courses;
    }
}
