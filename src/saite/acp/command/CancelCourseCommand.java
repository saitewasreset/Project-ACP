package saite.acp.command;

import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.user.Student;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserRole;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CancelCourseCommand extends Command {
    private final String rawCourseID;
    private static final Pattern courseIDPattern = Pattern.compile("^C-([0-9]+)$");

    public CancelCourseCommand(Context context, String rawCourseID) {
        super(context);
        this.rawCourseID = rawCourseID;
    }

    @Override
    public void execute() throws CommandException {
        User currentUser = getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        Matcher courseIDMatcher = courseIDPattern.matcher(rawCourseID);

        if (!courseIDMatcher.find()) {
            throw new IllegalArgumentContentException("course id");
        }

        int courseID;

        try {
            courseID = Integer.parseInt(courseIDMatcher.group(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentContentException("course id");
        }

        if (courseID == 0) {
            throw new IllegalArgumentContentException("course id");
        }

        HashMap<Integer, Course> globalCourses = getContext().getServer().getCourses();
        Course targetCourse = globalCourses.get(courseID);

        if (targetCourse == null) {
            throw new CommandException("Course does not exist");
        }

        String targetCourseName = targetCourse.getCourseName();

        switch (currentUser.getUserRole()) {
            case Student -> {
                Student student = (Student) currentUser;

                HashMap<String, Course> studentCourses = student.getCourses();

                Course removed = studentCourses.remove(targetCourseName);

                if (removed == null) {
                    throw new CommandException("Course does not exist");
                } else {
                    removed.cancelCourse(student);
                }
            }
            case Teacher -> {
                Teacher teacher = (Teacher) currentUser;

                HashMap<String, Course> teacherCourses = teacher.getCourses();

                Course removed = teacherCourses.remove(targetCourseName);

                if (removed == null) {
                    throw new CommandException("Course does not exist");
                } else {
                    getContext().getServer().deleteCourse(removed.getId());
                }
            }
            case Administrator -> {
                getContext().getServer().deleteCourse(courseID);
            }
        }

        System.out.printf("Cancel course success (courseId: C-%d)\n", courseID);
    }
}
