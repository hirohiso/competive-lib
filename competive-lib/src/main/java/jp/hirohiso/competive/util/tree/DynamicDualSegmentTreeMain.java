package jp.hirohiso.competive.util.tree;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.LongFunction;
import java.util.function.Supplier;

public class DynamicDualSegmentTreeMain {
    public static void main(String[] args) {
        // 区間 chmax・一点取得（添字 10^18 まで）
        var st = new DynamicDualSegmentTree<Long, Long>(
                1_000_000_000_000_000_000L,
                Math::max, // 作用
                Math::max, // 作用の合成
                () -> 0L // 未操作の値
        );
        st.apply(0, 1_000_000_000_000_000_000L, 3L);
        st.apply(10, 20, 5L);
        System.out.println(st.get(15)); // 5
        System.out.println(st.get(999_999_999_999_999_999L)); // 3

        // 初期値を添字から決める（a_i = i に区間加算）
        var add = new DynamicDualSegmentTree<Long, Long>(
                -1_000_000_000L, 1_000_000_000L, Long::sum, Long::sum, i -> i);
        add.apply(-5, 5, 100L);
        System.out.println(add.get(-3)); // 97
    }

    /**
     * 必要な節点だけを生成する、区間作用・一点取得の動的双対セグメント木。
     * <p>添字の範囲は半開区間 {@code [lo,hi)} で、long の広い範囲や負の添字を扱える
     * （ただし {@code hi - lo} が long に収まること）。
     * 関数の約束は {@link DualSegmentTreeMain.DualSegmentTree} と同じで、
     * composition(new,old) は「old の後に new」。非可換な作用も扱える。
     * 作用の単位元は不要（作用がない状態は内部で区別する）。
     * init(i) は添字 i の初期値。各操作 O(log(hi-lo))、領域は操作回数 Q に対して
     * O(Q log(hi-lo))。渡す関数は引数を破壊しないこと。</p>
     *
     * @param <T> 値の型
     * @param <U> 作用の型
     */
    public static class DynamicDualSegmentTree<T, U> {
        /** 区間全体に未伝播の作用（null なら無し）を持つ節点。 */
        private static final class Node<U> {
            private U lazy;
            private Node<U> left, right;
        }

        private final long lo, hi;
        private final BiFunction<U, T, T> mapping;
        private final BinaryOperator<U> composition;
        private final LongFunction<T> init;
        private Node<U> root;

        /**
         * 添字範囲 {@code [0,n)}、初期値が全て e の動的双対セグメント木を構築する。
         *
         * @param n           要素数（1以上）
         * @param mapping     作用 mapping(f,x)
         * @param composition 作用の合成 composition(new,old)
         * @param e           全要素の初期値
         */
        public DynamicDualSegmentTree(long n, BiFunction<U, T, T> mapping, BinaryOperator<U> composition,
                                      Supplier<T> e) {
            this(0, n, mapping, composition, i -> e.get());
        }

        /**
         * 添字範囲 {@code [lo,hi)} の動的双対セグメント木を構築する。
         *
         * @param lo          添字の下限（含む）
         * @param hi          添字の上限（含まない）。{@code lo < hi}
         * @param mapping     作用 mapping(f,x)
         * @param composition 作用の合成 composition(new,old)
         * @param init        添字から初期値を返す関数
         */
        public DynamicDualSegmentTree(long lo, long hi, BiFunction<U, T, T> mapping,
                                      BinaryOperator<U> composition, LongFunction<T> init) {
            if (lo >= hi) {
                throw new IllegalArgumentException("lo < hi が必要: [" + lo + "," + hi + ")");
            }
            this.lo = lo;
            this.hi = hi;
            this.mapping = mapping;
            this.composition = composition;
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
            if (start < lo || end > hi || start > end) {
                throw new IndexOutOfBoundsException("[" + start + "," + end + "), Range: [" + lo + "," + hi + ")");
            }
            root = apply(root, lo, hi, start, end, f);
        }

        private Node<U> apply(Node<U> node, long l, long r, long start, long end, U f) {
            if (r <= start || end <= l) {
                return node;
            }
            if (node == null) {
                node = new Node<>();
            }
            if (start <= l && r <= end) {
                node.lazy = mergeLazy(f, node.lazy);
                return node;
            }
            // 子に進む前に古い作用を伝播し、更新の時系列を保つ。
            push(node);
            long m = mid(l, r);
            node.left = apply(node.left, l, m, start, end, f);
            node.right = apply(node.right, m, r, start, end, f);
            return node;
        }

        /**
         * 一点の値を取得する。
         *
         * @param index 添字
         * @return 初期値に作用を順に適用した値
         */
        public T get(long index) {
            if (index < lo || hi <= index) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Range: [" + lo + "," + hi + ")");
            }
            return get(root, lo, hi, index);
        }

        // 深い節点ほど古い作用を持つので、葉側から根側の順に適用する。
        private T get(Node<U> node, long l, long r, long index) {
            if (node == null) {
                return init.apply(index);
            }
            T value;
            if (r - l == 1) {
                value = init.apply(index);
            } else {
                long m = mid(l, r);
                value = index < m ? get(node.left, l, m, index) : get(node.right, m, r, index);
            }
            return node.lazy == null ? value : mapping.apply(node.lazy, value);
        }

        private void push(Node<U> node) {
            if (node.lazy == null) {
                return;
            }
            if (node.left == null) {
                node.left = new Node<>();
            }
            if (node.right == null) {
                node.right = new Node<>();
            }
            node.left.lazy = mergeLazy(node.lazy, node.left.lazy);
            node.right.lazy = mergeLazy(node.lazy, node.right.lazy);
            node.lazy = null;
        }

        private U mergeLazy(U newer, U older) {
            return older == null ? newer : composition.apply(newer, older);
        }

        // 負の添字でも切り捨て方向が揃うように差分で中点を求める。
        private static long mid(long l, long r) {
            return l + ((r - l) >> 1);
        }
    }
}
