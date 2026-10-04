package jp.hirohiso.competive.util.tree;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class DualSegmentTreeMain {
    public static void main(String[] args) {
        var input = new Integer[]{1, 1, 2, 3, 5, 8, 13};
        //区間Max操作・区間和取得
        var st = new DualSegmentTree<Integer, Integer>(
                input,
                Math::max, // 遅延値を適用する関数
                Math::max, // 遅延値の合成
                () -> 0, // 初期値
                () -> 0 // 遅延値の初期値
        );
        st.debug();
        st.apply(0, input.length, 3);
        st.apply(0, 2, 5);
        st.apply(4, 6, 7);


        for(int i = 0; i < input.length; i++) {
            System.out.println("Index " + i + ": " + st.get(i));
        }
    }

    //双対セグメンテーション木
    //T : Data
    //U : Lazy
    /**
     * 区間更新・一点取得用の双対セグメント木。
     * <p>入力は非空配列。0始まり、apply は半開区間 {@code [start,end)}。
     * composition(new,old) は old の後に new を適用する作用。非可換な作用も扱える。
     * 構築 O(N)、更新と取得 O(log N)、領域 O(N)。mapping/id は作用とその単位元、
     * e は未設定値の単位元。渡す関数は引数を破壊しないこと。</p>
     */
    public static class DualSegmentTree<T, U> {
        //配列
        private Object[] array, lazy;
        //最下段のサイズ
        private int rowSize;

        //遅延評価関数<U,Tを受取>
        //Tに対して遅延値Uを作用させて値を更新する
        private BiFunction<U, T, T> mapping;
        //遅延値Uの加算
        private BinaryOperator<U> composition;

        //初期化関数T
        private final Supplier<T> e;
        //初期化関数U
        private Supplier<U> id;

        public DualSegmentTree(T[] input, BiFunction<U, T, T> mapping, BinaryOperator<U> composition,
                               Supplier<T> e, Supplier<U> id) {
            int size = input.length;
            int n = 1;
            while (n < size) {
                n *= 2;
            }
            rowSize = n;
            //nは最下段に必要な要素数。
            //配列全体は2*n-1必要
            this.array = new Object[input.length];
            this.lazy = new Object[2 * n - 1];
            this.mapping = mapping;
            this.composition = composition;
            this.e = e;
            this.id = id;
            for (int i = 0; i < this.array.length; i++) {
                setType(i, input[i]);
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
            if (r <= start || end <= l) {
                return;
            }
            //被覆しているならlazyに格納したあと評価
            if (start <= l && r <= end) {
                mergeLazy(k, f);
            } else {
                // 子に進む前に古い作用を伝播し、更新の時系列を保つ。
                push(k);
                apply(start, end, k * 2 + 1, l, (l + r) / 2, f);
                apply(start, end, k * 2 + 2, (l + r) / 2, r, f);
            }
        }


        private void mergeLazy(int k, U f) {
            setLazy(k, this.composition.apply(f, getLazy(k)));
        }

        private void push(int k) {
            U f = getLazy(k);
            mergeLazy(2 * k + 1, f);
            mergeLazy(2 * k + 2, f);
            setLazy(k, id.get());
        }

        public T get(int index) {
            var value = getType(index);
            for (int k = index + rowSize - 1; ; k = (k - 1) / 2) {
                value = this.mapping.apply(getLazy(k), value);
                if (k == 0) return value;
            }
        }

        @SuppressWarnings("unchecked")
        private T getType(int index) {
            if (index < 0 || index >= this.array.length) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.array.length);
            }
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
