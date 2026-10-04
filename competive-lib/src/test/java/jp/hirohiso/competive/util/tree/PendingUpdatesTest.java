package jp.hirohiso.competive.util.tree;

import static jp.hirohiso.competive.util.tree.LazySegmentTreeTest.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class PendingUpdatesTest {
    @Test
    void 照会で伝播する前に次の操作を行う() {
        var rng = new Random(998244353);
        for (int n : new int[] {2, 4, 7, 8, 9, 16, 31}) {
            Sum[] init = new Sum[n];
            Arrays.setAll(init, i -> new Sum(i + 1, 1));
            long[] a = new long[n];
            Arrays.setAll(a, i -> i + 1);
            var bottom =
                    new LazySegmentTreeBottom.LazySegmentTree<Sum, Affine>(
                            init, OP, E, MAP, LazySegmentTreeTest::compose, ID);
            var recursive =
                    new LazySegmentationTree.LazySegmentTree<Sum, Affine>(
                            init, OP, MAP, (g, f) -> compose(f, g), E, ID);
            for (int step = 0; step < 300; step++) {
                int l = rng.nextInt(n + 1), r = l + rng.nextInt(n - l + 1), p = rng.nextInt(n);
                var f = new Affine(rng.nextInt(2), rng.nextInt(10));
                switch (rng.nextInt(8)) {
                    case 0, 1, 2 -> {
                        bottom.apply(l, r, f);
                        recursive.apply(l, r, f);
                        for (int i = l; i < r; i++) a[i] = f.a() * a[i] + f.b();
                    }
                    case 3 -> {
                        bottom.apply(p, f);
                        recursive.apply(p, p + 1, f);
                        a[p] = f.a() * a[p] + f.b();
                    }
                    case 4 -> {
                        bottom.set(p, new Sum(f.b(), 1));
                        recursive.apply(p, p + 1, new Affine(0, f.b()));
                        a[p] = f.b();
                    }
                    case 5 -> {
                        long sum = 0;
                        for (int i = l; i < r; i++) sum += a[i];
                        assertEquals(new Sum(sum, r - l), bottom.prod(l, r));
                        assertEquals(new Sum(sum, r - l), recursive.getRange(l, r));
                    }
                    case 6 -> {
                        assertEquals(new Sum(a[p], 1), bottom.get(p));
                        assertEquals(new Sum(Arrays.stream(a).sum(), n), bottom.allProd());
                    }
                    case 7 -> {
                        long limit = rng.nextInt(30);
                        int right = l;
                        long sum = 0;
                        while (right < n && sum + a[right] <= limit) sum += a[right++];
                        assertEquals(
                                right,
                                bottom.maxRight(l, x -> x.sum() <= limit),
                                "step=" + step + ", n=" + n);
                        int left = r;
                        sum = 0;
                        while (left > 0 && sum + a[left - 1] <= limit) sum += a[--left];
                        assertEquals(left, bottom.minLeft(r, x -> x.sum() <= limit));
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                assertEquals(new Sum(a[i], 1), bottom.get(i));
                assertEquals(new Sum(a[i], 1), recursive.getRange(i, i + 1));
            }
        }
    }

    @Test
    void 非可換の区間積は左右の順序を守る() {
        String[] a = {"a", "b", "c", "d", "e"};
        var t =
                new LazySegmentTreeBottom.LazySegmentTree<String, Boolean>(
                        a,
                        String::concat,
                        () -> "",
                        (f, x) -> f ? x.toUpperCase() : x,
                        (f, g) -> f || g,
                        () -> false);
        t.apply(1, 4, true);
        assertEquals("aBCDe", t.prod(0, 5));
        assertEquals("BCD", t.prod(1, 4));
        assertEquals(3, t.maxRight(0, s -> "aBC".startsWith(s)));
        assertEquals(1, t.minLeft(4, s -> "BCD".endsWith(s)));
    }
}
