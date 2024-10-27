package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import saite.acp.server.Context;
import saite.acp.server.Server;

import static org.junit.jupiter.api.Assertions.*;

class ListStudentCommandTest {
    private Server server;
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.server = new Server();
        this.context = server.getContext();
    }

    @Test
    void illegalArgumentCount() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "listStudent C-1 C-2").execute());
        assertEquals("Illegal argument count", e.toString());
    }

    @Test
    void notLogin() {
        NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "listStudent C-1").execute());
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
        void permissionDenied() {
            Command.parse(context, "login 23371001 AAA111@@@").execute();
            PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "listStudent C-1").execute());
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

            @ParameterizedTest
            @ValueSource(strings = {"C-0", "1", "C-A", "C-1-2"})
            void illegalCourseID(String rawCourseID) {
                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(teacher0Context, "listStudent " + rawCourseID).execute());
                assertEquals("Illegal course id", e.toString());
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
                void courseNotRegistered() {
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher0Context, "listStudent C-15").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void courseCancelled() {
                    Command.parse(teacher0Context, "cancelCourse C-1").execute();
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher0Context, "listStudent C-1").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void courseNotBelongToSelf() {
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(teacher2Context, "listStudent C-1").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Nested
                class WithStudents {
                    private Context student0Context;
                    private Context student1Context;
                    private Context student2Context;

                    @BeforeEach
                    void registerStudents() {
                        student0Context = server.getContext();
                        student1Context = server.getContext();
                        student2Context = server.getContext();
                        Command.parse(student0Context, "register 23161001 Scout AAA111@@@ AAA111@@@ Student").execute();
                        Command.parse(student1Context, "register 23161002 Engineer AAA111@@@ AAA111@@@ Student").execute();
                        Command.parse(student2Context, "register 23161003 Gunner AAA111@@@ AAA111@@@ Student").execute();

                        Command.parse(student0Context, "login 23161001 AAA111@@@").execute();
                        Command.parse(student1Context, "login 23161002 AAA111@@@").execute();
                        Command.parse(student2Context, "login 23161003 AAA111@@@").execute();
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
                        void noStudentSelect() {
                            CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(adminContext, "listStudent C-1").execute());
                            assertEquals("Student does not select course", e.toString());
                        }

                        @Test
                        void simpleSuccess() {
                            Command.parse(student0Context, "selectCourse C-1").execute();
                            Command.parse(adminContext, "listStudent C-1").execute();
                        }
                    }

                }


            }
        }
    }

}