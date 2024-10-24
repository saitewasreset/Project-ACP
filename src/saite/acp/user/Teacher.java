package saite.acp.user;

import saite.acp.course.Course;

import java.util.HashMap;

public class Teacher extends User {
    // CourseName -> Course
    private HashMap<String, Course> courses;

    public Teacher(UserID id, String name, byte[] passwordDigestWithSalt, byte[] salt) {
        super(id, name, passwordDigestWithSalt, salt, UserRole.Teacher);

        this.courses = new HashMap<String, Course>();
    }

    public HashMap<String, Course> getCourses() {
        return this.courses;
    }
}
