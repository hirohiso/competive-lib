package jp.hirohiso.competive.util.tree;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class LazySegmentationTree {

    public static void main(String[] args) {

        //区間中の最大値を計算
        //区間計算は値の足し算
        //区間加算操作・区間最大値取得
        LazySegmentTree<Integer, Integer> st = new LazySegmentTree<>(
                new Integer[]{1, 1, 2, 3, 5, 8, 13},
                (i, j) -> Math.max(i, j),
                (i, j) -> i + j,
                (i, j) -> i + j,
                () -> 0,
                () -> 0);
        st.debug();

        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));
        st.apply(0, 1, 5);
        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));
        st.apply(1, 3, 10);
        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));

        System.out.println("---------------------");

        //区間加算操作・区間和取得
        var sum = new LazySegmentTree<Pair, Integer>(
                new Pair[]{
                        new Pair(1, 1),
                        new Pair(3, 1),
                        new Pair(2, 1),
                        new Pair(4, 1),
                        new Pair(3, 1),
                        new Pair(5, 1),
                },
                (p, q) -> new Pair(p.v + q.v, p.n + q.n),
                (i, p) -> new Pair(p.v + p.n * i, p.n),
                (i, j) -> i + j,
                () -> new Pair(0, 1),
                () -> 0
        );
        System.out.println("区間[0,0):" + sum.getRange(0, 1).v);
        System.out.println("区間[0,4):" + sum.getRange(0, 4).v);
        System.out.println("区間[2,6):" + sum.getRange(2, 6).v);
        System.out.println("区間[6,8):" + sum.getRange(6, 8).v);
        sum.apply(0, 8, 10);
        sum.apply(0, 4, 100);
        System.out.println("区間[0,0):" + sum.getRange(0, 1).v);
        System.out.println("区間[0,4):" + sum.getRange(0, 4).v);
        System.out.println("区間[2,6):" + sum.getRange(2, 6).v);
        System.out.println("区間[6,8):" + sum.getRange(6, 8).v);
    }

    record Pair(int v, int n) {
    }

    ;

    //遅延評価セグメンテーション木
    //T : Data
    //U : Lazy
    public static class LazySegmentTree<T, U> {
        //配列
        private Object[] array, lazy;
        //最下段のサイズ
        private int rowSize;
        //集約関数
        private BinaryOperator<T> operator;
        //遅延評価関数<U,Tを受取>
        //Tに対して遅延値Uを作用させて値を更新する
        private BiFunction<U, T, T> mapping;
        //遅延値Uの加算
        private BinaryOperator<U> composition;

        //初期化関数T
        private Supplier<T> e;
        //初期化関数U
        private Supplier<U> id;

        public LazySegmentTree(T[] input, BinaryOperator<T> ope, BiFunction<U, T, T> mapping, BinaryOperator<U> composition,
                               Supplier<T> e, Supplier<U> id) {
            int size = input.length;
            int n = 1;
            while (n < size) {
                n *= 2;
            }
            rowSize = n;
            //nは最下段に必要な要素数。
            //配列全体は2*n-1必要
            this.array = new Object[2 * n - 1];
            this.lazy = new Object[2 * n - 1];
            this.operator = ope;
            this.mapping = mapping;
            this.composition = composition;
            this.e = e;
            this.id = id;
            for (int i = 0; i < size; i++) {
                setType(i + n - 1, input[i]);
            }
            for (int i = n - 2; i >= 0; i--) {
                T a = getType(2 * i + 1);
                T b = getType(2 * i + 2);
                setType(i, this.operator.apply(a, b));
            }
        }

        /**
         * startからendまでの区間にfを作用させる
         *
         * @param start
         * @param end
         * @param f
         */
        public void apply(int start, int end, U f) {
            apply(start, end, 0, 0, rowSize, f);
        }

        private void apply(int start, int end, int k, int l, int r, U f) {
            //k番目の要素を評価
            eval(k, l, r);
            if (r <= start || end <= l) {
                return;
            }
            //被覆しているならlazyに格納したあと評価
            if (start <= l && r <= end) {
                mergeLazy(k, f);
                eval(k, l, r);
            } else {
                //異なるなら子の要素を計算して,値を更新
                apply(start, end, k * 2 + 1, l, (l + r) / 2, f);
                apply(start, end, k * 2 + 2, (l + r) / 2, r, f);
                T a = getType(2 * k + 1);
                T b = getType(2 * k + 2);
                setType(k, this.operator.apply(a, b));
            }
        }

        public T getRange(int start, int end) {
            return getRange(start, end, 0, 0, rowSize);
        }

        private T getRange(int start, int end, int k, int l, int r) {
            if (r <= start || end <= l) {
                return e.get();
            }
            eval(k, l, r);
            if (start <= l && r <= end) {
                return getType(k);
            }
            T a = getRange(start, end, k * 2 + 1, l, (l + r) / 2);
            T b = getRange(start, end, k * 2 + 2, (l + r) / 2, r);
            return this.operator.apply(a, b);
        }

        //遅延評価
        private void eval(int k, int l, int r) {
            if (getLazy(k) != id.get()) {
                T a = getType(k);
                U b = getLazy(k);
                setType(k, this.mapping.apply(b, a));
                if (r - l > 1) {
                    mergeLazy(2 * k + 1, b);
                    mergeLazy(2 * k + 2, b);
                }
                setLazy(k, id.get());
            }
        }

        private void mergeLazy(int k, U f) {
            setLazy(k, this.composition.apply(f, getLazy(k)));
        }

        @SuppressWarnings("unchecked")
        private T getType(int index) {
            return (T) Objects.requireNonNullElse(this.array[index], e.get());
        }

        private void setType(int index, T e) {
            this.array[index] = (Object) e;
        }

        @SuppressWarnings("unchecked")
        private U getLazy(int index) {
            return (U) Objects.requireNonNullElse(this.lazy[index], id.get());
        }

        private void setLazy(int index, U e) {
            this.lazy[index] = (Object) e;
        }

        public void debug() {
            for (Object i : this.array) {
                System.out.println(i);
            }
        }

    }

}
