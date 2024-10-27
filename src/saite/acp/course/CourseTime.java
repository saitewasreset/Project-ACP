package saite.acp.course;

import saite.acp.command.CommandException;
import saite.acp.command.IllegalArgumentContentException;
import saite.acp.util.Range;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseTime implements Serializable {
    private int day;
    private int begin;
    private int end;

    private static final Pattern courseTimePattern = Pattern.compile("^([0-9]+)_([0-9]+)-([0-9]+)$");

    private static final Range<Integer> dayRange = new Range<Integer>(1, 7);
    private static final Range<Integer> classTimeRange = new Range<Integer>(1, 14);

    public CourseTime(String rawCourseTime) throws CommandException {
        Matcher m = courseTimePattern.matcher(rawCourseTime);
        if (!m.find()) {
            throw new IllegalArgumentContentException("course time");
        }


        String dayStr = m.group(1);
        String beginStr = m.group(2);
        String endStr = m.group(3);

        try {
            int day = Integer.parseInt(dayStr);
            int begin = Integer.parseInt(beginStr);
            int end = Integer.parseInt(endStr);

            if (!dayRange.checkValue(day)) {
                throw new IllegalArgumentContentException("course time");
            }

            if (!classTimeRange.checkValue(begin)) {
                throw new IllegalArgumentContentException("course time");
            }

            if (!classTimeRange.checkValue(end)) {
                throw new IllegalArgumentContentException("course time");
            }

            if (begin > end) {
                throw new IllegalArgumentContentException("course time");
            }

            this.day = day;
            this.begin = begin;
            this.end = end;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentContentException("course time");
        }
    }

    public boolean checkConflict(CourseTime other) {
        if (other.day != this.day) {
            return false;
        } else {
            if (other.end < this.begin) {
                return false;
            } else return other.begin <= this.end;
        }
    }

    @Override
    public String toString() {
        return String.format("%d_%d-%d", this.day, this.begin, this.end);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CourseTime other) {
            return (other.begin == this.begin) && (other.end == this.end) && (other.day == this.day);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) ((((long) this.day) << 48) + (((long) this.end) << 24) + ((long) this.begin));
    }
}
