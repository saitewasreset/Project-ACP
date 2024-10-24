package saite.acp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeTest {

    @Test
    void checkValue() {
        // closed
        Range<Integer> defaultClosed = new Range<>(1, 5);

        for (int i = 1; i <= 5; i++) {
            assertTrue(defaultClosed.checkValue(i));
        }

        assertFalse(defaultClosed.checkValue(0));
        assertFalse(defaultClosed.checkValue(6));

        // closed
        Range<Integer> closed = new Range<>(1, 5, true);

        for (int i = 1; i <= 5; i++) {
            assertTrue(closed.checkValue(i));
        }

        assertFalse(closed.checkValue(0));
        assertFalse(closed.checkValue(6));

        // open
        Range<Integer> open = new Range<>(1, 5, false);

        for (int i = 1; i < 5; i++) {
            assertTrue(open.checkValue(i));
        }

        assertFalse(open.checkValue(0));
        assertFalse(open.checkValue(5));
        assertFalse(open.checkValue(6));

    }
}