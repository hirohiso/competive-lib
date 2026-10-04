package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Random;

class FenwickTreeTest {
    @Test
    void longの和と配列初期化() {
        var bit = new FenwickTreeMain.FenwickTree(3);
        bit.add(0, 2_000_000_000L);
        bit.add(1, 2_000_000_000L);
        assertEquals(4_000_000_000L, bit.range(0, 3));
        var generic =
                new FenwickTreeMain.GenericFenwickTree<>(
                        new Long[] {2L, 3L, 5L}, Long::sum, x -> -x, () -> 0L);
        assertEquals(10L, generic.range(0, 3));
    }

    @Test
    void 配列との乱択比較() {
        var random = new Random(20261004);
        for (int n : new int[] {1, 2, 3, 7, 8, 9, 31}) {
            long[] a = new long[n];
            var bit = new FenwickTreeMain.FenwickTree(n);
            var generic =
                    new FenwickTreeMain.GenericFenwickTree<Long>(n, Long::sum, x -> -x, () -> 0L);
            for (int step = 0; step < 150; step++) {
                int p = random.nextInt(n);
                long v = random.nextInt(200) - 100;
                a[p] += v;
                bit.add(p, v);
                generic.add(p, v);
                for (int l = 0; l <= n; l++)
                    for (int r = l; r <= n; r++) {
                        long sum = 0;
                        for (int i = l; i < r; i++) sum += a[i];
                        assertEquals(
                                sum,
                                bit.range(l, r),
                                "n=" + n + ", step=" + step + ", l=" + l + ", r=" + r);
                        assertEquals(sum, generic.range(l, r));
                    }
            }
        }
    }
}
