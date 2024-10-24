package saite.acp.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import saite.acp.course.Course;
import saite.acp.course.CourseTime;
import saite.acp.server.Context;
import saite.acp.server.Server;
import saite.acp.user.Teacher;
import saite.acp.user.UserID;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ListCourseCommandTest {
    private Server server;
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.server = new Server();
        this.context = server.getContext();
    }


    @Test
    void illegalArgumentCount() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "listCourse 10001 10001").execute());
        assertEquals("Illegal argument count", e.toString());
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
        void notLogin() {
            NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "listCourse").execute());
            assertEquals("No one is online", e.toString());
        }

        @Test
        void notLoginAndPrivilege() {
            NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "listCourse 12345").execute());
            assertEquals("No one is online", e.toString());
        }

        @Nested
        class WithStudent {
            @BeforeEach
            void loginStudent() {
                Command.parse(context, "login 23371001 AAA111@@@").execute();
            }

            @Test
            void noGlobalCourse() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "listCourse").execute());
                assertEquals("Course does not exist", e.toString());
            }

            @Test
            void permissionDenied() {
                PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "listCourse 10001").execute());
                assertEquals("Permission denied", e.toString());
            }
        }

        @Nested
        class WithTeacher {
            private Context teacher0Context;
            private Context teacher1Context;
            private Context teacher2Context;

            @BeforeEach
            void loginTeacher() {
                teacher0Context = server.getContext();
                teacher1Context = server.getContext();
                teacher2Context = server.getContext();
                Command.parse(teacher0Context, "login 12345 AAA111@@@").execute();
                Command.parse(teacher1Context, "login 10001 AAA111@@@").execute();
                Command.parse(teacher2Context, "login 10002 AAA111@@@").execute();
            }

            @Nested
            class WithGlobalCourse {
                @BeforeEach
                void prepareCourse() {
                    Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_0 1_1-2 5.0 64").execute();
                    Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_1 1_3-4 5.0 64").execute();
                    Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_2 1_5-6 5.0 64").execute();
                    Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_3 1_7-8 5.0 64").execute();
                    Command.parse(teacher0Context, "createCourse Deep_Rock_Galactic_4 1_9-10 5.0 64").execute();
                    Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_5 1_11-12 5.0 64").execute();
                    Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_6 1_13-14 5.0 64").execute();
                    Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_7 2_1-2 5.0 64").execute();
                    Command.parse(teacher1Context, "createCourse Deep_Rock_Galactic_8 2_3-4 5.0 64").execute();
                }

                @Test
                void noTeacherCourse() {
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher2Context, "listCourse").execute());

                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void permissionDenied() {
                    PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(teacher0Context, "listCourse 10001").execute());
                    assertEquals("Permission denied", e.toString());
                }

                @Test
                void simpleSuccess() {
                    Command.parse(teacher0Context, "listCourse").execute();
                }
            }
        }

        @Nested
        class WithAdministrator {
            @BeforeEach
            void loginAdministrator() {
                Command.parse(context, "login AD123 AAA111@@@").execute();
            }

            @Test
            void illegalUserID() {
                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "listCourse 12").execute());
                assertEquals("Illegal user id", e.toString());
            }

            @Test
            void userDoesNotExist() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "listCourse 10099").execute());
                assertEquals("User does not exist", e.toString());
            }

            @Test
            void notATeacher() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "listCourse 23371001").execute());
                assertEquals("User id does not belong to a Teacher", e.toString());
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
                void teacherNoCourse() {
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "listCourse 10002").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void simpleSuccess() {
                    Command.parse(context, "listCourse 10001").execute();
                }
            }
        }

    }

    @Nested
    class WithCourse {
        private HashMap<String, Course> strCourses;
        private HashMap<Integer, Course> intCourses;

        ArrayList<Course> studentSortedExpected;
        ArrayList<Course> teacherSortedExpected;

        @BeforeEach
        void prepareCourse() {
            this.strCourses = new HashMap<>();
            this.intCourses = new HashMap<>();
            this.studentSortedExpected = new ArrayList<>();
            this.teacherSortedExpected = new ArrayList<>();

            Teacher t1 = new Teacher(new UserID("10001"), "Lee", new byte[0], new byte[0]);
            Teacher t2 = new Teacher(new UserID("10002"), "Leo", new byte[0], new byte[0]);

            Course c1 = new Course("M_1", new CourseTime("1_1-2"), 3.0, 64, 100, t1);
            c1.setId(4);

            Course c2 = new Course("M_2", new CourseTime("1_3-4"), 3.0, 64, 100, t1);
            c2.setId(3);

            Course c3 = new Course("M_3", new CourseTime("2_1-2"), 3.0, 64, 100, t2);
            c3.setId(2);

            Course c4 = new Course("M_4", new CourseTime("2_3-4"), 3.0, 64, 100, t2);
            c4.setId(1);

            strCourses.put("M_1", c1);
            strCourses.put("M_2", c2);
            strCourses.put("M_3", c3);
            strCourses.put("M_4", c4);

            intCourses.put(4, c1);
            intCourses.put(3, c2);
            intCourses.put(2, c3);
            intCourses.put(1, c4);

            studentSortedExpected.add(c2);
            studentSortedExpected.add(c1);
            studentSortedExpected.add(c4);
            studentSortedExpected.add(c3);

            teacherSortedExpected.add(c4);
            teacherSortedExpected.add(c3);
            teacherSortedExpected.add(c2);
            teacherSortedExpected.add(c1);
        }

        @Test
        void getStudentSorted() {
            ArrayList<Course> actual = ListCourseCommand.getStudentSorted(this.intCourses);

            assertEquals(this.studentSortedExpected, actual);
        }

        @Test
        void getTeacherSorted() {
            ArrayList<Course> actual = ListCourseCommand.getTeacherSorted(this.strCourses);

            assertEquals(this.teacherSortedExpected, actual);
        }
    }


}