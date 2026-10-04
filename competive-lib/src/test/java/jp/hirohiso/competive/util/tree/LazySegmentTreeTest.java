package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;
import java.util.function.*;

class LazySegmentTreeTest {
    record Sum(long sum, int size) {}

    record Affine(long a, long b) {}

    static final BinaryOperator<Sum> OP = (x, y) -> new Sum(x.sum + y.sum, x.size + y.size);
    static final Supplier<Sum> E = () -> new Sum(0, 0);
    static final Supplier<Affine> ID = () -> new Affine(1, 0);
    static final BiFunction<Affine, Sum, Sum> MAP =
            (f, x) -> new Sum(f.a * x.sum + f.b * x.size, x.size);

    static Affine compose(Affine f, Affine g) {
        return new Affine(f.a * g.a, f.a * g.b + f.b);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void 区間アフィン更新を配列と比較(boolean bottom) {
        var rng = new Random(424);
        for (int n : new int[] {1, 2, 3, 7, 8, 9}) {
            long[] a = new long[n];
            Sum[] initial = new Sum[n];
            Arrays.fill(initial, new Sum(0, 1));
            // 再帰版は (古い作用, 新しい作用)、反復版は (新しい作用, 古い作用)。
            var recursive =
                    new LazySegmentationTree.LazySegmentTree<Sum, Affine>(
                            initial, OP, MAP, (g, f) -> compose(f, g), E, ID);
            var iterative =
                    new LazySegmentTreeBottom.LazySegmentTree<Sum, Affine>(
                            initial, OP, E, MAP, LazySegmentTreeTest::compose, ID);
            for (int step = 0; step < 80; step++) {
                int l = step % 5 == 0 ? 0 : rng.nextInt(n + 1),
                        r = step % 5 == 0 ? n : l + rng.nextInt(n - l + 1);
                var f = new Affine(rng.nextInt(2), rng.nextInt(10));
                if (bottom) iterative.apply(l, r, f);
                else recursive.apply(l, r, f);
                for (int i = l; i < r; i++) a[i] = f.a * a[i] + f.b;
                for (int x = 0; x <= n; x++)
                    for (int y = x; y <= n; y++) {
                        long s = 0;
                        for (int i = x; i < y; i++) s += a[i];
                        assertEquals(
                                new Sum(s, y - x),
                                bottom ? iterative.prod(x, y) : recursive.getRange(x, y),
                                "n=" + n + ", step=" + step + ", [" + x + "," + y + ")");
                    }
                if (bottom) {
                    long total = Arrays.stream(a).sum();
                    assertEquals(new Sum(total, n), iterative.allProd());
                    int p = rng.nextInt(n);
                    assertEquals(new Sum(a[p], 1), iterative.get(p));
                    iterative.set(p, new Sum(3, 1));
                    a[p] = 3;
                    iterative.apply(p, new Affine(1, 2));
                    a[p] += 2;
                    long limit = rng.nextInt(30);
                    for (int x = 0; x <= n; x++) {
                        int y = x;
                        long s = 0;
                        while (y < n && s + a[y] <= limit) s += a[y++];
                        assertEquals(y, iterative.maxRight(x, v -> v.sum <= limit));
                    }
                    for (int y = 0; y <= n; y++) {
                        int x = y;
                        long s = 0;
                        while (x > 0 && s + a[x - 1] <= limit) s += a[--x];
                        assertEquals(x, iterative.minLeft(y, v -> v.sum <= limit));
                    }
                }
            }
        }
    }
}
