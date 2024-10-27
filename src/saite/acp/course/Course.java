package saite.acp.course;

import saite.acp.user.Student;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserRole;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Course implements Serializable {
    transient private int id;
    private boolean valid;
    private String courseName;
    private CourseTime courseTime;
    private double courseCredit;
    private int courseClassHour;

    private int capacity;

    transient private Teacher teacher;

    transient private HashSet<User> selectedUserSet;

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

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String roleView(UserRole role) {
        return switch (role) {
            case Teacher -> String.format("C-%d %s %s %.1f %d", this.id, this.courseName,
                    this.courseTime, this.courseCredit, this.courseClassHour);
            case Student, Administrator -> String.format("%s C-%d %s %s %.1f %d", this.teacher.getName(), this.id,
                    this.courseName, this.courseTime, this.courseCredit, this.courseClassHour);
        };
    }

    public String scheduleView() {
        return String.format("%s %s %.1f %d %s", this.courseTime, this.courseName, this.courseCredit,
                this.courseClassHour, this.teacher.getName());
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
                HashMap<Integer, Course> studentCourses = student.getCourses();

                studentCourses.remove(this.getId());
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

    public int getCapacity() {
        return capacity;
    }

    public void clearSelected() {
        if (this.selectedUserSet == null) {
            this.selectedUserSet = new HashSet<>();
        } else {
            this.selectedUserSet.clear();
        }
    }

    public double getCourseCredit() {
        return this.courseCredit;
    }

    public int getCourseClassHour() {
        return this.courseClassHour;
    }

    public HashSet<User> getSelectedUserSet() {
        return this.selectedUserSet;
    }
}
