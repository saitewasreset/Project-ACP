package saite.acp.command;

import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserID;
import saite.acp.user.UserRole;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class ListCourseCommand extends Command {
    private String rawUserID;

    public ListCourseCommand(Context context, String rawUserID) {
        super(context);

        this.rawUserID = rawUserID;
    }

    @Override
    public void execute() throws CommandException {
        User currentUser = getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        UserRole currentUserRole = currentUser.getUserRole();

        if (this.rawUserID == null) {
            switch (currentUserRole) {
                case Student, Administrator -> {
                    HashMap<Integer, Course> courses = getContext().getServer().getCourses();

                    if (courses.isEmpty()) {
                        throw new CommandException("Course does not exist");
                    }

                    ArrayList<Course> courseList = getStudentSorted(courses);

                    for (Course currentCourse : courseList) {
                        System.out.println(currentCourse.roleView(currentUserRole));
                    }
                }
                case Teacher -> {
                    Teacher teacher = (Teacher) currentUser;

                    HashMap<String, Course> courses = teacher.getCourses();

                    if (courses.isEmpty()) {
                        throw new CommandException("Course does not exist");
                    }

                    ArrayList<Course> courseList = getTeacherSorted(courses);

                    for (Course currentCourse : courseList) {
                        System.out.println(currentCourse.roleView(currentUserRole));
                    }
                }
            }
        } else {
            if (currentUserRole != UserRole.Administrator) {
                throw new PermissionDeniedException();
            }

            UserID userID = new UserID(this.rawUserID);

            HashMap<UserID, User> users = getContext().getServer().getUsers();

            User targetUser = users.get(userID);

            if (targetUser == null) {
                throw new CommandException("User does not exist");
            }

            if (targetUser.getUserRole() != UserRole.Teacher) {
                throw new CommandException("User id does not belong to a Teacher");
            }

            Teacher teacher = (Teacher) targetUser;

            HashMap<String, Course> courses = teacher.getCourses();

            if (courses.isEmpty()) {
                throw new CommandException("Course does not exist");
            }

            ArrayList<Course> courseList = new ArrayList<>(courses.values());

            courseList.sort(Comparator.comparing(Course::getTeacher, Comparator.comparing(Teacher::getName)).thenComparing(Course::getId));

            for (Course currentCourse : courseList) {
                System.out.println(currentCourse.roleView(currentUserRole));
            }
        }


        System.out.println("List course success");
    }

    public static ArrayList<Course> getTeacherSorted(HashMap<String, Course> courses) {
        ArrayList<Course> courseList = new ArrayList<>(courses.values());

        courseList.sort(Comparator.comparingInt(Course::getId));
        return courseList;
    }

    public static ArrayList<Course> getStudentSorted(HashMap<Integer, Course> courses) {
        ArrayList<Course> courseList = new ArrayList<>(courses.values());

        courseList.sort(Comparator.comparing(Course::getTeacher, Comparator.comparing(Teacher::getName)).thenComparing(Course::getId));

        return courseList;
    }
}
