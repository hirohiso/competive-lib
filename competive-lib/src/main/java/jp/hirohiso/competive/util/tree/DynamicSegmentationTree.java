package jp.hirohiso.competive.util.tree;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DynamicSegmentationTree {

    public static void main(String[] args) {
        // 添字が 10^18 まである区間和
        var st = new DynamicSegmentTree<Long>(1_000_000_000_000_000_000L, Long::sum, () -> 0L);
        st.update(3, 5L);
        st.update(999_999_999_999_999_999L, 7L);
        st.merge(3, 10L);
        System.out.println(st.getRange(0, 1_000_000_000_000_000_000L)); // 22
        System.out.println(st.getRange(4, 1_000_000_000_000_000_000L)); // 7
        System.out.println(st.maxRight(0, v -> v <= 15)); // 999999999999999999

        // 負の添字も扱える
        var neg = new DynamicSegmentTree<Long>(-1_000_000_000L, 1_000_000_000L, Math::max, () -> Long.MIN_VALUE);
        neg.update(-5, 3L);
        neg.update(8, 1L);
        System.out.println(neg.getRange(-10, 0)); // 3
    }

    /**
     * 必要な節点だけを生成する、点更新・区間集約の動的セグメント木。
     * <p>添字の範囲は半開区間 {@code [lo,hi)} で、long の広い範囲や負の添字を扱える
     * （ただし {@code hi - lo} が long に収まること）。座標圧縮なしでオンラインに使える。
     * 未更新の要素は単位元 e とみなす。演算は結合的で、e は単位元であること。
     * 非可換な演算も左から右の順に集約する。
     * 各操作 O(log(hi-lo))、領域は更新回数 Q に対して O(Q log(hi-lo))。
     * 渡す関数は引数を破壊しないこと。</p>
     *
     * @param <T> 要素の型
     */
    public static class DynamicSegmentTree<T> {
        /** 区間の集約値と子への参照を持つ節点。null の子は全要素が単位元の区間を表す。 */
        private static final class Node<T> {
            private T value;
            private Node<T> left, right;

            private Node(T value) {
                this.value = value;
            }
        }

        /** maxRight/minLeft の再帰で確定済み部分の集約値を持ち回るための入れ物。 */
        private static final class Acc<T> {
            private T value;

            private Acc(T value) {
                this.value = value;
            }
        }

        private final long lo, hi;
        private final BinaryOperator<T> op;
        private final Supplier<T> e;
        private Node<T> root;

        /**
         * 添字範囲 {@code [0,n)} の動的セグメント木を構築する。
         *
         * @param n  要素数（1以上）
         * @param op 結合的な二項演算
         * @param e  単位元
         */
        public DynamicSegmentTree(long n, BinaryOperator<T> op, Supplier<T> e) {
            this(0, n, op, e);
        }

        /**
         * 添字範囲 {@code [lo,hi)} の動的セグメント木を構築する。
         *
         * @param lo 添字の下限（含む）
         * @param hi 添字の上限（含まない）。{@code lo < hi}
         * @param op 結合的な二項演算
         * @param e  単位元
         */
        public DynamicSegmentTree(long lo, long hi, BinaryOperator<T> op, Supplier<T> e) {
            if (lo >= hi) {
                throw new IllegalArgumentException("lo < hi が必要: [" + lo + "," + hi + ")");
            }
            this.lo = lo;
            this.hi = hi;
            this.op = op;
            this.e = e;
        }

        /**
         * 一点の値を取得する。
         *
         * @param index 添字
         * @return 値（未更新なら単位元）
         */
        public T get(long index) {
            checkIndex(index);
            Node<T> node = root;
            long l = lo, r = hi;
            while (node != null && r - l > 1) {
                long m = mid(l, r);
                if (index < m) {
                    node = node.left;
                    r = m;
                } else {
                    node = node.right;
                    l = m;
                }
            }
            return valueOf(node);
        }

        /**
         * 一点の値を val に置き換える。
         *
         * @param index 添字
         * @param val   新しい値
         */
        public void update(long index, T val) {
            checkIndex(index);
            root = update(root, lo, hi, index, val);
        }

        private Node<T> update(Node<T> node, long l, long r, long index, T val) {
            if (node == null) {
                node = new Node<>(e.get());
            }
            if (r - l == 1) {
                node.value = val;
                return node;
            }
            long m = mid(l, r);
            if (index < m) {
                node.left = update(node.left, l, m, index, val);
            } else {
                node.right = update(node.right, m, r, index, val);
            }
            node.value = op.apply(valueOf(node.left), valueOf(node.right));
            return node;
        }

        /**
         * 一点の値を {@code op(現在値, val)} に置き換える。
         *
         * @param index 添字
         * @param val   演算する値
         */
        public void merge(long index, T val) {
            update(index, op.apply(get(index), val));
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
            return getRange(root, lo, hi, start, end);
        }

        private T getRange(Node<T> node, long l, long r, long start, long end) {
            if (node == null || r <= start || end <= l) {
                return e.get();
            }
            if (start <= l && r <= end) {
                return node.value;
            }
            long m = mid(l, r);
            return op.apply(getRange(node.left, l, m, start, end), getRange(node.right, m, r, start, end));
        }

        /**
         * {@code f(getRange(start, r))} が真となる最大の r を返す。
         * f は単調で、単位元に対して真であること。
         *
         * @param start 左端（{@code lo <= start <= hi}）
         * @param f     述語
         * @return 条件を満たす最大の右端
         */
        public long maxRight(long start, Predicate<T> f) {
            checkRange(start, start);
            return maxRight(root, lo, hi, start, f, new Acc<>(e.get()));
        }

        // 区間 [max(l,start), r) を全て取り込めたら r、そうでなければ境界を返す。
        private long maxRight(Node<T> node, long l, long r, long start, Predicate<T> f, Acc<T> acc) {
            if (r <= start) {
                return r;
            }
            if (start <= l) {
                T next = op.apply(acc.value, valueOf(node));
                if (f.test(next)) {
                    acc.value = next;
                    return r;
                }
                if (r - l == 1) {
                    return l;
                }
            }
            long m = mid(l, r);
            long res = maxRight(childLeft(node), l, m, start, f, acc);
            if (res < m) {
                return res;
            }
            return maxRight(childRight(node), m, r, start, f, acc);
        }

        /**
         * {@code f(getRange(l, end))} が真となる最小の l を返す。
         * f は単調で、単位元に対して真であること。
         *
         * @param end 右端（{@code lo <= end <= hi}）
         * @param f   述語
         * @return 条件を満たす最小の左端
         */
        public long minLeft(long end, Predicate<T> f) {
            checkRange(end, end);
            return minLeft(root, lo, hi, end, f, new Acc<>(e.get()));
        }

        // 区間 [l, min(r,end)) を全て取り込めたら l、そうでなければ境界を返す。
        private long minLeft(Node<T> node, long l, long r, long end, Predicate<T> f, Acc<T> acc) {
            if (end <= l) {
                return l;
            }
            if (r <= end) {
                T next = op.apply(valueOf(node), acc.value);
                if (f.test(next)) {
                    acc.value = next;
                    return l;
                }
                if (r - l == 1) {
                    return r;
                }
            }
            long m = mid(l, r);
            long res = minLeft(childRight(node), m, r, end, f, acc);
            if (res > m) {
                return res;
            }
            return minLeft(childLeft(node), l, m, end, f, acc);
        }

        private T valueOf(Node<T> node) {
            return node == null ? e.get() : node.value;
        }

        private static <T> Node<T> childLeft(Node<T> node) {
            return node == null ? null : node.left;
        }

        private static <T> Node<T> childRight(Node<T> node) {
            return node == null ? null : node.right;
        }

        // 負の添字でも切り捨て方向が揃うように差分で中点を求める。
        private static long mid(long l, long r) {
            return l + ((r - l) >> 1);
        }

        private void checkIndex(long index) {
            if (index < lo || hi <= index) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Range: [" + lo + "," + hi + ")");
            }
        }

        private void checkRange(long start, long end) {
            if (start < lo || end > hi || start > end) {
                throw new IndexOutOfBoundsException("[" + start + "," + end + "), Range: [" + lo + "," + hi + ")");
            }
        }
    }
}
