package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.*;

class DynamicLazySegmentTreeTest {
    record Sum(long sum, long size) {}

    record Affine(long a, long b) {
        long apply(long x) {
            return a * x + b;
        }
    }

    static final BinaryOperator<Sum> OP = (x, y) -> new Sum(x.sum + y.sum, x.size + y.size);
    static final BiFunction<Affine, Sum, Sum> MAP = (f, x) -> new Sum(f.a * x.sum + f.b * x.size, x.size);
    // composition(old,new) = new ∘ old
    static final BinaryOperator<Affine> COMP = (g, f) -> new Affine(f.a * g.a, f.a * g.b + f.b);

    static DynamicLazySegmentationTree.DynamicLazySegmentTree<Sum, Affine> sumTree(long lo, long hi) {
        return new DynamicLazySegmentationTree.DynamicLazySegmentTree<>(
                lo, hi, OP, MAP, COMP, () -> new Sum(0, 0), (l, r) -> new Sum(0, r - l));
    }

    @Test
    void 区間アフィン更新を配列と比較() {
        var rng = new Random(2024);
        for (int n : new int[] {1, 2, 3, 7, 8, 9}) {
            for (long lo : new long[] {0, -4}) {
                long[] a = new long[n];
                var t = sumTree(lo, lo + n);
                for (int step = 0; step < 80; step++) {
                    int l = step % 5 == 0 ? 0 : rng.nextInt(n + 1),
                            r = step % 5 == 0 ? n : l + rng.nextInt(n - l + 1);
                    var f = new Affine(rng.nextInt(2), rng.nextInt(10));
                    t.apply(lo + l, lo + r, f);
                    for (int i = l; i < r; i++) a[i] = f.apply(a[i]);
                    if (step % 3 == 0) {
                        int p = rng.nextInt(n);
                        long v = rng.nextInt(10);
                        t.set(lo + p, new Sum(v, 1));
                        a[p] = v;
                    }
                    // 照会せずに更新を重ねる回も作り、保留中の作用の伝播を検証する。
                    if (step % 2 == 1) continue;
                    for (int x = 0; x <= n; x++)
                        for (int y = x; y <= n; y++) {
                            long s = 0;
                            for (int i = x; i < y; i++) s += a[i];
                            assertEquals(new Sum(s, y - x), t.getRange(lo + x, lo + y),
                                    "n=" + n + ", lo=" + lo + ", step=" + step + ", [" + x + "," + y + ")");
                        }
                    for (int i = 0; i < n; i++) assertEquals(new Sum(a[i], 1), t.get(lo + i));
                }
            }
        }
    }

    @Test
    void 広い添字範囲を基本区間に分けて比較() {
        // 端点を少数の座標に限定し、隣接座標間の基本区間ごとに一様な値として愚直に管理する。
        // 和は long の桁あふれを含めて 2^64 を法として一致する。
        var rng = new Random(77);
        long n = 1_000_000_000_000_000_000L;
        var points = new TreeSet<Long>(List.of(0L, n));
        while (points.size() < 12) points.add(Math.floorMod(rng.nextLong(), n));
        Long[] b = points.toArray(Long[]::new);
        long[] v = new long[b.length - 1];
        var t = sumTree(0, n);
        for (int step = 0; step < 300; step++) {
            int x = rng.nextInt(b.length), y = rng.nextInt(b.length);
            int l = Math.min(x, y), r = Math.max(x, y);
            var f = new Affine(rng.nextInt(3), rng.nextInt(1000));
            t.apply(b[l], b[r], f);
            for (int i = l; i < r; i++) v[i] = f.apply(v[i]);
            x = rng.nextInt(b.length);
            y = rng.nextInt(b.length);
            l = Math.min(x, y);
            r = Math.max(x, y);
            long s = 0;
            for (int i = l; i < r; i++) s += v[i] * (b[i + 1] - b[i]);
            assertEquals(new Sum(s, b[r] - b[l]), t.getRange(b[l], b[r]));
            int k = rng.nextInt(v.length);
            long p = b[k] + Math.floorMod(rng.nextLong(), b[k + 1] - b[k]);
            assertEquals(new Sum(v[k], 1), t.get(p));
        }
    }

    @Test
    void 単位元のみの構築で区間加算区間最大() {
        var rng = new Random(5);
        int n = 20;
        long neg = Long.MIN_VALUE / 2;
        long[] a = new long[n];
        var t = new DynamicLazySegmentationTree.DynamicLazySegmentTree<Long, Long>(
                n, Math::max, Long::sum, Long::sum, () -> neg);
        // 未操作の要素は単位元なので、全体に -neg を加えて全要素を0にしておく
        t.apply(0, n, -neg);
        for (int step = 0; step < 100; step++) {
            int l = rng.nextInt(n + 1), r = l + rng.nextInt(n - l + 1);
            long f = rng.nextInt(21) - 10;
            t.apply(l, r, f);
            for (int i = l; i < r; i++) a[i] += f;
            int x = rng.nextInt(n), y = x + 1 + rng.nextInt(n - x);
            long m = Long.MIN_VALUE;
            for (int i = x; i < y; i++) m = Math.max(m, a[i]);
            assertEquals(m, t.getRange(x, y));
        }
        assertEquals(neg, t.getRange(3, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> t.getRange(0, n + 1));
        assertThrows(IndexOutOfBoundsException.class, () -> t.set(-1, 0L));
        assertThrows(IllegalArgumentException.class, () -> sumTree(3, 3));
    }
}
