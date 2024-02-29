package jp.hirohiso.competive.util.tree;

import java.util.Arrays;
import java.util.function.*;

public class FenwickTreeMain {
    public static void main(String[] args) {
        var ft = new FenwickTree<Integer>(10, (i, j) -> i + j, i -> -i, () -> 0);
        ft.update(2, 10);
        ft.update(5, 5);
        System.out.println(ft.sum(3));
        System.out.println(ft.sum(6));
        ft.update(3, -6);
        System.out.println(ft.sum(6));
        System.out.println(ft.range(4, 6));
    }

    private static class IntFenwickTree {
        private final int[] arr;
        private final int size;

        IntFenwickTree(int N) {
            this.arr = new int[N + 1];
            this.size = N;
        }

        /**
         *
         * @param x 1 <= x <= N
         * @param v
         */
        void update(int x, int v) {
            int i = x;
            while (i <= size) {
                arr[i] += v;
                i += i & -i;
            }
        }

        /**
         *
         * @param i 1 <= i <= N
         * @return
         */
        int sum(int i) {
            var s = 0;
            while (i > 0) {
                s += arr[i];
                i -= i & -i;
            }
            return s;
        }

        int range(int start, int end) {
            return sum(end - 1) - sum(start);
        }

    }

    private static class FenwickTree<T> {
        Object[] arr;

        private final BinaryOperator<T> add;
        private final UnaryOperator<T> inv;
        private final Supplier<T> e;

        FenwickTree(T[] a, BinaryOperator<T> add, UnaryOperator<T> inv, Supplier<T> e) {
            this.arr = new Object[a.length + 1];
            for (int i = 0; i < a.length; i++) {
                arr[i + 1] = a[i];
            }
            this.add = add;
            this.inv = inv;
            this.e = e;
        }

        FenwickTree(int N, BinaryOperator<T> add, UnaryOperator<T> minus, Supplier<T> e) {
            this.arr = new Object[N + 1];
            for (int i = 0; i < this.arr.length; i++) {
                this.arr[i] = e.get();
            }
            this.add = add;
            this.inv = minus;
            this.e = e;
        }

        /**
         *
         * @param x 1 <= x <= N
         * @param v
         */
        void update(int x, T v) {
            for (int i = x; i <= arr.length; i += i & -i) {
                arr[i] = add.apply((T) arr[i], v);
            }
        }

        /**
         *
         * @param i 1 <= i <= N
         */
        T sum(int i) {
            var s = e.get();
            while (i > 0) {
                s = add.apply((T) arr[i], s);
                i -= i & -i;
            }
            return s;
        }

        T range(int start, int end) {
            var r = sum(end - 1);
            var l = sum(start);
            return add.apply(r, inv.apply(l));
        }
    }
}
