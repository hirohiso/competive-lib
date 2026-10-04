package jp.hirohiso.competive.util.tree;

import java.util.function.*;

public class FenwickTreeMain {
    public static void main(String[] args) {
        var ft = new GenericFenwickTree<Integer>(6, (i, j) -> i + j, i -> -i, () -> 0);
        //ft.update(0, 1);
        for (int i = 0; i < 3; i++) {
            System.out.println(i);
            ft.add(i, i);
        }
        System.out.println(ft.sum(-1));
        System.out.println(ft.sum(0));
        System.out.println(ft.sum(1));
        System.out.println(ft.sum(2));
        System.out.println(ft.sum(5));
        ft.add(3, -6);
        System.out.println(ft.sum(5));
        System.out.println(ft.range(4, 5));
        System.out.println(ft.range(0, 5));

        System.out.println("=======");
        var bit = new FenwickTree(5);
        bit.add(0, 1);
        bit.add(2, 1);
        bit.add(4, 1);
        bit.add(5, 1);

        for (int i = 0; i < 5; i++) {
            System.out.println(bit.sum(i));
            System.out.println(bit.range(i, i + 1));
        }
    }

    /**
     * long の一点加算と区間和を扱う Fenwick Tree。
     * <p>add の添字は {@code 0 <= x < N}。sum(i) は両端を含む [0,i] の和で
     * sum(-1)=0。range は半開区間 [start,end) で空区間も可。
     * 初期化 O(N)、操作 O(log N)、領域 O(N)。全ての中間和が long に収まること。</p>
     */
    static class FenwickTree {
        private final long[] arr;
        private final int size;

        FenwickTree(int N) {
            this.arr = new long[N + 1];
            this.size = N;
        }

        /**
         * @param x 更新する添字 {@code 0 <= x < N}
         * @param v 加算する値
         */
        void add(int x, long v) {
            int i = x + 1;
            while (i <= size) {
                arr[i] += v;
                i += i & -i;
            }
        }

        /**
         * @param i 含める右端 {@code -1 <= i < N}。-1なら単位元
         * @return 指定範囲の和
         */
        long sum(int i) {
            i++;
            var s = 0L;
            while (i > 0) {
                s += arr[i];
                i -= i & -i;
            }
            return s;
        }

        /**
         * @param start 含む左端 {@code 0 <= start <= end}
         * @param end 含まない右端 {@code start <= end <= N}
         * @return 指定範囲の和
         */
        long range(int start, int end) {
            return sum(end - 1) - sum(start - 1);
        }
    }

    /**
     * アーベル群上の Fenwick Tree。add/inv/e は群演算・逆元・単位元。
     * <p>sum(i) は [0,i]、range(l,r) は [l,r)。0始まりで sum(-1)=e。
     * 配列構築 O(N log N)、サイズ指定の構築 O(N)、操作 O(log N)、領域 O(N)。
     * 演算は引数を変更せず、新しい値を返すこと。</p>
     */
    static class GenericFenwickTree<T> {
        Object[] arr;

        private final BinaryOperator<T> add;
        private final UnaryOperator<T> inv;
        private final Supplier<T> e;

        GenericFenwickTree(T[] a, BinaryOperator<T> add, UnaryOperator<T> inv, Supplier<T> e) {
            this(a.length, add, inv, e);
            for (int i = 0; i < a.length; i++) {
                add(i, a[i]);
            }
        }

        GenericFenwickTree(int N, BinaryOperator<T> add, UnaryOperator<T> minus, Supplier<T> e) {
            this.arr = new Object[N + 1];
            for (int i = 0; i < this.arr.length; i++) {
                this.arr[i] = e.get();
            }
            this.add = add;
            this.inv = minus;
            this.e = e;
        }

        /**
         * @param x 更新する添字 {@code 0 <= x < N}
         * @param v 加算する値
         */
        void add(int x, T v) {
            for (int i = x + 1; i <= arr.length - 1; i += i & -i) {
                arr[i] = add.apply((T) arr[i], v);
            }
        }

        /**
         * @param i 含める右端 {@code -1 <= i < N}。-1なら単位元
         */
        T sum(int i) {
            var s = e.get();
            i++;
            while (i > 0) {
                s = add.apply((T) arr[i], s);
                i -= i & -i;
            }
            return s;
        }

        /**
         * @param start 含む左端 {@code 0 <= start <= end}
         * @param end 含まない右端 {@code start <= end <= N}
         * @return 指定範囲の和
         */
        T range(int start, int end) {
            var r = sum(end - 1);
            var l = sum(start - 1);
            return add.apply(r, inv.apply(l));
        }
    }
}
