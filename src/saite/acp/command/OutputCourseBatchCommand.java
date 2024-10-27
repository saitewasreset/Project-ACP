package saite.acp.command;

import saite.acp.GlobalConfig;
import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.user.Teacher;
import saite.acp.user.User;
import saite.acp.user.UserRole;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class OutputCourseBatchCommand extends Command {
    private File writePath;

    public OutputCourseBatchCommand(Context context, String rawPath) {
        super(context);
        writePath = new File(GlobalConfig.DATA_DIRECTORY, rawPath);
    }

    @Override
    public void execute() {
        User currentUser = getContext().getCurrentUser();

        if (currentUser == null) {
            throw new NoContextUserException();
        }

        if (currentUser.getUserRole() != UserRole.Teacher) {
            throw new PermissionDeniedException();
        }

        Teacher currentTeacher = (Teacher) currentUser;

        ArrayList<Course> courseList = currentTeacher.getCourses().values().stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // We assume that courses are sorted by courseID
        courseList.sort(Comparator.comparingInt(Course::getId));

        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.writePath)))) {
            oos.writeObject(courseList);
            oos.flush();
        } catch (IOException e) {
            throw new CommandException("Output course batch failed with I/O error: " + e);
        }


        System.out.println("Output course batch success");
    }
}
