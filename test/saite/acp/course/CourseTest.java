package saite.acp.course;

import org.junit.jupiter.api.Test;
import saite.acp.user.Teacher;
import saite.acp.user.UserID;
import saite.acp.user.UserRole;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {


    @Test
    void roleView() {
        Teacher t = new Teacher(new UserID("12345"), "saitewasreset", new byte[0], new byte[0]);
        Course c = new Course("Math", new CourseTime("1_1-14"), 3.0, 48, 100, t);
        c.setId(1);

        String studentExpected = "saitewasreset C-1 Math 1_1-14 3.0 48";
        String teacherExpected = "C-1 Math 1_1-14 3.0 48";

        assertEquals(studentExpected, c.roleView(UserRole.Student));
        assertEquals(studentExpected, c.roleView(UserRole.Administrator));
        assertEquals(teacherExpected, c.roleView(UserRole.Teacher));

    }
}