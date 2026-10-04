package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegressionTest {
    @Test
    void Fenwick配列構築では内部節点も集約する() {
        var bit =
                new FenwickTreeMain.GenericFenwickTree<>(
                        new Long[] {2L, 3L, 5L}, Long::sum, x -> -x, () -> 0L);
        assertEquals(10L, bit.range(0, 3));
        assertEquals(5L, bit.range(2, 3));
    }

    @Test
    void 双対木の全体更新と部分更新の時系列() {
        var t =
                new DualSegmentTreeMain.DualSegmentTree<Long, DualSegmentTreeTest.Affine>(
                        new Long[] {1L, 2L, 3L, 4L},
                        (f, x) -> f.apply(x),
                        (f, g) ->
                                new DualSegmentTreeTest.Affine(
                                        f.a() * g.a(), f.a() * g.b() + f.b()),
                        () -> 0L,
                        () -> new DualSegmentTreeTest.Affine(1, 0));
        t.apply(0, 4, new DualSegmentTreeTest.Affine(0, 5));
        t.apply(1, 3, new DualSegmentTreeTest.Affine(1, 2));
        assertEquals(5L, t.get(0));
        assertEquals(7L, t.get(1));
        assertEquals(7L, t.get(2));
        assertEquals(5L, t.get(3));
        t.apply(0, 4, new DualSegmentTreeTest.Affine(0, 9));
        assertEquals(9L, t.get(1));
    }

    @Test
    void 遅延木は葉の遅延配列へ書き込まない() {
        var t =
                new LazySegmentTreeBottom.LazySegmentTree<
                        LazySegmentTreeTest.Sum, LazySegmentTreeTest.Affine>(
                        new LazySegmentTreeTest.Sum[] {
                            new LazySegmentTreeTest.Sum(0, 1), new LazySegmentTreeTest.Sum(0, 1)
                        },
                        LazySegmentTreeTest.OP,
                        LazySegmentTreeTest.E,
                        LazySegmentTreeTest.MAP,
                        LazySegmentTreeTest::compose,
                        LazySegmentTreeTest.ID);
        t.apply(0, 2, new LazySegmentTreeTest.Affine(1, 3));
        assertEquals(new LazySegmentTreeTest.Sum(3, 1), t.get(0));
    }

    @Test
    void 双対木の未更新の初期値を取得できる() {
        var tree =
                new DualSegmentTreeMain.DualSegmentTree<Long, Long>(
                        new Long[] {2L, 5L, 7L}, Long::sum, Long::sum, () -> 0L, () -> 0L);
        assertEquals(2L, tree.get(0));
        assertEquals(5L, tree.get(1));
        assertEquals(7L, tree.get(2));
        tree.apply(1, 2, 3L);
        assertEquals(2L, tree.get(0));
        assertEquals(8L, tree.get(1));
        assertEquals(7L, tree.get(2));
    }
}
