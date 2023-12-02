package jp.hirohiso.competive.util.math;

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
            acc = new long[ array.length + 1];
            for (int i = 0; i < array.length; i++) {
                acc[i + 1] = acc[i] + array[i];
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
