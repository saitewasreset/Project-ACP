package saite.acp.course;

import saite.acp.user.Student;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserRole;

import java.util.HashMap;
import java.util.HashSet;

public class Course {
    private int id;
    private boolean valid;
    private String courseName;
    private CourseTime courseTime;
    private double courseCredit;
    private int courseClassHour;

    private int capacity;

    private Teacher teacher;

    private HashSet<User> selectedUserSet;

    public Course(String courseName, CourseTime courseTime, double courseCredit, int courseClassHour, int capacity,
                  Teacher teacher) {
        this.id = 1;
        this.valid = true;
        this.courseName = courseName;
        this.courseTime = courseTime;

        this.courseCredit = courseCredit;
        this.courseClassHour = courseClassHour;

        this.capacity = capacity;

        this.teacher = teacher;

        this.selectedUserSet = new HashSet<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public CourseTime getCourseTime() {
        return this.courseTime;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String roleView(UserRole role) {
        return switch (role) {
            case Teacher -> String.format("C-%d %s %s %.1f %d", this.id, this.courseName,
                    this.courseTime, this.courseCredit, this.courseClassHour);
            case Student, Administrator -> String.format("%s C-%d %s %s %.1f %d", this.teacher.getName(), this.id,
                    this.courseName, this.courseTime, this.courseCredit, this.courseClassHour);
        };
    }

    public boolean selectCourse(User user) {
        if (this.selectedUserSet.size() >= this.capacity) {
            return false;
        } else {
            this.selectedUserSet.add(user);
            return true;
        }
    }

    public boolean cancelCourse(User user) {
        return this.selectedUserSet.remove(user);
    }

    private void cancelAllSelected() {
        for (User selectedUser : this.selectedUserSet) {
            if (selectedUser instanceof Student student) {
                HashMap<String, Course> studentCourses = student.getCourses();

                studentCourses.remove(this.getCourseName());
            }
        }
    }

    public void processCourseDelete() {
        this.cancelAllSelected();
        this.teacher.getCourses().remove(this.getCourseName());
    }

    public int getSelectedCount() {
        return this.selectedUserSet.size();
    }
}
