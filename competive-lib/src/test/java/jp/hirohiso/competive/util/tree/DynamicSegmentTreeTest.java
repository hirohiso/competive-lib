package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class DynamicSegmentTreeTest {
    @Test
    void 負の添字を含む範囲で配列と比較() {
        var rng = new Random(9101);
        for (int n : new int[] {1, 2, 3, 8, 9, 17}) {
            for (long lo : new long[] {0, -5, -n}) {
                long[] a = new long[n];
                var t = new DynamicSegmentationTree.DynamicSegmentTree<Long>(lo, lo + n, Long::sum, () -> 0L);
                for (int step = 0; step < 60; step++) {
                    int p = rng.nextInt(n);
                    long v = rng.nextInt(10);
                    if (step % 2 == 0) {
                        a[p] = v;
                        t.update(lo + p, v);
                    } else {
                        a[p] += v;
                        t.merge(lo + p, v);
                    }
                    for (int i = 0; i < n; i++) assertEquals(a[i], t.get(lo + i));
                    for (int l = 0; l <= n; l++)
                        for (int r = l; r <= n; r++) {
                            long sum = 0;
                            for (int i = l; i < r; i++) sum += a[i];
                            assertEquals(sum, t.getRange(lo + l, lo + r), "n=" + n + ", lo=" + lo);
                        }
                    long limit = rng.nextInt(30);
                    for (int l = 0; l <= n; l++) {
                        int r = l;
                        long s = 0;
                        while (r < n && s + a[r] <= limit) s += a[r++];
                        assertEquals(lo + r, t.maxRight(lo + l, x -> x <= limit));
                    }
                    for (int r = 0; r <= n; r++) {
                        int l = r;
                        long s = 0;
                        while (l > 0 && s + a[l - 1] <= limit) s += a[--l];
                        assertEquals(lo + l, t.minLeft(lo + r, x -> x <= limit));
                    }
                }
            }
        }
    }

    @Test
    void 広い添字範囲で疎な更新をTreeMapと比較() {
        var rng = new Random(31);
        long n = 1_000_000_000_000_000_000L;
        var t = new DynamicSegmentationTree.DynamicSegmentTree<Long>(n, Long::sum, () -> 0L);
        var map = new TreeMap<Long, Long>();
        long[] keys = new long[20];
        for (int i = 0; i < keys.length; i++) keys[i] = Math.floorMod(rng.nextLong(), n);
        keys[0] = 0;
        keys[1] = n - 1;
        for (int step = 0; step < 200; step++) {
            long k = keys[rng.nextInt(keys.length)];
            long v = rng.nextInt(100);
            t.update(k, v);
            map.put(k, v);
            long x = Math.floorMod(rng.nextLong(), n + 1), y = Math.floorMod(rng.nextLong(), n + 1);
            long l = Math.min(x, y), r = Math.max(x, y);
            long expected = map.subMap(l, r).values().stream().mapToLong(Long::longValue).sum();
            assertEquals(expected, t.getRange(l, r));
            assertEquals(map.getOrDefault(k, 0L), t.get(k));
            long q = Math.floorMod(rng.nextLong(), n);
            assertEquals(map.getOrDefault(q, 0L), t.get(q));
        }
        long total = map.values().stream().mapToLong(Long::longValue).sum();
        assertEquals(total, t.getRange(0, n));
        // 合計が total - 1 以下になる最大の右端は、最後の非零要素の位置
        long last = map.descendingMap().entrySet().stream()
                .filter(en -> en.getValue() > 0).findFirst().orElseThrow().getKey();
        assertEquals(last, t.maxRight(0, s -> s <= total - 1));
        long first = map.entrySet().stream()
                .filter(en -> en.getValue() > 0).findFirst().orElseThrow().getKey();
        assertEquals(first + 1, t.minLeft(n, s -> s <= total - 1));
        assertEquals(n, t.maxRight(0, s -> s <= total));
        assertEquals(0, t.minLeft(n, s -> s <= total));
    }

    @Test
    void 非可換演算と範囲外() {
        var t = new DynamicSegmentationTree.DynamicSegmentTree<String>(-2, 3, String::concat, () -> "");
        t.update(-2, "a");
        t.update(0, "b");
        t.update(2, "c");
        assertEquals("abc", t.getRange(-2, 3));
        assertEquals("bc", t.getRange(-1, 3));
        assertEquals("", t.getRange(1, 1));
        assertEquals("", t.get(1));
        assertEquals(2, t.maxRight(-2, s -> s.length() <= 2));
        assertEquals(-1, t.minLeft(3, s -> s.length() <= 2));
        assertThrows(IndexOutOfBoundsException.class, () -> t.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> t.update(-3, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> t.getRange(2, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new DynamicSegmentationTree.DynamicSegmentTree<String>(0, String::concat, () -> ""));
    }
}
