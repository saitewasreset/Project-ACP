package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.server.Server;
import saite.acp.user.Student;
import saite.acp.user.UserID;

import static org.junit.jupiter.api.Assertions.*;

class RemoveStudentCommandTest {
    private Server server;
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.server = new Server();
        this.context = server.getContext();
    }

    @Test
    void illegalArgumentCount() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "removeStudent").execute());
        assertEquals("Illegal argument count", e.toString());
        e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "removeStudent 23371001 C-1 C-2").execute());
        assertEquals("Illegal argument count", e.toString());
    }

    @Test
    void notLogin() {
        NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "removeStudent 23371001").execute());
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
            Command.parse(context, "register 23161001 Scout_A AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register 23161002 Scout_B AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register 23161003 Scout_C AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register 23161004 Scout_D AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register 23161005 Scout_E AAA111@@@ AAA111@@@ Student").execute();

        }


        @Test
        void permissionDenied() {
            Command.parse(context, "login 23371001 AAA111@@@").execute();
            PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "removeStudent 23371001").execute());
            assertEquals("Permission denied", e.toString());
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

            @Test
            void illegalUserID() {
                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(teacher0Context, "removeStudent 2337100").execute());
                assertEquals("Illegal user id", e.toString());
            }

            @Test
            void userDoesNotExist() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher0Context, "removeStudent 23375001").execute());
                assertEquals("User does not exist", e.toString());
            }

            @Test
            void userNotStudent() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher0Context, "removeStudent 12345").execute());
                assertEquals("User id does not belong to a Student", e.toString());
            }

            @Nested
            class WithCourses {
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

                @Nested
                class WithCourseSelected {
                    private Context student0Context;
                    private Context student1Context;
                    private Context student2Context;
                    private Context student3Context;
                    private Context student4Context;

                    @BeforeEach
                    void prepareStudents() {
                        student0Context = server.getContext();
                        student1Context = server.getContext();
                        student2Context = server.getContext();
                        student3Context = server.getContext();
                        student4Context = server.getContext();
                        Command.parse(student0Context, "login 23161001 AAA111@@@").execute();
                        Command.parse(student1Context, "login 23161002 AAA111@@@").execute();
                        Command.parse(student2Context, "login 23161003 AAA111@@@").execute();
                        Command.parse(student3Context, "login 23161004 AAA111@@@").execute();
                        Command.parse(student4Context, "login 23161005 AAA111@@@").execute();

                        Command.parse(student0Context, "selectCourse C-1").execute();
                        Command.parse(student0Context, "selectCourse C-2").execute();
                        Command.parse(student1Context, "selectCourse C-1").execute();
                        Command.parse(student1Context, "selectCourse C-2").execute();
                        Command.parse(student2Context, "selectCourse C-1").execute();
                        Command.parse(student2Context, "selectCourse C-3").execute();
                        Command.parse(student2Context, "selectCourse C-9").execute();
                    }

                    @Test
                    void notSelectSelfOne() {
                        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher1Context, "removeStudent 23161001").execute());
                        assertEquals("Student does not select course", e.toString());
                    }

                    @Test
                    void simpleSuccessOne() {
                        Student targetStudent = (Student) server.getUsers().get(new UserID("23161001"));
                        assertTrue(targetStudent.getCourses().containsKey(1));
                        Course targetCourse = server.getCourses().get(1);

                        assertTrue(targetCourse.getSelectedUserSet().contains(targetStudent));

                        Command.parse(teacher0Context, "removeStudent 23161001").execute();

                        assertFalse(targetStudent.getCourses().containsKey(1));
                        assertFalse(targetCourse.getSelectedUserSet().contains(targetStudent));
                        assertFalse(targetStudent.getCourses().containsKey(2));

                    }

                    @Test
                    void simpleSuccessTwo() {
                        Student targetStudent = (Student) server.getUsers().get(new UserID("23161001"));
                        assertTrue(targetStudent.getCourses().containsKey(1));
                        Course targetCourse = server.getCourses().get(1);
                        Course stillSelectedCourse = server.getCourses().get(2);

                        assertTrue(targetCourse.getSelectedUserSet().contains(targetStudent));
                        assertTrue(stillSelectedCourse.getSelectedUserSet().contains(targetStudent));

                        Command.parse(teacher0Context, "removeStudent 23161001 C-1").execute();

                        assertFalse(targetStudent.getCourses().containsKey(1));
                        assertFalse(targetCourse.getSelectedUserSet().contains(targetStudent));
                        assertTrue(targetStudent.getCourses().containsKey(2));
                        assertTrue(stillSelectedCourse.getSelectedUserSet().contains(targetStudent));
                    }

                    @Test
                    void courseDoesNotBelongToSelf() {
                        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher2Context, "removeStudent 23161001 C-1").execute());
                        assertEquals("Course does not exist", e.toString());
                    }

                    @Test
                    void courseAlreadyCancelled() {
                        Command.parse(teacher0Context, "cancelCourse C-1").execute();
                        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher0Context, "removeStudent 23161001 C-1").execute());
                        assertEquals("Course does not exist", e.toString());
                    }

                    @Nested
                    class WithAdministrator {
                        private Context adminContext;

                        @BeforeEach
                        void loginAdmin() {
                            adminContext = server.getContext();
                            Command.parse(adminContext, "login AD123 AAA111@@@").execute();
                        }

                        @Test
                        void simpleSuccessOne() {
                            Command.parse(adminContext, "removeStudent 23161001").execute();
                        }

                        @Test
                        void simpleSuccessTwo() {
                            Command.parse(adminContext, "removeStudent 23161001 C-1").execute();
                        }

                        @Test
                        void illegalCourseID() {
                            IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(adminContext, "removeStudent 23161001 C-1-2").execute());
                            assertEquals("Illegal course id", e.toString());
                        }

                        @Test
                        void notSelectTwo() {
                            CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(adminContext, "removeStudent 23161001 C-7").execute());
                            assertEquals("Student does not select course", e.toString());
                        }
                    }
                }
            }


        }

    }
}