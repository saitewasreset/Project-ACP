package saite.acp.util;

import java.io.Serializable;

public class Range<T extends Comparable<T>> implements Serializable {
    private T begin;
    private T end;
    private boolean closed;

    public Range(T begin, T end) {
        this(begin, end, true);
    }

    // open: [begin, end)
    // closed: [begin, end]
    public Range(T begin, T end, boolean closed) {
        this.begin = begin;
        this.end = end;
        this.closed = closed;
    }

    public boolean checkValue(T value) {
        if (this.closed) {
            return value.compareTo(this.begin) >= 0 && value.compareTo(this.end) <= 0;
        } else {
            return value.compareTo(this.begin) >= 0 && value.compareTo(this.end) < 0;
        }
    }
}
