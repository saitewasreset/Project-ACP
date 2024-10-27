package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import saite.acp.GlobalConfig;
import saite.acp.course.Course;
import saite.acp.course.CourseTime;
import saite.acp.server.Context;
import saite.acp.server.Server;
import saite.acp.user.Teacher;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BatchInputOutputTest {
    protected Server server;
    protected Context context;

    @BeforeEach
    void prepareContext() {
        this.server = new Server();
        this.context = server.getContext();
    }

    @Test
    void illegalArgumentCountForOutput() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "outputCourseBatch").execute());
        assertEquals("Illegal argument count", e.toString());

        e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "outputCourseBatch rock.txt stone.txt").execute());
        assertEquals("Illegal argument count", e.toString());
    }

    @Test
    void illegalArgumentCountForInput() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "inputCourseBatch").execute());
        assertEquals("Illegal argument count", e.toString());

        e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "inputCourseBatch rock.txt stone.txt").execute());
        assertEquals("Illegal argument count", e.toString());
    }

    @Test
    void notLoginForOutput() {
        NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "outputCourseBatch rock.txt").execute());
        assertEquals("No one is online", e.toString());
    }

    @Test
    void notLoginForInput() {
        NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "inputCourseBatch rock.txt").execute());
        assertEquals("No one is online", e.toString());
    }

    @Nested
    class WithUsers {
        @BeforeEach
        void prepareUsers() {
            Command.parse(context, "register 23371001 Scout AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register SY2221118 Engineer AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register BY2221118 Gunner AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register 12345 Driller_A AAA111@@@ AAA111@@@ Teacher").execute();
            Command.parse(context, "register 10001 Driller_B AAA111@@@ AAA111@@@ Teacher").execute();
            Command.parse(context, "register 10002 Driller_C AAA111@@@ AAA111@@@ Teacher").execute();
            Command.parse(context, "register AD123 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute();
        }

        @Test
        void permissionDeniedForOutput() {
            Command.parse(context, "login 23371001 AAA111@@@").execute();
            PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "outputCourseBatch rock.txt").execute());
            assertEquals("Permission denied", e.toString());
        }

        @Test
        void permissionDeniedForInput() {
            Command.parse(context, "login 23371001 AAA111@@@").execute();
            PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "inputCourseBatch rock.txt").execute());
            assertEquals("Permission denied", e.toString());
        }

        @Nested
        class WithCourses {
            private Context teacher0Context;
            private Context teacher1Context;
            private Context teacher2Context;

            @BeforeEach
            void prepareCourse() {
                teacher0Context = server.getContext();
                teacher1Context = server.getContext();
                teacher2Context = server.getContext();
                Command.parse(teacher0Context, "login 12345 AAA111@@@").execute();
                Command.parse(teacher1Context, "login 10001 AAA111@@@").execute();
                Command.parse(teacher2Context, "login 10002 AAA111@@@").execute();
                Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_0 1_1-2 5.0 64").execute();
                Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_1 1_3-4 5.0 64").execute();
                Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_2 1_5-6 5.0 64").execute();
                Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_3 1_7-8 5.0 64").execute();
                Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_4 1_9-10 5.0 64").execute();
                Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_5 2_11-12 5.0 64").execute();
                Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_5 1_13-14 5.0 64").execute();
                Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_6 2_1-2 5.0 64").execute();
                Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_7 2_3-4 5.0 64").execute();
                Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_8 2_5-6 5.0 64").execute();
                Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_9 2_11-12 5.0 64").execute();
            }

            @Test
            @SuppressWarnings("unchecked")
            void simpleSuccess() throws IOException, ClassNotFoundException {
                Teacher targetTeacher = (Teacher) teacher0Context.getCurrentUser();
                HashMap<String, Course> expectedTeacherCourseMap = targetTeacher.getCourses();
                Command.parse(teacher0Context, "outputCourseBatch success_0.txt").execute();

                File savePath = new File(GlobalConfig.DATA_DIRECTORY, "success_0.txt");

                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(savePath)));

                ArrayList<Course> courseList = (ArrayList<Course>) ois.readObject();

                assertEquals(expectedTeacherCourseMap.size(), courseList.size());

                for (Course course : courseList) {
                    Course expectedCourse = expectedTeacherCourseMap.get(course.getCourseName());
                    assertNotNull(expectedCourse);

                    assertEquals(course.getCourseTime(), expectedCourse.getCourseTime());
                    assertEquals(course.getCourseCredit(), expectedCourse.getCourseCredit());
                    assertEquals(course.getCourseClassHour(), expectedCourse.getCourseClassHour());
                }

                Context newTeacherContext = server.getContext();
                Command.parse(newTeacherContext, "register 60000 Driller_C AAA111@@@ AAA111@@@ Teacher").execute();
                Command.parse(newTeacherContext, "login 60000 AAA111@@@").execute();
                Command.parse(newTeacherContext, "inputCourseBatch success_0.txt").execute();

                for (Course course : ((Teacher) newTeacherContext.getCurrentUser()).getCourses().values()) {
                    Course expectedCourse = expectedTeacherCourseMap.get(course.getCourseName());
                    assertNotNull(expectedCourse);

                    assertEquals(course.getCourseTime(), expectedCourse.getCourseTime());
                    assertEquals(course.getCourseCredit(), expectedCourse.getCourseCredit());
                    assertEquals(course.getCourseClassHour(), expectedCourse.getCourseClassHour());
                    assertNotEquals(expectedCourse.getId(), course.getId());
                    assertEquals(0, course.getSelectedUserSet().size());
                }
            }

            @Test
            void courseCountReachesLimit() {
                Teacher targetTeacher = (Teacher) teacher0Context.getCurrentUser();
                Command.parse(teacher0Context, "outputCourseBatch success_1.txt").execute();

                Context newTeacherContext = server.getContext();
                Command.parse(newTeacherContext, "register 60000 Driller_C AAA111@@@ AAA111@@@ Teacher").execute();
                Command.parse(newTeacherContext, "login 60000 AAA111@@@").execute();

                Command.parse(newTeacherContext, "createCourse Deep_Rock_N0 3_1-2 5.0 64").execute();
                Command.parse(newTeacherContext, "createCourse Deep_Rock_N1 3_3-4 5.0 64").execute();
                Command.parse(newTeacherContext, "createCourse Deep_Rock_N2 3_5-6 5.0 64").execute();
                Command.parse(newTeacherContext, "createCourse Deep_Rock_N3 3_7-8 5.0 64").execute();
                Command.parse(newTeacherContext, "createCourse Deep_Rock_N4 3_9-10 5.0 64").execute();
                Command.parse(newTeacherContext, "createCourse Deep_Rock_N5 4_11-12 5.0 64").execute();

                Command.parse(newTeacherContext, "inputCourseBatch success_1.txt").execute();
                Teacher newTeacher = (Teacher) newTeacherContext.getCurrentUser();

                assertEquals(GlobalConfig.TEACHER_COURSE_COUNT_LIMIT, newTeacher.getCourses().size());

                // We assume that courses are sorted by courseID

                Set<String> expectedCourseNameSet = Set.of("Deep_Rock_N0", "Deep_Rock_N1",
                        "Deep_Rock_N2", "Deep_Rock_N3", "Deep_Rock_N4",
                        "Deep_Rock_N5", "Deep_Rock_Galactic_0", "Deep_Rock_Galactic_1", "Deep_Rock_Galactic_2",
                        "Deep_Rock_Galactic_3");

                HashMap<String, Course> newTeacherCoursesMap = ((Teacher) newTeacherContext.getCurrentUser()).getCourses();

                assertEquals(GlobalConfig.TEACHER_COURSE_COUNT_LIMIT, newTeacherCoursesMap.size());

                assertTrue(expectedCourseNameSet.containsAll(newTeacherCoursesMap.values().stream().map(Course::getCourseName).toList()));
            }

            @Test
            void courseNameAlreadyExist() {
                Teacher targetTeacher = (Teacher) teacher0Context.getCurrentUser();
                Command.parse(teacher0Context, "outputCourseBatch success_2.txt").execute();

                Context newTeacherContext = server.getContext();
                Command.parse(newTeacherContext, "register 60000 Driller_C AAA111@@@ AAA111@@@ Teacher").execute();
                Command.parse(newTeacherContext, "login 60000 AAA111@@@").execute();

                Command.parse(newTeacherContext, "createCourse Deep_Rock_Galactic_0 3_1-2 5.0 64").execute();
                Command.parse(newTeacherContext, "createCourse Deep_Rock_N1 3_3-4 5.0 64").execute();

                Command.parse(newTeacherContext, "inputCourseBatch success_2.txt").execute();

                Teacher newTeacher = (Teacher) newTeacherContext.getCurrentUser();

                HashMap<String, Course> newTeacherCoursesMap = newTeacher.getCourses();

                assertEquals(7, newTeacherCoursesMap.size());

                Course saveOrigin = newTeacherCoursesMap.get("Deep_Rock_Galactic_0");

                assertNotNull(saveOrigin);

                assertEquals(new CourseTime("3_1-2"), saveOrigin.getCourseTime());
            }

            @Test
            void courseTimeConflict() {
                Teacher targetTeacher = (Teacher) teacher0Context.getCurrentUser();
                Command.parse(teacher0Context, "outputCourseBatch success_3.txt").execute();

                Context newTeacherContext = server.getContext();
                Command.parse(newTeacherContext, "register 60000 Driller_C AAA111@@@ AAA111@@@ Teacher").execute();
                Command.parse(newTeacherContext, "login 60000 AAA111@@@").execute();

                Command.parse(newTeacherContext, "createCourse Deep_Rock_N0 1_1-2 5.0 64").execute();
                Command.parse(newTeacherContext, "createCourse Deep_Rock_N1 3_3-4 5.0 64").execute();

                Command.parse(newTeacherContext, "inputCourseBatch success_3.txt").execute();

                Teacher newTeacher = (Teacher) newTeacherContext.getCurrentUser();

                HashMap<String, Course> newTeacherCoursesMap = newTeacher.getCourses();

                assertEquals(7, newTeacherCoursesMap.size());

                assertNull(newTeacherCoursesMap.get("Deep_Rock_Galactic_0"));
                assertNotNull(newTeacherCoursesMap.get("Deep_Rock_N0"));
            }

        }
    }
}
