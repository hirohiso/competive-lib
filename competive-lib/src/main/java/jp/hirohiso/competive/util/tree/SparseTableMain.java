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

    }

    /**
     * Tおよび結合律と冪等性を満たす演算ope
     * <p>
     * 単位元や逆元がなくても使用可能
     * 更新なし
     * 構築 O(n logn)
     * クエリ O(1)
     */
    private static class SparseTable<T> {

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
         * @param l　含む 1<=
         * @param r　含まない　<= N
         * @return
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
