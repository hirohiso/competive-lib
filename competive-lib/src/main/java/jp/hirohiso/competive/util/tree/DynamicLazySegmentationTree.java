package jp.hirohiso.competive.util.tree;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class DynamicLazySegmentationTree {

    public static void main(String[] args) {
        // 区間加算・区間和（添字 10^9 まで）
        record Sum(long sum, long size) {
        }
        var st = new DynamicLazySegmentTree<Sum, Long>(
                0, 1_000_000_000L,
                (a, b) -> new Sum(a.sum + b.sum, a.size + b.size),
                (f, x) -> new Sum(x.sum + f * x.size, x.size),
                Long::sum,
                () -> new Sum(0, 0),
                (l, r) -> new Sum(0, r - l) // 未作成区間の初期値には区間長が必要
        );
        st.apply(0, 1_000_000_000L, 1L);
        st.apply(10, 20, 5L);
        System.out.println(st.getRange(0, 1_000_000_000L).sum); // 1000000050
        System.out.println(st.getRange(15, 25).sum); // 35

        // 区間加算・区間最大（初期値が区間長に依存しない場合）
        var max = new DynamicLazySegmentTree<Long, Long>(
                1_000_000_000_000L, Math::max, Long::sum, Long::sum, () -> 0L);
        max.apply(100, 200, 3L);
        max.apply(150, 1_000_000L, 4L);
        System.out.println(max.getRange(0, 160)); // 7
    }

    /**
     * 必要な節点だけを生成する、区間作用・区間集約の動的遅延セグメント木。
     * <p>添字の範囲は半開区間 {@code [lo,hi)} で、long の広い範囲や負の添字を扱える
     * （ただし {@code hi - lo} が long に収まること）。
     * 各操作 O(log(hi-lo))、領域は操作回数 Q に対して O(Q log(hi-lo))。</p>
     * <p>関数の約束は {@link LazySegmentationTree.LazySegmentTree} と同じで、
     * composition(old,new) は「old の後に new」。作用の単位元は不要
     * （作用がない状態は内部で区別する）。init(l,r) は未操作の区間 {@code [l,r)} の集約値で、
     * {@code op(init(l,m), init(m,r)) = init(l,r)} を満たすこと。
     * 区間和のように区間長が必要なときは init で長さを与える。値と作用は不変として扱う。</p>
     *
     * @param <T> 値の型
     * @param <U> 作用の型
     */
    public static class DynamicLazySegmentTree<T, U> {
        /** 未操作の区間 {@code [l,r)} の集約値を返す関数。 */
        @FunctionalInterface
        public interface RangeInit<T> {
            /**
             * 未操作の区間の集約値を返す。
             *
             * @param l 区間の開始（含む）
             * @param r 区間の終了（含まない）
             * @return 集約値
             */
            T get(long l, long r);
        }

        /** 自身の作用を反映済みの集約値と、子へ未伝播の作用（null なら無し）を持つ節点。 */
        private static final class Node<T, U> {
            private T value;
            private U lazy;
            private Node<T, U> left, right;

            private Node(T value) {
                this.value = value;
            }
        }

        private final long lo, hi;
        private final BinaryOperator<T> op;
        private final BiFunction<U, T, T> mapping;
        private final BinaryOperator<U> composition;
        private final Supplier<T> e;
        private final RangeInit<T> init;
        private Node<T, U> root;

        /**
         * 添字範囲 {@code [0,n)}、未操作の要素が全て単位元の動的遅延セグメント木を構築する。
         * 区間長に依存しない集約（最大・最小など）向け。
         *
         * @param n           要素数（1以上）
         * @param op          集約演算
         * @param mapping     作用 mapping(f,x)
         * @param composition 作用の合成 composition(old,new)
         * @param e           集約の単位元
         */
        public DynamicLazySegmentTree(long n, BinaryOperator<T> op, BiFunction<U, T, T> mapping,
                                      BinaryOperator<U> composition, Supplier<T> e) {
            this(0, n, op, mapping, composition, e, (l, r) -> e.get());
        }

        /**
         * 添字範囲 {@code [lo,hi)} の動的遅延セグメント木を構築する。
         *
         * @param lo          添字の下限（含む）
         * @param hi          添字の上限（含まない）。{@code lo < hi}
         * @param op          集約演算
         * @param mapping     作用 mapping(f,x)
         * @param composition 作用の合成 composition(old,new)
         * @param e           集約の単位元（空区間の結果）
         * @param init        未操作の区間の集約値
         */
        public DynamicLazySegmentTree(long lo, long hi, BinaryOperator<T> op, BiFunction<U, T, T> mapping,
                                      BinaryOperator<U> composition, Supplier<T> e, RangeInit<T> init) {
            if (lo >= hi) {
                throw new IllegalArgumentException("lo < hi が必要: [" + lo + "," + hi + ")");
            }
            this.lo = lo;
            this.hi = hi;
            this.op = op;
            this.mapping = mapping;
            this.composition = composition;
            this.e = e;
            this.init = init;
        }

        /**
         * 半開区間 {@code [start,end)} に作用 f を適用する。空区間なら何もしない。
         *
         * @param start 開始（含む）
         * @param end   終了（含まない）
         * @param f     作用
         */
        public void apply(long start, long end, U f) {
            checkRange(start, end);
            root = apply(root, lo, hi, start, end, f);
        }

        private Node<T, U> apply(Node<T, U> node, long l, long r, long start, long end, U f) {
            if (r <= start || end <= l) {
                return node;
            }
            if (node == null) {
                node = new Node<>(init.get(l, r));
            }
            if (start <= l && r <= end) {
                applyAll(node, f);
                return node;
            }
            long m = mid(l, r);
            push(node, l, m, r);
            node.left = apply(node.left, l, m, start, end, f);
            node.right = apply(node.right, m, r, start, end, f);
            pull(node, l, m, r);
            return node;
        }

        /**
         * 一点の値を val に置き換える。
         *
         * @param index 添字
         * @param val   新しい値
         */
        public void set(long index, T val) {
            checkRange(index, index + 1);
            root = set(root, lo, hi, index, val);
        }

        private Node<T, U> set(Node<T, U> node, long l, long r, long index, T val) {
            if (node == null) {
                node = new Node<>(init.get(l, r));
            }
            if (r - l == 1) {
                node.value = val;
                return node;
            }
            long m = mid(l, r);
            push(node, l, m, r);
            if (index < m) {
                node.left = set(node.left, l, m, index, val);
            } else {
                node.right = set(node.right, m, r, index, val);
            }
            pull(node, l, m, r);
            return node;
        }

        /**
         * 一点の値を取得する。
         *
         * @param index 添字
         * @return 値
         */
        public T get(long index) {
            return getRange(index, index + 1);
        }

        /**
         * 半開区間 {@code [start,end)} の集約値を返す。空区間なら単位元。
         *
         * @param start 開始（含む）
         * @param end   終了（含まない）
         * @return 集約値
         */
        public T getRange(long start, long end) {
            checkRange(start, end);
            if (start == end) {
                return e.get();
            }
            return getRange(root, lo, hi, start, end);
        }

        // [start,end) と [l,r) が交わることを呼出し側で保証する。
        private T getRange(Node<T, U> node, long l, long r, long start, long end) {
            if (node == null) {
                return init.get(Math.max(l, start), Math.min(r, end));
            }
            if (start <= l && r <= end) {
                return node.value;
            }
            long m = mid(l, r);
            push(node, l, m, r);
            if (end <= m) {
                return getRange(node.left, l, m, start, end);
            }
            if (m <= start) {
                return getRange(node.right, m, r, start, end);
            }
            return op.apply(getRange(node.left, l, m, start, end), getRange(node.right, m, r, start, end));
        }

        private void applyAll(Node<T, U> node, U f) {
            node.value = mapping.apply(f, node.value);
            node.lazy = node.lazy == null ? f : composition.apply(node.lazy, f);
        }

        // 子が無ければ初期値で作ってから、保留中の作用を子へ渡す。
        private void push(Node<T, U> node, long l, long m, long r) {
            if (node.lazy == null) {
                return;
            }
            if (node.left == null) {
                node.left = new Node<>(init.get(l, m));
            }
            if (node.right == null) {
                node.right = new Node<>(init.get(m, r));
            }
            applyAll(node.left, node.lazy);
            applyAll(node.right, node.lazy);
            node.lazy = null;
        }

        private void pull(Node<T, U> node, long l, long m, long r) {
            T a = node.left == null ? init.get(l, m) : node.left.value;
            T b = node.right == null ? init.get(m, r) : node.right.value;
            node.value = op.apply(a, b);
        }

        // 負の添字でも切り捨て方向が揃うように差分で中点を求める。
        private static long mid(long l, long r) {
            return l + ((r - l) >> 1);
        }

        private void checkRange(long start, long end) {
            if (start < lo || end > hi || start > end) {
                throw new IndexOutOfBoundsException("[" + start + "," + end + "), Range: [" + lo + "," + hi + ")");
            }
        }
    }
}
