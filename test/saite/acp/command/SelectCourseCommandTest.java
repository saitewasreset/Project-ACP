package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import saite.acp.course.Course;
import saite.acp.server.Context;
import saite.acp.server.Server;
import saite.acp.user.Student;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SelectCourseCommandTest {
    private Server server;
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.server = new Server();
        this.context = server.getContext();
    }

    @Test
    void illegalArgumentCount() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "selectCourse"));
        assertEquals("Illegal argument count", e.toString());

        e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "selectCourse 1 2"));
        assertEquals("Illegal argument count", e.toString());
    }

    @Test
    void notLogin() {
        NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "selectCourse C-1").execute());
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

            @Test
            void courseCapacity() {
                Course targetCourse = server.getCourses().get(1);

                int courseCapacity = targetCourse.getCapacity();

                for (int i = 0; i < courseCapacity + 1; i++) {
                    int studentID = 23371020 + i;
                    String studentName = String.format("Scout", i);
                    Command.parse(context, String.format("register %d %s AAA111@@@ AAA111@@@ Student", studentID, studentName)).execute();
                }

                ArrayList<Context> studentContextList = new ArrayList<>();

                for (int i = 0; i < courseCapacity + 1; i++) {
                    Context currentContext = server.getContext();

                    Command.parse(currentContext, String.format("login %d AAA111@@@", 23371020 + i)).execute();

                    studentContextList.add(currentContext);
                }

                for (int i = 0; i < courseCapacity; i++) {
                    Command.parse(studentContextList.get(i), "selectCourse C-1").execute();
                }

                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(studentContextList.get(courseCapacity), "selectCourse C-1").execute());
                assertEquals("Course capacity is full", e.toString());
            }

            @Nested
            class WithStudent {
                @BeforeEach
                void loginStudent() {
                    Command.parse(context, "login 23371001 AAA111@@@").execute();
                }

                @Test
                void illegalCourseID() {
                    IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "selectCourse C-0").execute());
                    assertEquals("Illegal course id", e.toString());

                    e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "selectCourse 100").execute());
                    assertEquals("Illegal course id", e.toString());

                    e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "selectCourse C-1a").execute());
                    assertEquals("Illegal course id", e.toString());
                }

                @Test
                void courseNotExist() {
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "selectCourse C-100").execute());
                    assertEquals("Course does not exist", e.toString());
                }

                @Test
                void simpleSuccess() {
                    Command.parse(context, "selectCourse C-10").execute();
                    Command.parse(context, "selectCourse C-9").execute();

                    Student currentStudent = (Student) context.getCurrentUser();
                    assertEquals(2, currentStudent.getCourses().size());
                }

                @Test
                void courseTimeConflict() {
                    Command.parse(context, "selectCourse C-6").execute();
                    CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "selectCourse C-11").execute());
                    assertEquals("Course time conflicts", e.toString());
                }

                @Nested
                class WithCanceledCourse {
                    @BeforeEach
                    void cancelCourse() {
                        Command.parse(teacher1Context, "cancelCourse C-10").execute();
                    }

                    @Test
                    void courseAlreadyCanceled() {
                        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "selectCourse C-10").execute());
                        assertEquals("Course does not exist", e.toString());
                    }
                }
            }

            @Nested
            class WithTeacher {
                @BeforeEach
                void prepareTeacher() {
                    context = teacher0Context;
                }

                @Test
                void permissionDenied() {
                    PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "selectCourse C-1").execute());
                    assertEquals("Permission denied", e.toString());
                }
            }

            @Nested
            class WithAdministrator {
                @BeforeEach
                void loginAdministrator() {
                    Command.parse(context, "login AD123 AAA111@@@").execute();
                }

                @Test
                void permissionDenied() {
                    PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "selectCourse C-1").execute());
                    assertEquals("Permission denied", e.toString());
                }
            }
        }
    }
}