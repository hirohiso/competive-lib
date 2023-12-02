package jp.hirohiso.competive.util.math;

import java.util.Arrays;

public class StaticMatrixRangeSumSolve {
    public static void main(String[] args) {
        var acc = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}};
        var mrs = new StaticMatrixRangeSum(acc);
        System.out.println(mrs.range(0, 0, 1, 1));
        System.out.println(mrs.range(0, 0, 3, 3));
        System.out.println(mrs.range(1, 1, 2, 2));
        System.out.println(mrs.range(1, 1, 3, 2));
    }


    /**
     * 二次元累積和
     */
    public static class StaticMatrixRangeSum {
        private long[][] acc;

        public StaticMatrixRangeSum(int[][] array) {
            acc = new long[array.length + 1][array[0].length + 1];
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    acc[i + 1][j + 1] = acc[i + 1][j] + acc[i][j + 1] - acc[i][j] + array[i][j];
                }
                System.out.println(Arrays.toString(acc[i + 1]));
            }
        }

        /**
         * [(x1,y1),(x2-1,y2-2)]の区間和を求める
         *
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         * @return
         */
        public long range(int x1, int y1, int x2, int y2) {
            return (acc[x2][y2] + acc[x1][y1] - acc[x1][y2] - acc[x2][y1]);
        }
    }
}
