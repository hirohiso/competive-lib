package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class DualSegmentTreeTest {
    record Affine(long a, long b) {
        long apply(long x) {
            return a * x + b;
        }
    }

    @Test
    void 根の更新と非可換な更新列() {
        var rng = new Random(73);
        for (int n : new int[] {1, 2, 3, 8, 9}) {
            Long[] a = new Long[n];
            Arrays.fill(a, 0L);
            var tree =
                    new DualSegmentTreeMain.DualSegmentTree<Long, Affine>(
                            a,
                            (f, x) -> f.apply(x),
                            (f, g) -> new Affine(f.a * g.a, f.a * g.b + f.b),
                            () -> 0L,
                            () -> new Affine(1, 0));
            for (int step = 0; step < 100; step++) {
                int l = step == 0 ? 0 : rng.nextInt(n),
                        r = step == 0 ? n : l + 1 + rng.nextInt(n - l);
                var f = new Affine(rng.nextInt(2), rng.nextInt(10));
                tree.apply(l, r, f);
                for (int i = l; i < r; i++) a[i] = f.apply(a[i]);
                for (int i = 0; i < n; i++)
                    assertEquals(a[i], tree.get(i), "n=" + n + ", step=" + step + ", i=" + i);
            }
            tree.apply(0, 0, new Affine(0, 999));
            assertEquals(a[0], tree.get(0));
        }
    }
}
