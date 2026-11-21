package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class DynamicDualSegmentTreeTest {
    record Affine(long a, long b) {
        long apply(long x) {
            return a * x + b;
        }

        // this の後に g を適用する合成
        Affine then(Affine g) {
            return new Affine(g.a * a, g.a * b + g.b);
        }
    }

    static DynamicDualSegmentTreeMain.DynamicDualSegmentTree<Long, Affine> affineTree(long lo, long hi) {
        return new DynamicDualSegmentTreeMain.DynamicDualSegmentTree<>(
                lo, hi,
                (f, x) -> f.apply(x),
                (f, g) -> g.then(f), // composition(new,old)
                i -> 3 * i + 1);
    }

    @Test
    void 非可換な更新列を配列と比較() {
        var rng = new Random(88);
        for (int n : new int[] {1, 2, 3, 8, 9}) {
            for (long lo : new long[] {0, -7}) {
                long[] a = new long[n];
                for (int i = 0; i < n; i++) a[i] = 3 * (lo + i) + 1;
                var t = affineTree(lo, lo + n);
                // 初期値だけの状態も確認する
                for (int i = 0; i < n; i++) assertEquals(a[i], t.get(lo + i));
                for (int step = 0; step < 100; step++) {
                    int l = step == 0 ? 0 : rng.nextInt(n + 1),
                            r = step == 0 ? n : l + rng.nextInt(n - l + 1);
                    var f = new Affine(rng.nextInt(2), rng.nextInt(10));
                    t.apply(lo + l, lo + r, f);
                    for (int i = l; i < r; i++) a[i] = f.apply(a[i]);
                    if (step % 2 == 1) continue;
                    for (int i = 0; i < n; i++)
                        assertEquals(a[i], t.get(lo + i), "n=" + n + ", lo=" + lo + ", step=" + step + ", i=" + i);
                }
            }
        }
    }

    @Test
    void 広い添字範囲を基本区間に分けて比較() {
        var rng = new Random(12);
        long n = 1_000_000_000_000_000_000L;
        var points = new TreeSet<Long>(List.of(-n, n));
        while (points.size() < 12) points.add(Math.floorMod(rng.nextLong(), 2 * n) - n);
        Long[] b = points.toArray(Long[]::new);
        // 基本区間ごとに、これまでの作用を合成したものを持つ
        Affine[] acc = new Affine[b.length - 1];
        Arrays.fill(acc, new Affine(1, 0));
        var t = affineTree(-n, n);
        for (int step = 0; step < 300; step++) {
            int x = rng.nextInt(b.length), y = rng.nextInt(b.length);
            int l = Math.min(x, y), r = Math.max(x, y);
            var f = new Affine(rng.nextInt(3), rng.nextInt(1000));
            t.apply(b[l], b[r], f);
            for (int i = l; i < r; i++) acc[i] = acc[i].then(f);
            for (int q = 0; q < 3; q++) {
                int k = rng.nextInt(acc.length);
                long p = b[k] + Math.floorMod(rng.nextLong(), b[k + 1] - b[k]);
                assertEquals(acc[k].apply(3 * p + 1), t.get(p));
            }
        }
    }

    @Test
    void 定数初期値のコンストラクタと範囲外() {
        var t = new DynamicDualSegmentTreeMain.DynamicDualSegmentTree<Long, Long>(10, Math::max, Math::max, () -> 0L);
        assertEquals(0L, t.get(9));
        t.apply(2, 5, 4L);
        t.apply(0, 10, 2L);
        t.apply(3, 3, 100L);
        assertEquals(2L, t.get(1));
        assertEquals(4L, t.get(3));
        assertEquals(2L, t.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> t.get(10));
        assertThrows(IndexOutOfBoundsException.class, () -> t.apply(-1, 3, 1L));
        assertThrows(IndexOutOfBoundsException.class, () -> t.apply(4, 3, 1L));
        assertThrows(IllegalArgumentException.class, () -> affineTree(1, 0));
    }
}
