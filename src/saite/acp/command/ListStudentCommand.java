package saite.acp.command;

import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserID;
import saite.acp.user.UserRole;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.*;

public class ListStudentCommand extends Command {
    private String rawCourseID;
    private static final Pattern courseIDPattern = Pattern.compile("^C-([0-9]+)$");

    public ListStudentCommand(Context context, String rawCourseID) {
        super(context);
        this.rawCourseID = rawCourseID;
    }

    @Override
    public void execute() throws CommandException {
        User currentUser = getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        if (currentUser.getUserRole() != UserRole.Teacher && currentUser.getUserRole() != UserRole.Administrator) {
            throw new PermissionDeniedException();
        }

        Matcher result = courseIDPattern.matcher(rawCourseID);

        int courseID;

        if (!result.find()) {
            throw new IllegalArgumentContentException("course id");
        } else {
            courseID = Integer.parseInt(result.group(1));
        }

        if (courseID == 0) {
            throw new IllegalArgumentContentException("course id");
        }

        Optional<Course> targetCourse = switch (currentUser.getUserRole()) {
            case Teacher -> {
                Teacher currentTeacher = (Teacher) currentUser;

                yield currentTeacher.getCourses().values().stream().filter((course -> course.getId() == courseID)).findAny();
            }
            case Administrator -> Optional.ofNullable(getContext().getServer().getCourses().get(courseID));
            default -> Optional.empty();
        };


        if (targetCourse.isEmpty()) {
            throw new CommandException("Course does not exist");
        } else {
            Course course = targetCourse.get();

            List<User> selectedUserList = course.getSelectedUserSet().stream()
                    .sorted(Comparator.comparing(User::getUserID, UserID.studentComparator())).toList();

            if (selectedUserList.isEmpty()) {
                throw new CommandException("Student does not select course");
            }

            for (User user : selectedUserList) {
                System.out.printf("%s: %s\n", user.getUserID().getRawID(), user.getName());
            }

            System.out.println("List student success");
        }
    }
}
