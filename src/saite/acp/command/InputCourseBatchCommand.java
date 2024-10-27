package saite.acp.command;

import saite.acp.GlobalConfig;
import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserRole;

import java.io.*;
import java.util.ArrayList;

public class InputCourseBatchCommand extends Command {
    private File readPath;

    public InputCourseBatchCommand(Context context, String rawPath) {
        super(context);

        readPath = new File(GlobalConfig.DATA_DIRECTORY, rawPath);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() throws CommandException {
        User currentUser = getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        if (currentUser.getUserRole() != UserRole.Teacher) {
            throw new PermissionDeniedException();
        }

        if (!readPath.exists()) {
            throw new CommandException("File does not exist");
        }

        if (readPath.isDirectory()) {
            throw new CommandException("File is a directory");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(this.readPath)))) {
            ArrayList<Course> courseList = (ArrayList<Course>) ois.readObject();

            for (Course course : courseList) {
                try {
                    course.setTeacher((Teacher) currentUser);
                    int courseID = getContext().getServer().tryAddCourse(course);
                    course.setId(courseID);
                    course.clearSelected();
                    System.out.printf("Create course success (courseId: C-%d)\n", courseID);
                } catch (CommandException e) {
                    System.out.println(e);
                }

            }
        } catch (IOException e) {
            throw new CommandException("File read error: " + e);
        } catch (ClassNotFoundException e) {
            throw new CommandException("File content error: " + e);
        }

        System.out.println("Input course batch success");
    }
}
