package saite.acp.command;

import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.user.Student;
import saite.acp.user.User;
import saite.acp.user.UserRole;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCourseCommand extends Command {
    private final String rawCourseID;
    private static final Pattern courseIDPattern = Pattern.compile("^C-([0-9]+)$");

    public SelectCourseCommand(Context context, String rawCourseID) {
        super(context);

        this.rawCourseID = rawCourseID;
    }

    @Override
    public void execute() throws CommandException {
        User currentUser = getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        UserRole currentUserRole = currentUser.getUserRole();

        if (currentUserRole != UserRole.Student) {
            throw new PermissionDeniedException();
        }

        int courseID = Course.parseCourseID(rawCourseID);

        HashMap<Integer, Course> courses = getContext().getServer().getCourses();

        Course targetCourse = courses.get(courseID);

        if (targetCourse == null) {
            throw new CommandException("Course does not exist");
        }

        Student student = (Student) currentUser;

        HashMap<Integer, Course> studentCourses = student.getCourses();

        for (Course currentCourse : studentCourses.values()) {
            if (currentCourse.getCourseTime().checkConflict(targetCourse.getCourseTime())) {
                throw new CommandException("Course time conflicts");
            }
        }

        if (targetCourse.selectCourse(student)) {
            studentCourses.put(targetCourse.getId(), targetCourse);
        } else {
            throw new CommandException("Course capacity is full");
        }


        System.out.printf("Select course success (courseId: C-%d)\n", courseID);
    }
}
