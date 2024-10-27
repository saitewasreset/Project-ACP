package saite.acp.command;

import saite.acp.course.Course;
import saite.acp.course.CourseTime;
import saite.acp.server.Context;
import saite.acp.user.Student;
import saite.acp.user.User;
import saite.acp.user.UserID;
import saite.acp.user.UserRole;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class ListCourseScheduleCommand extends Command {
    private final String rawUserID;

    public ListCourseScheduleCommand(Context context, String rawUserID) {
        super(context);
        this.rawUserID = rawUserID;
    }

    public ArrayList<Course> getToPrintCourseList() throws CommandException {
        User currentUser = getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        User targetUser = null;

        if (rawUserID == null) {
            if (currentUser.getUserRole() != UserRole.Student) {
                throw new PermissionDeniedException();
            }
            targetUser = currentUser;
        } else {
            if (currentUser.getUserRole() != UserRole.Administrator) {
                throw new PermissionDeniedException();
            }

            UserID targetUserID = new UserID(rawUserID);

            HashMap<UserID, User> registeredUsers = getContext().getServer().getUsers();

            targetUser = registeredUsers.get(targetUserID);

            if (targetUser == null) {
                throw new CommandException("User does not exist");
            }

            if (targetUser.getUserRole() != UserRole.Student) {
                throw new CommandException("User id does not belong to a Student");
            }
        }

        Student targetStudent = (Student) targetUser;

        ArrayList<Course> studentCourseList = targetStudent.getCourses().values().stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (studentCourseList.isEmpty()) {
            throw new CommandException("Student does not select course");
        }

        studentCourseList.sort(Comparator.comparing(Course::getCourseTime, CourseTime.comparator));

        return studentCourseList;
    }

    @Override
    public void execute() throws CommandException {
        ArrayList<Course> toPrintCourseList = getToPrintCourseList();

        for (Course course : toPrintCourseList) {
            System.out.println(course.scheduleView());
        }
        System.out.println("List course schedule success");
    }
}
