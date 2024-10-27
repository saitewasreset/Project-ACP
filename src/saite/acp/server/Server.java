package saite.acp.server;

import saite.acp.GlobalConfig;
import saite.acp.command.CommandException;
import saite.acp.course.Course;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserID;
import saite.acp.util.Observer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class Server {
    // preserve login order
    private LinkedHashMap<UserID, User> loggedUsers;

    private HashMap<UserID, User> users;

    private int nextCourseID;
    private HashMap<Integer, Course> courses;

    private HashSet<Observer> observerSet;

    public Server() {
        this.loggedUsers = new LinkedHashMap<UserID, User>();
        this.users = new HashMap<UserID, User>();
        this.nextCourseID = 1;
        this.courses = new HashMap<Integer, Course>();
        this.observerSet = new HashSet<>();
    }

    public HashMap<UserID, User> getUsers() {
        return this.users;
    }

    public HashMap<UserID, User> getLoggedUsers() {
        return this.loggedUsers;
    }

    public void shutdown() {
        for (var entry : loggedUsers.entrySet()) {
            System.out.printf("%s Bye~\n", entry.getKey());
        }

        System.out.println("----- Good Bye! -----");
    }

    public boolean userLogout(UserID userID) {
        if (loggedUsers.remove(userID) == null) {
            return false;
        }


        observerSet.removeIf((item) -> {
            if (item instanceof Context context) {
                if (context.getCurrentUser().getUserID() == userID) {
                    context.update();
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        });


        System.out.printf("%s Bye~\n", userID);
        return true;
    }

    public void userLogin(User user) {
        loggedUsers.put(user.getUserID(), user);
    }

    public Context getContext() {
        return new Context(this);
    }

    public void addObserver(Observer o) {
        boolean alreadyExist = false;
        for (Observer observer : this.observerSet) {
            if (observer instanceof Context context) {
                if (o instanceof Context toAddContext) {
                    if (context == toAddContext) {
                        alreadyExist = true;
                    }
                }

            }
        }

        if (!alreadyExist) {
            this.observerSet.add(o);
        }
    }

    public void removeObserver(Observer o) {
        this.observerSet.remove(o);
    }

    public int addCourse(Course course) throws CommandException {
        courses.put(this.nextCourseID, course);
        course.setId(this.nextCourseID);
        this.nextCourseID++;

        return course.getId();
    }

    public int tryAddCourse(Course course) throws CommandException {
        Teacher teacher = course.getTeacher();

        if (teacher.getCourses().size() >= GlobalConfig.TEACHER_COURSE_COUNT_LIMIT) {
            throw new CommandException("Course count reaches limit");
        }

        if (teacher.getCourses().containsKey(course.getCourseName())) {
            // In CreateCourseCommand, message is "Course name exists" (according to specification)
            throw new CommandException("Course name already exists");
        }

        for (var courseInfo : teacher.getCourses().values()) {
            if (course.getCourseTime().checkConflict(courseInfo.getCourseTime())) {
                throw new CommandException("Course time conflicts");
            }
        }

        teacher.getCourses().put(course.getCourseName(), course);

        return addCourse(course);
    }

    public void deleteCourse(int courseID) throws CommandException {
        Course targetCourse = this.courses.get(courseID);

        if (targetCourse == null) {
            throw new CommandException("Course does not exist");
        }

        targetCourse.processCourseDelete();
        courses.remove(courseID);
    }

    public HashMap<Integer, Course> getCourses() {
        return courses;
    }

    public HashSet<Observer> getObserverSet() {
        return observerSet;
    }
}
