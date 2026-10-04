package jp.hirohiso.competive.util.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

public class SparseTableMain {

    public static void main(String[] args) {
        BinaryOperator<Long> ope = Long::min;

        var arr = new Long[]{1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l};

        var st = new SparseTable<>(arr, ope);

        System.out.println(st.query(0, 1));

        System.out.println(st.query(3, 6));
        System.out.println(st.query(0, 8));

    }

    /**
     * 更新しない配列の区間集約を O(1) で求める Sparse Table。
     * <p>演算は結合的かつ冪等であること（min/max/gcd 等。和は不可）。
     * 入力は非空。query(l,r) は {@code 0 <= l < r <= N} の半開区間。
     * 構築・領域 O(N log N)。入力配列の変更は反映されない。</p>
     */
    static class SparseTable<T> {

        BinaryOperator<T> ope;
        List<List<T>> list;
        int[] logtable;

        public SparseTable(T[] arr, BinaryOperator<T> ope) {
            this.ope = ope;

            var maxLength = 0;
            logtable = new int[arr.length + 1];
            while ((1 << (maxLength)) <= arr.length) {
                maxLength++;
            }
            list = new ArrayList<List<T>>(maxLength);
            for (int i = 0; i < maxLength; i++) {
                List<T> l = new ArrayList<>(arr.length);
                for (int j = 0; j < arr.length - (1 << i) + 1; j++) {
                    if (i > 0) {
                        var t = ope.apply(list.get(i - 1).get(j), list.get(i - 1).get(j + (1 << (i - 1))));
                        l.add(t);
                    } else {
                        l.add(arr[j]);
                    }
                }
                list.add(l);
            }
            for (int i = 2; i <= arr.length; i++) {
                logtable[i] = logtable[i >> 1] + 1;
            }
        }

        /**
         * [l,r)のクエリ処理。
         * @param l 含む左端 {@code 0 <= l < r}
         * @param r 含まない右端 {@code l < r <= N}
         * @return 区間の集約値
         */

        public T query(int l, int r) {
            var length = r - l;
            return ope.apply(
                    list.get(logtable[length]).get(l),
                    list.get(logtable[length]).get(r - (1 << logtable[length]))
            );
        }
    }
}
