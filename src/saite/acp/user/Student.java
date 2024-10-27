package saite.acp.user;

import saite.acp.course.Course;

import java.util.HashMap;

public class Student extends User {
    // CourseName -> Course
    private HashMap<Integer, Course> courses;

    public Student(UserID id, String name, byte[] passwordDigestWithSalt, byte[] salt) {
        super(id, name, passwordDigestWithSalt, salt, UserRole.Student);

        this.courses = new HashMap<Integer, Course>();
    }

    public HashMap<Integer, Course> getCourses() {
        return courses;
    }
}
