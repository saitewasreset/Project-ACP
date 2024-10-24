package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import saite.acp.server.Context;
import saite.acp.server.Server;
import saite.acp.user.Student;

import static org.junit.jupiter.api.Assertions.*;

class CancelCourseCommandTest {
    protected Server server;
    protected Context context;

    @BeforeEach
    void prepareContext() {
        this.server = new Server();
        this.context = server.getContext();
    }

    @Test
    void illegalArgumentCount() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "cancelCourse"));
        assertEquals("Illegal argument count", e.toString());

        e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "cancelCourse C-1 C-2"));
        assertEquals("Illegal argument count", e.toString());
    }

    @Test
    void noContextUser() {
        NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "cancelCourse C-1").execute());
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

            @Nested
            class WithStudent {
                private Context student0Context;
                private Context student1Context;
                private Context student2Context;

                @BeforeEach
                void prepareStudent() {
                    student0Context = server.getContext();
                    student1Context = server.getContext();
                    student2Context = server.getContext();
                    Command.parse(student0Context, "login 23371001 AAA111@@@").execute();
                    Command.parse(student1Context, "login SY2221118 AAA111@@@").execute();
                    Command.parse(student2Context, "login BY2221118 AAA111@@@").execute();
                    Command.parse(student0Context, "selectCourse C-1").execute();
                    Command.parse(student0Context, "selectCourse C-2").execute();
                    Command.parse(student0Context, "selectCourse C-3").execute();
                    Command.parse(student0Context, "selectCourse C-4").execute();
                    Command.parse(student0Context, "selectCourse C-5").execute();
                    Command.parse(student0Context, "selectCourse C-6").execute();
                    Command.parse(student0Context, "selectCourse C-7").execute();
                    Command.parse(student0Context, "selectCourse C-8").execute();
                    Command.parse(student0Context, "selectCourse C-9").execute();
                    Command.parse(student0Context, "selectCourse C-10").execute();
                    Command.parse(student1Context, "selectCourse C-1").execute();
                    Command.parse(student1Context, "selectCourse C-2").execute();
                    Command.parse(student1Context, "selectCourse C-3").execute();
                    Command.parse(student1Context, "selectCourse C-4").execute();
                    Command.parse(student1Context, "selectCourse C-5").execute();
                    Command.parse(student1Context, "selectCourse C-6").execute();
                    Command.parse(student1Context, "selectCourse C-7").execute();
                    Command.parse(student1Context, "selectCourse C-8").execute();
                    Command.parse(student1Context, "selectCourse C-9").execute();
                    Command.parse(student1Context, "selectCourse C-10").execute();
                    Command.parse(student2Context, "selectCourse C-1").execute();
                    Command.parse(student2Context, "selectCourse C-2").execute();
                    Command.parse(student2Context, "selectCourse C-3").execute();
                    Command.parse(student2Context, "selectCourse C-4").execute();
                    Command.parse(student2Context, "selectCourse C-5").execute();
                    Command.parse(student2Context, "selectCourse C-6").execute();
                    Command.parse(student2Context, "selectCourse C-7").execute();
                    Command.parse(student2Context, "selectCourse C-8").execute();
                    Command.parse(student2Context, "selectCourse C-9").execute();
                    Command.parse(student2Context, "selectCourse C-10").execute();
                }

                @Nested
                class WithSingleStudent {
                    private Context context;

                    @BeforeEach
                    void prepareStudent() {
                        context = student0Context;
                    }

                    @Test
                    void illegalCourseID() {
                        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "cancelCourse C-0").execute());
                        assertEquals("Illegal course id", e.toString());

                        e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "cancelCourse 100").execute());
                        assertEquals("Illegal course id", e.toString());

                        e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "cancelCourse C-1a").execute());
                        assertEquals("Illegal course id", e.toString());
                    }

                    @Test
                    void simpleSuccess() {
                        Command.parse(context, "cancelCourse C-1");
                    }

                    @Test
                    void notSelected() {
                        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "cancelCourse C-11").execute());
                        assertEquals("Course does not exist", e.toString());
                    }

                    @Test
                    void alreadyCancelled() {
                        Command.parse(context, "cancelCourse C-1").execute();
                        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "cancelCourse C-1").execute());
                        assertEquals("Course does not exist", e.toString());
                    }
                }
            }

            @Nested
            class WithTeacher {
                private Context context;

                @BeforeEach
                void prepareTeacher() {
                    context = teacher0Context;
                }

                @Test
                void courseDoesExist() {
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "cancelCourse C-100").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void courseDoesNotBelongToSelf() {
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "cancelCourse C-9").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void courseAlreadyCanceled() {
                    Command.parse(context, "cancelCourse C-1").execute();
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "cancelCourse C-1").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void simpleSuccess() {
                    Command.parse(context, "cancelCourse C-1").execute();
                }

                @Nested
                class WithStudentSelected {
                    private Context student0Context;

                    @BeforeEach
                    void prepareSelectCourse() {
                        student0Context = server.getContext();
                        Command.parse(student0Context, "login 23371001 AAA111@@@").execute();
                        Command.parse(student0Context, "selectCourse C-1").execute();
                    }

                    @Test
                    void syncCancelled() {
                        Command.parse(context, "cancelCourse C-1").execute();
                        Student currentStudent = (Student) student0Context.getCurrentUser();
                        assertFalse(currentStudent.getCourses().containsKey("Deep_Rock_Galactic_0"));
                    }

                    @Test
                    void cannotSelectAfterCancel() {
                        Command.parse(context, "cancelCourse C-1").execute();
                        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(student0Context, "selectCourse C-1").execute());
                        assertEquals("Course does not exist", e.toString());
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
                void simpleSuccess() {
                    Command.parse(context, "cancelCourse C-1").execute();
                }

                @Test
                void courseNotExist() {
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "cancelCourse C-100").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void courseAlreadyCancelled() {
                    Command.parse(context, "cancelCourse C-1").execute();
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "cancelCourse C-1").execute());
                    assertEquals("Course does not exist", e.toString());

                }

                @Nested
                class WithStudentSelected {
                    private Context student0Context;

                    @BeforeEach
                    void prepareSelectCourse() {
                        student0Context = server.getContext();
                        Command.parse(student0Context, "login 23371001 AAA111@@@").execute();
                        Command.parse(student0Context, "selectCourse C-1").execute();
                    }

                    @Test
                    void syncCancelled() {
                        Command.parse(context, "cancelCourse C-1").execute();
                        Student currentStudent = (Student) student0Context.getCurrentUser();
                        assertFalse(currentStudent.getCourses().containsKey("Deep_Rock_Galactic_0"));
                    }

                    @Test
                    void cannotSelectAfterCancel() {
                        Command.parse(context, "cancelCourse C-1").execute();
                        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(student0Context, "selectCourse C-1").execute());
                        assertEquals("Course does not exist", e.toString());
                    }
                }

            }
        }
    }
}