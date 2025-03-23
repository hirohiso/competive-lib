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
        System.out.println(bit.sum(0));
        System.out.println(bit.sum(3));
        System.out.println(bit.sum(4));
        System.out.println(bit.sum(4));
    }

    private static class FenwickTree {
        private final long[] arr;
        private final int size;

        FenwickTree(int N) {
            this.arr = new long[N + 1];
            this.size = N;
        }

        /**
         * @param x 0 <= x < N
         * @param v
         */
        void add(int x, long v) {
            int i = x + 1;
            while (i <= size) {
                arr[i] += v;
                i += i & -i;
            }
        }

        /**
         * @param i 0 <= i < N
         * @return
         */
        long sum(int i) {
            i++;
            var s = 0;
            while (i > 0) {
                s += arr[i];
                i -= i & -i;
            }
            return s;
        }

        long range(int start, int end) {
            return sum(end) - sum(start - 1);
        }
    }

    private static class GenericFenwickTree<T> {
        Object[] arr;

        private final BinaryOperator<T> add;
        private final UnaryOperator<T> inv;
        private final Supplier<T> e;

        GenericFenwickTree(T[] a, BinaryOperator<T> add, UnaryOperator<T> inv, Supplier<T> e) {
            this.arr = new Object[a.length + 1];
            for (int i = 0; i < a.length; i++) {
                arr[i + 1] = a[i];
            }
            this.add = add;
            this.inv = inv;
            this.e = e;
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
         * @param x 0 <= x < N
         * @param v
         */
        void add(int x, T v) {
            for (int i = x + 1; i <= arr.length -1; i += i & -i) {
                arr[i] = add.apply((T) arr[i], v);
            }
        }

        /**
         * @param i 0 <= i < N
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

        T range(int start, int end) {
            var r = sum(end);
            var l = sum(start - 1);
            return add.apply(r, inv.apply(l));
        }
    }
}
