package jp.hirohiso.competive.util.math;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class StaticRangeSumSolve {
    public static void main(String[] args) {
        var array = new int[]{1,2,3,4,5,6,7,8,9,10};
        var rs = new RangeSum(array);
        System.out.println(rs.range(0,1));
        System.out.println(rs.range(0,10));
    }

    /**
     * 一次元累積和を管理
     */
    public static class RangeSum{
        private long[] acc;
        public RangeSum(int[] array){
            Function<Integer,Long> func = i -> (long) array[i];
            new RangeSum(array.length, func);
        }
        public RangeSum(long[] array){
            Function<Integer,Long> func = i -> (long) array[i];
            new RangeSum(array.length, func);
        }

        public RangeSum(int size, Function<Integer,Long> operator){
            acc = new long[ size + 1];
            for (int i = 0; i < size; i++) {
                acc[i + 1] = acc[i] + operator.apply(i);
            }
        }

        /**
         * 区間[l,r-1]の累積和を求めます
         * @param l　区間の開始
         * @param r　区間の終了
         * @return lからr-1を累積した和
         */
        public long range(int l, int r){
            return acc[r] - acc[l];
        }
    }
}
