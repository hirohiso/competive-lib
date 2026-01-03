package jp.hirohiso.competive.util.tree;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SegmentationTree {

    public static void main(String[] args) {

        BinaryOperator<Integer> func = Integer::sum;
        SegmentTree<Integer> st = new SegmentTree<>(new Integer[]{1, 1, 2, 3, 5, 8, 13}, func);
        //st.debug();

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


        st.debug();
        System.err.println(Arrays.toString(st.array));
        System.err.println(st.maxRight(0, (v) -> v <= 1));
        System.err.println(st.maxRight(0, (v) -> v <= 2));
        System.err.println(st.maxRight(0, (v) -> v <= 35));
        System.err.println(st.maxRight(0, (v) -> v < 35));
        System.err.println(st.maxRight(3, (v) -> v < 16));
        System.err.println(st.maxRight(3, (v) -> v <= 16));

        System.err.println("========");
        System.err.println(st.minLeft(6, (v) -> v <= 1));
        System.err.println(st.minLeft(6, (v) -> v <= 8));
        System.err.println(st.minLeft(6, (v) -> v <= 12));
        System.err.println(st.minLeft(6, (v) -> v <= 13));
        System.err.println(st.minLeft(6, (v) -> v <= 35));
        System.err.println(st.minLeft(7, (v) -> v < 35));
    }

    /**
     * セグメント木（Segment Tree）を実装するクラス。
     * <p>
     * セグメント木は、配列の区間に対する集約クエリと要素の更新を高速に処理するデータ構造です。
     * 時間計算量は、クエリ・更新ともにO(log n)です。
     * </p>
     *
     * @param <T> 要素の型
     */
    public static class SegmentTree<T> {
        /**
         * セグメント木の内部配列
         */
        private Object[] array;

        /**
         * 最下段のサイズ（元の配列サイズを超える最小の2のべき乗）
         */
        private int rowSize;

        /**
         * 単位元を提供するサプライヤー（nullの場合は単位元なし）
         */
        private Supplier<T> ie = null;

        /**
         * 区間を集約するための二項演算子
         */
        private BinaryOperator<T> operator;


        /***
         * 要素数
         */
        private int N;

        /**
         * 配列と演算子、単位元サプライヤーを指定してセグメント木を構築します。
         *
         * @param input 初期配列
         * @param ope   区間を集約する二項演算子
         * @param ie    単位元を提供するサプライヤー
         */
        public SegmentTree(T[] input, BinaryOperator<T> ope, Supplier<T> ie) {
            int size = input.length;
            N = input.length;
            this.ie = ie;
            int n = 1;
            while (n < size) {
                n *= 2;
            }
            rowSize = n;
            //nは最下段に必要な要素数。
            //配列全体は2*n-1必要
            this.array = new Object[2 * n - 1];
            this.operator = wrapNullAsE(ope);
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
         * サイズと演算子を指定してセグメント木を構築します（単位元なし）。
         *
         * @param size 配列のサイズ
         * @param ope  区間を集約する二項演算子
         */
        public SegmentTree(int size, BinaryOperator<T> ope) {
            this(size, ope, null);
        }

        /**
         * 配列と演算子を指定してセグメント木を構築します（単位元なし）。
         *
         * @param input 初期配列
         * @param ope   区間を集約する二項演算子
         */
        public SegmentTree(T[] input, BinaryOperator<T> ope) {
            this(input, ope, null);
        }

        /**
         * サイズと演算子、単位元サプライヤーを指定してセグメント木を構築します。
         *
         * @param size 配列のサイズ
         * @param ope  区間を集約する二項演算子
         * @param ie   単位元を提供するサプライヤー
         */
        @SuppressWarnings("unchecked")
        public SegmentTree(int size, BinaryOperator<T> ope, Supplier<T> ie) {
            this((T[]) new Object[size], ope, ie);
        }

        /**
         * 二項演算子をnullセーフにラップします。
         * <p>
         * いずれかの引数がnullの場合、もう一方の値を返します。
         * 両方がnullでない場合は元の演算子を適用します。
         * </p>
         *
         * @param <S> 演算対象の型
         * @param ope ラップする二項演算子
         * @return nullセーフな二項演算子
         */
        private static <S> BinaryOperator<S> wrapNullAsE(BinaryOperator<S> ope) {
            return (m1, m2) -> {
                if (m1 == null) {
                    return m2;
                }
                if (m2 == null) {
                    return m1;
                }
                return ope.apply(m1, m2);
            };
        }

        /**
         * 指定されたインデックスの要素を取得します。
         *
         * @param index 取得する要素のインデックス（0-based）
         * @return 指定されたインデックスの要素
         */
        public T get(int index) {
            return getType(index + rowSize - 1);
        }

        /**
         * 指定されたインデックスの要素を更新します。
         * <p>
         * 更新後、影響を受ける親ノードも再計算されます。
         * 時間計算量: O(log n)
         * </p>
         *
         * @param x   更新する要素のインデックス（0-based）
         * @param val 新しい値
         */
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

        /**
         * 指定されたインデックスの要素と値をマージします。
         * <p>
         * 現在の値と新しい値を演算子で結合した結果で更新します。
         * 時間計算量: O(log n)
         * </p>
         *
         * @param x   マージする要素のインデックス（0-based）
         * @param val マージする値
         */
        public void merge(int x, T val) {
            var now = get(x);
            update(x, this.operator.apply(now, val));
        }

        /**
         * 指定された区間の集約結果を取得します。
         * <p>
         * 区間は [start, end) の半開区間で指定します。
         * 時間計算量: O(log n)
         * </p>
         *
         * @param start 区間の開始インデックス（含む）
         * @param end   区間の終了インデックス（含まない）
         * @return 指定された区間を演算子で集約した結果
         */
        public T getRange(int start, int end) {
            return getRange(start, end, 0, 0, rowSize);
        }

        /**
         * 区間集約の内部実装（再帰処理）。
         *
         * @param start 要求区間の開始インデックス
         * @param end   要求区間の終了インデックス
         * @param k     現在のノードのインデックス
         * @param l     現在のノードが表す区間の開始
         * @param r     現在のノードが表す区間の終了
         * @return 区間の集約結果
         */
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

        /**
         * 内部配列から型変換して要素を取得します。
         *
         * @param index 内部配列のインデックス
         * @return 型変換された要素
         */
        @SuppressWarnings("unchecked")
        private T getType(int index) {
            return (T) this.array[index];
        }

        /**
         * 内部配列に要素を設定します。
         * <p>
         * 要素がnullで単位元サプライヤーが設定されている場合、単位元を使用します。
         * </p>
         *
         * @param index 内部配列のインデックス
         * @param e     設定する要素
         */
        private void setType(int index, T e) {
            if (e == null && ie != null) {
                e = ie.get();
            }
            this.array[index] = (Object) e;
        }

        public int maxRight(int l, Predicate<T> f) {
            if (l == N) {
                return N;
            }
            l += (rowSize - 1);
            T sum = null;
            if (ie != null) {
                sum = ie.get();
            }
            do {
                while (l % 2 == 1) {
                    l--;
                    l >>= 1;
                }
                if (!f.test(operator.apply(sum, getType(l)))) {
                    while (l < (rowSize - 1)) {
                        l <<= 1;
                        l++;
                        if (f.test(operator.apply(sum, getType(l)))) {
                            sum = operator.apply(sum, getType(l));
                            l++;
                        }
                    }
                    return l - (rowSize - 1);
                }
                sum = operator.apply(sum, getType(l));
                l++;
            } while (((l + 1) & -(l + 1)) != (l + 1));
            return N;
        }


        public int minLeft(int r, Predicate<T> f) {
            if (r == 0) {
                return 0;
            }
            r--;
            r += (rowSize - 1);
            T sum = null;
            if (ie != null) {
                sum = ie.get();
            }
            do {
                while (r % 2 == 0) {
                    r--;
                    r >>= 1;
                }
                if (!f.test(operator.apply(getType(r), sum))) {
                    while (r < (rowSize - 1)) {
                        r <<= 1;
                        r += 2;
                        if (f.test(operator.apply(getType(r), sum))) {
                            sum = operator.apply(getType(r), sum);
                            r--;
                        }
                    }
                    return r + 1 - (rowSize - 1);
                }
                sum = operator.apply(getType(r), sum);
                r--;
            } while (((r + 1) & -(r + 1)) != (r + 1));
            return 0;
        }

        /**
         * セグメント木の内部状態をデバッグ出力します。
         * <p>
         * 木構造を階層ごとに標準エラー出力に出力します。
         * </p>
         */
        public void debug() {
            var rowNum = 0;
            var maxRow = 0;
            var t = this.rowSize;
            while (t > 0) {
                maxRow++;
                t >>= 1;
            }
            for (int i = 0; i < this.array.length; i++) {
                System.err.print(this.array[i]);
                if ((1 << (rowNum + 1)) - 2 == i) {
                    System.err.println();
                    rowNum++;
                } else {
                    System.err.print(" , ");
                }
            }
            System.err.println();
        }

    }
}
