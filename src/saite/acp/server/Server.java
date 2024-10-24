package saite.acp.server;

import saite.acp.command.CommandException;
import saite.acp.course.Course;
import saite.acp.user.User;
import saite.acp.user.UserID;
import saite.acp.util.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Server {
    // preserve login order
    private LinkedHashMap<UserID, User> loggedUsers;

    private HashMap<UserID, User> users;

    private int nextCourseID;
    private HashMap<Integer, Course> courses;

    private ArrayList<Observer> observerList;

    public Server() {
        this.loggedUsers = new LinkedHashMap<UserID, User>();
        this.users = new HashMap<UserID, User>();
        this.nextCourseID = 1;
        this.courses = new HashMap<Integer, Course>();
        this.observerList = new ArrayList<>();
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


        observerList.removeIf((item) -> {
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
        this.observerList.add(o);
    }

    public void removeObserver(Observer o) {
        this.observerList.remove(o);
    }

    public int addCourse(Course course) throws CommandException {
        courses.put(this.nextCourseID, course);
        course.setId(this.nextCourseID);
        this.nextCourseID++;

        return course.getId();
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
}
