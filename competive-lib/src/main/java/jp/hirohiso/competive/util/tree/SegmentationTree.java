package jp.hirohiso.competive.util.tree;

import java.util.function.BinaryOperator;

public class SegmentationTree {

    public static void main(String[] args) {

        BinaryOperator<Integer> func = (i, j) -> {
            if (i == null) {
                return j;
            }
            if (j == null) {
                return i;
            }
            return i + j;
        };
        SegmentTree<Integer> st = new SegmentTree<>(new Integer[] { 1, 1, 2, 3, 5, 8, 13 }, func);
        st.debug();

        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));
        st.update(2, 5);
        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));
        st.update(0, 0);
        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));
    }

    //セグメンテーション木
    public static class SegmentTree<T> {
        //配列
        private Object[] array;
        //最下段のサイズ
        private int rowSize;

        //集約関数
        private BinaryOperator<T> operator;

        public SegmentTree(T[] input, BinaryOperator<T> ope) {
            int size = input.length;
            int n = 1;
            while (n < size) {
                n *= 2;
            }
            rowSize = n;
            //nは最下段に必要な要素数。
            //配列全体は2*n-1必要
            this.array = new Object[2 * n - 1];
            this.operator = ope;
            for (int i = 0; i < size; i++) {
                setType(i + n - 1, input[i]);
            }

            for (int i = n - 2; i >= 0; i--) {
                T a = getType(2 * i + 1);
                T b = getType(2 * i + 2);
                setType(i, this.operator.apply(a, b));
            }

        }

        public void update(int x, T val) {
            x += rowSize - 1;
            setType(x, val);
            while (x > 0) {
                x = (x - 1) / 2;
                T a = getType(2 * x + 1);
                T b = getType(2 * x + 2);
                setType(x, this.operator.apply(a, b));
            }
        }

        public T getRange(int start, int end) {
            return getRange(start, end, 0, 0, rowSize);
        }

        private T getRange(int start, int end, int k, int l, int r) {
            if (r <= start || end <= l) {
                return null;
            }
            if (start <= l && r <= end) {
                return getType(k);
            }
            T a = getRange(start, end, k * 2 + 1, l, (l + r) / 2);
            T b = getRange(start, end, k * 2 + 2, (l + r) / 2, r);
            return this.operator.apply(a, b);
        }

        @SuppressWarnings("unchecked")
        private T getType(int index) {
            return (T) this.array[index];
        }

        private void setType(int index, T e) {
            this.array[index] = (Object) e;
        }

        public void debug() {
            for (Object i : this.array) {
                System.out.println(i);
            }
        }

    }
}
