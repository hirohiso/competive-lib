package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class SegmentTreeTest {
    @Test
    void 更新と区間積と二分探索() {
        var rng = new Random(715);
        for (int n : new int[] {1, 2, 3, 8, 9, 17}) {
            Long[] a = new Long[n];
            Arrays.fill(a, 0L);
            var t = new SegmentationTree.SegmentTree<Long>(a, Long::sum, () -> 0L);
            for (int step = 0; step < 60; step++) {
                int p = rng.nextInt(n);
                long v = rng.nextInt(10);
                if (step % 2 == 0) {
                    a[p] = v;
                    t.update(p, v);
                } else {
                    a[p] += v;
                    t.merge(p, v);
                }
                assertEquals(a[p], t.get(p));
                for (int l = 0; l <= n; l++)
                    for (int r = l; r <= n; r++) {
                        long sum = 0;
                        for (int i = l; i < r; i++) sum += a[i];
                        assertEquals(sum, t.getRange(l, r));
                    }
                long limit = rng.nextInt(30);
                for (int l = 0; l <= n; l++) {
                    int r = l;
                    long s = 0;
                    while (r < n && s + a[r] <= limit) s += a[r++];
                    assertEquals(r, t.maxRight(l, x -> x <= limit));
                }
                for (int r = 0; r <= n; r++) {
                    int l = r;
                    long s = 0;
                    while (l > 0 && s + a[l - 1] <= limit) s += a[--l];
                    assertEquals(l, t.minLeft(r, x -> x <= limit));
                }
            }
        }
    }

    @Test
    void 非可換演算と単位元省略コンストラクタ() {
        var t =
                new SegmentationTree.SegmentTree<String>(
                        new String[] {"a", "b", "c"}, String::concat);
        assertEquals("abc", t.getRange(0, 3));
        assertEquals("bc", t.getRange(1, 3));
        var sized = new SegmentationTree.SegmentTree<String>(3, String::concat);
        sized.update(1, "x");
        assertEquals("x", sized.getRange(0, 3));
        var zeros = new SegmentationTree.SegmentTree<Long>(3, Long::sum, () -> 0L);
        assertEquals(0L, zeros.getRange(0, 3));
    }
}
