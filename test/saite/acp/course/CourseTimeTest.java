package saite.acp.course;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import saite.acp.command.IllegalArgumentContentException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class CourseTimeTest {
    @ParameterizedTest
    @ValueSource(strings = {"1_1-14", "1_2-2", "1_2-2", "7_1-3"})
    void valid(String rawCourseTime) {
        new CourseTime(rawCourseTime);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0_1-14", "8_1-14", "2_8-7", "A_2-3", "2_A-7", "2_A-B", "1_0-1", "1_5-15", "1_1-2-3", "1_1_1-2"})
    void invalid(String rawCourseTime) {
        IllegalArgumentContentException e = assertThrows(IllegalArgumentContentException.class, () -> new CourseTime(rawCourseTime));
        assertEquals("Illegal course time", e.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1_5-7|1_7-8", "1_5-7|1_4-5", "1_5-7|1_6-8"})
    void conflict(String compoundRawCourseTime) {
        String[] split = compoundRawCourseTime.split("\\|");

        CourseTime ct1 = new CourseTime(split[0]);
        CourseTime ct2 = new CourseTime(split[1]);

        assertTrue(ct1.checkConflict(ct2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1_5-7|1_8-9", "1_5-7|2_5-7", "1_5-7|1_2-3"})
    void NotConflict(String compoundRawCourseTime) {
        String[] split = compoundRawCourseTime.split("\\|");

        CourseTime ct1 = new CourseTime(split[0]);
        CourseTime ct2 = new CourseTime(split[1]);

        assertFalse(ct1.checkConflict(ct2));
    }

    @Test
    void testToString() {
        CourseTime ct = new CourseTime("2_8-9");

        assertEquals("2_8-9", ct.toString());
    }

    @Test
    void testEquals() {
        CourseTime a = new CourseTime("1_1-2");
        CourseTime b = new CourseTime("1_1-2");

        assertEquals(a, b);
    }

    @Test
    void testCompare() {
        String[] rawCourseTimeList = {"5_5-6", "5_1-2", "3_7-8", "3_1-2", "1_1-2", "1_3-4"};
        String[] expectedCourseTimeList = {"1_1-2", "1_3-4", "3_1-2", "3_7-8", "5_1-2", "5_5-6"};

        Object[] actualCourseTimeList = Arrays.stream(rawCourseTimeList).map(CourseTime::new).sorted(CourseTime.comparator).map(CourseTime::toString).toArray();

        assertArrayEquals(expectedCourseTimeList, actualCourseTimeList);
    }
}