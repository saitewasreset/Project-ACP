package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import saite.acp.server.Context;
import saite.acp.server.Server;

import static org.junit.jupiter.api.Assertions.*;

class CreateCourseCommandTest {
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.context = new Server().getContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"createCourse", "createCourse Deep_Rock_Galactic", "createCourse Deep_Rock_Galactic 2_8-9", "createCourse Deep_Rock_Galactic 2_8-9 5.0 64 64"})
    void illegalArgumentCount(String command) {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, command).execute());

        assertEquals(e.toString(), "Illegal argument count");
    }

    @Test
    void notLogin() {
        NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "createCourse Deep_Rock_Galactic 2_8-9 5.0 64").execute());
        assertEquals(e.toString(), "No one is online");
    }

    @Nested
    class WithUsers {
        @BeforeEach
        void prepareUsers() {
            Command.parse(context, "register 23371001 Scout AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register SY2221118 Engineer AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register BY2221118 Gunner AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register 12345 Driller AAA111@@@ AAA111@@@ Teacher").execute();
            Command.parse(context, "register AD123 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute();
        }

        @Nested
        class WithStudent {
            @BeforeEach
            void loginUser() {
                Command.parse(context, "login 23371001 AAA111@@@").execute();
            }

            @Test
            void permissionDenied() {
                PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "createCourse Deep_Rock_Galactic 2_8-9 5.0 64").execute());
                assertEquals("Permission denied", e.toString());
            }
        }

        @Nested
        class WithAdministrator {
            @BeforeEach
            void loginUser() {
                Command.parse(context, "login AD123 AAA111@@@").execute();
            }

            @Test
            void permissionDenied() {
                PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "createCourse Deep_Rock_Galactic 2_8-9 5.0 64").execute());
                assertEquals("Permission denied", e.toString());
            }
        }

        @Nested
        class WithTeacher {
            @BeforeEach
            void loginUser() {
                Command.parse(context, "login 12345 AAA111@@@").execute();
            }

            @Test
            void simpleSuccess() {
                Command.parse(context, "createCourse Deep_Rock_Galactic 2_8-9 5.0 64").execute();
            }

            @Test
            void successBelowCourseLimit() {
                Command.parse(context, "createCourse Deep_Rock_Galactic_0 1_1-2 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_1 1_3-4 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_2 1_5-6 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_3 1_7-8 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_4 1_9-10 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_5 1_11-12 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_6 1_13-14 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_7 2_1-2 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_8 2_3-4 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_9 2_5-6 5.0 64").execute();
            }

            @Test
            void exceedCourseLimit() {
                Command.parse(context, "createCourse Deep_Rock_Galactic_0 1_1-2 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_1 1_3-4 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_2 1_5-6 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_3 1_7-8 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_4 1_9-10 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_5 1_11-12 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_6 1_13-14 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_7 2_1-2 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_8 2_3-4 5.0 64").execute();
                Command.parse(context, "createCourse Deep_Rock_Galactic_9 2_5-6 5.0 64").execute();

                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "createCourse Deep_Rock_Galactic_9 3_10-11 5.0 64").execute());

                assertEquals("Course count reaches limit", e.toString());
            }

            @ParameterizedTest
            @ValueSource(strings = {"Rock_And-Stone", "aaaaa", "AAAAA", "A", "AAAAAAAAAAAAAAAAAAAA"})
            void validCourseName(String courseName) {
                String formatted = String.format("createCourse %s 1_1-2 5.0 64", courseName);
                Command.parse(context, formatted).execute();
            }

            @ParameterizedTest
            @ValueSource(strings = {"1-_", "_A1-", "-A1_", "1A-_", "AAAAAAAAAAAAAAAAAAAAA", "AAAAA@"})
            void illegalCourseName(String courseName) {
                String formatted = String.format("createCourse %s 1_1-2 5.0 64", courseName);
                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, formatted).execute());

                assertEquals("Illegal course name", e.toString());
            }

            @Test
            void courseNameExist() {
                Command.parse(context, "createCourse Deep_Rock_Galactic 1_1-2 5.0 64").execute();
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "createCourse Deep_Rock_Galactic 1_2-3 5.0 64").execute());

                assertEquals("Course name exists", e.toString());
            }

            @Test
            void illegalCourseTime() {
                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "createCourse Deep_Rock_Galactic 1_1-2-3 5.0 64").execute());

                assertEquals("Illegal course time", e.toString());
            }

            @Test
            void courseTimeConflict() {
                Command.parse(context, "createCourse Deep_Rock_Galactic_0 1_2-3 5.0 64").execute();
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "createCourse Deep_Rock_Galactic_1 1_1-2 5.0 64").execute());
                assertEquals("Course time conflicts", e.toString());
            }

            @ParameterizedTest
            @ValueSource(strings = {"0", "0.0", "12.1"})
            void illegalCourseCredit(String credit) {
                String command = String.format("createCourse Deep_Rock_Galactic_0 1_2-3 %s 64", credit);

                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, command).execute());

                assertEquals("Illegal course credit", e.toString());
            }

            @ParameterizedTest
            @ValueSource(strings = {"0.1", "12", "12.0"})
            void validCourseCredit(String credit) {
                String command = String.format("createCourse Deep_Rock_Galactic_0 1_2-3 %s 64", credit);

                Command.parse(context, command).execute();

            }

            @ParameterizedTest
            @ValueSource(strings = {"64", "32", "1280"})
            void validClassHour(String classHour) {
                String command = String.format("createCourse Deep_Rock_Galactic_0 1_2-3 5.0 %s", classHour);

                Command.parse(context, command).execute();

            }

            @ParameterizedTest
            @ValueSource(strings = {"64.0", "32.5", "0", "0.0", "1280.1", "1281"})
            void illegalClassHour(String classHour) {
                String command = String.format("createCourse Deep_Rock_Galactic_0 1_2-3 5.0 %s", classHour);

                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, command).execute());

                assertEquals("Illegal course period", e.toString());
            }
        }
    }
}