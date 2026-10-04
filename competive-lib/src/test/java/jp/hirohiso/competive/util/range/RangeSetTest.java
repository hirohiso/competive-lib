package jp.hirohiso.competive.util.range;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class RangeSetTest {
    @Test
    void 挿入削除と共通部分を整数集合と比較() {
        var rng = new Random(341);
        var set = new RangeSet();
        Set<Integer> expected = new HashSet<>();
        for (int step = 0; step < 300; step++) {
            int l = rng.nextInt(30) - 15, r = l + 1 + rng.nextInt(10);
            if (rng.nextBoolean()) {
                set.insert(new Range(l, r));
                for (int i = l; i < r; i++) expected.add(i);
            } else {
                Set<Integer> removed = new HashSet<>();
                for (int i = l; i < r; i++) if (expected.remove(i)) removed.add(i);
                Set<Integer> actual = new HashSet<>();
                for (var a : set.remove(new Range(l, r))) {
                    assertTrue(a.l() < a.r());
                    for (long i = a.l(); i < a.r(); i++) actual.add((int) i);
                }
                assertEquals(removed, actual);
            }
            for (int i = -20; i <= 25; i++)
                assertEquals(expected.contains(i), set.get(i) != null, "step=" + step + ", i=" + i);
            for (int i : expected) {
                int left = i, right = i + 1;
                while (expected.contains(left - 1)) left--;
                while (expected.contains(right)) right++;
                assertEquals(new Range(left, right), set.get(i));
            }
            var other = new RangeSet();
            other.insert(new Range(-4, 8));
            var both = set.and(other);
            for (int i = -20; i <= 25; i++)
                assertEquals(expected.contains(i) && i >= -4 && i < 8, both.get(i) != null);
        }
    }

    @Test
    void 隣接区間の統合と左右の区間() {
        var s = new RangeSet();
        s.insert(new Range(1, 3));
        s.insert(new Range(3, 5));
        s.insert(new Range(8, 10));
        assertEquals(new Range(1, 5), s.get(3));
        assertNull(s.get(5));
        assertEquals(new Range(1, 5), s.getLeft(8));
        assertEquals(new Range(8, 10), s.getRight(5));
        assertEquals(new Range(1, 5), s.getLeft(5));
        assertEquals(new Range(Long.MAX_VALUE, Long.MAX_VALUE), s.getRight(8));
        assertEquals(new Range(Long.MIN_VALUE, Long.MIN_VALUE), s.getLeft(0));
        assertEquals(new Range(Long.MAX_VALUE, Long.MAX_VALUE), s.getRight(10));
        assertNull(new RangeSet().and(s).get(2));
    }
}
