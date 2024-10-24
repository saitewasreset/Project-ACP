package saite.acp.command;

import saite.acp.course.Course;
import saite.acp.course.CourseTime;
import saite.acp.server.Context;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserRole;

import java.util.HashMap;

public class CreateCourseCommand extends Command {
    private final String courseName;
    private final String rawCourseTime;
    private final String rawCourseCredit;
    private final String rawClassHour;

    public CreateCourseCommand(Context context, String courseName, String rawCourseTime, String rawCourseCredit, String rawClassHour) {
        super(context);

        this.courseName = courseName;
        this.rawCourseTime = rawCourseTime;
        this.rawCourseCredit = rawCourseCredit;
        this.rawClassHour = rawClassHour;
    }

    @Override
    public void execute() throws CommandException {
        User currentUser = this.getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        if (currentUser.getUserRole() != UserRole.Teacher) {
            throw new PermissionDeniedException();
        }

        Teacher teacher = (Teacher) currentUser;
        HashMap<String, Course> teacherCourses = teacher.getCourses();

        if (teacherCourses.size() >= 10) {
            throw new CommandException("Course count reaches limit");
        }

        // Check course name

        if (this.courseName.isEmpty() || this.courseName.length() > 20) {
            throw new IllegalArgumentContentException("course name");
        }

        char firstCharacter = this.courseName.charAt(0);
        if (!((firstCharacter >= 'A' && firstCharacter <= 'Z') || (firstCharacter >= 'a' && firstCharacter <= 'z'))) {
            throw new IllegalArgumentContentException("course name");
        }

        // We don't need to check existence of English letters as courseName[0] is English letter

        for (char ch : this.courseName.toCharArray()) {
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                continue;
            } else if (ch >= '0' && ch <= '9') {
                continue;
            } else if (ch == '-' || ch == '_') {
                continue;
            } else {
                throw new IllegalArgumentContentException("course name");
            }
        }

        if (teacherCourses.containsKey(this.courseName)) {
            throw new CommandException("Course name exists");
        }

        // Check course time

        CourseTime courseTime = new CourseTime(this.rawCourseTime);

        for (var courseInfo : teacherCourses.values()) {
            if (courseTime.checkConflict(courseInfo.getCourseTime())) {
                throw new CommandException("Course time conflicts");
            }
        }

        // Check course credit
        double courseCredit = 0;
        try {
            courseCredit = Double.parseDouble(this.rawCourseCredit);

            if (!(courseCredit > 0 && courseCredit <= 12)) {
                throw new IllegalArgumentContentException("course credit");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentContentException("course credit");
        }

        // Check course class hour
        int classHour = 0;

        try {
            classHour = Integer.parseInt(this.rawClassHour);

            if (!(classHour > 0 && classHour <= 1280)) {
                throw new IllegalArgumentContentException("course period");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentContentException("course period");
        }

        Course newCourse = new Course(this.courseName, courseTime, courseCredit, classHour, Integer.MAX_VALUE, teacher);
        int courseID = this.getContext().getServer().addCourse(newCourse);

        teacher.getCourses().put(this.courseName, newCourse);

        System.out.printf("Create course success (courseId: C-%d)\n", courseID);
    }
}
