package saite.acp.command;

import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.user.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.*;

public class RemoveStudentCommand extends Command {
    private String rawUserID;
    private String rawCourseID;
    private static final Pattern courseIDPattern = Pattern.compile("^C-([0-9]+)$");

    public RemoveStudentCommand(Context context, String rawUserID, String rawCourseID) {
        super(context);
        this.rawUserID = rawUserID;
        this.rawCourseID = rawCourseID;
    }

    @Override
    public void execute() throws CommandException {
        User currentUser = (User) getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        if (currentUser.getUserRole() != UserRole.Teacher && currentUser.getUserRole() != UserRole.Administrator) {
            throw new PermissionDeniedException();
        }

        UserID targetUserID = new UserID(this.rawUserID);

        HashMap<UserID, User> registeredUsers = getContext().getServer().getUsers();

        User targetUser = registeredUsers.get(targetUserID);

        if (targetUser == null) {
            throw new CommandException("User does not exist");
        }

        if (targetUser.getUserRole() != UserRole.Student) {
            throw new CommandException("User id does not belong to a Student");
        }

        ArrayList<Course> toRemoveFromCourseList = new ArrayList<>();

        if (this.rawCourseID == null) {
            ArrayList<Course> scopeCourseList = switch (currentUser.getUserRole()) {
                case Teacher -> {
                    Teacher currentTeacher = (Teacher) currentUser;
                    yield new ArrayList<>(currentTeacher.getCourses().values());
                }
                case Administrator -> new ArrayList<>(getContext().getServer().getCourses().values());
                default -> new ArrayList<>();
            };
            toRemoveFromCourseList.addAll(scopeCourseList.stream().filter((course -> course.getSelectedUserSet().contains(targetUser))).toList());
        } else {
            int courseID;
            Matcher result = courseIDPattern.matcher(rawCourseID);
            if (!result.find()) {
                throw new IllegalArgumentContentException("course id");
            } else {
                courseID = Integer.parseInt(result.group(1));
            }

            Optional<Course> targetCourseOptional = switch (currentUser.getUserRole()) {
                case Teacher -> {
                    Teacher currentTeacher = (Teacher) currentUser;
                    yield currentTeacher.getCourses().values().stream().filter((course) -> course.getId() == courseID).findAny();
                }
                case Administrator -> Optional.ofNullable(getContext().getServer().getCourses().get(courseID));
                default -> Optional.empty();
            };

            if (targetCourseOptional.isEmpty()) {
                throw new CommandException("Course does not exist");
            } else {
                Course targetCourse = targetCourseOptional.get();
                if (targetCourse.getSelectedUserSet().contains(targetUser)) {
                    toRemoveFromCourseList.add(targetCourse);
                }

            }
        }

        if (toRemoveFromCourseList.isEmpty()) {
            throw new CommandException("Student does not select course");
        }

        for (Course course : toRemoveFromCourseList) {
            course.getSelectedUserSet().remove(targetUser);
            Student targetStudent = (Student) targetUser;

            targetStudent.getCourses().remove(course.getCourseName());
        }

        System.out.println("Remove student success");
    }
}
