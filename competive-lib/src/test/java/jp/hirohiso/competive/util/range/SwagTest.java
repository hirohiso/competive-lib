package jp.hirohiso.competive.util.range;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class SwagTest {
    @Test
    void キューとの非可換集約比較() {
        var swag =
                new SlidingWindowAggregationMain.SlidingWindowAggregation<String>(
                        String::concat, () -> "");
        var queue = new ArrayDeque<String>();
        var rng = new Random(42);
        assertEquals("", swag.cal());
        for (int step = 0; step < 300; step++) {
            if (queue.isEmpty() || rng.nextBoolean()) {
                String x = "" + (char) ('a' + rng.nextInt(26));
                queue.add(x);
                swag.push(x);
            } else assertEquals(queue.remove(), swag.pop());
            assertEquals(String.join("", queue), swag.cal(), "step=" + step + ", queue=" + queue);
        }
        while (!queue.isEmpty()) {
            assertEquals(queue.remove(), swag.pop());
            assertEquals(String.join("", queue), swag.cal());
        }
    }
}
