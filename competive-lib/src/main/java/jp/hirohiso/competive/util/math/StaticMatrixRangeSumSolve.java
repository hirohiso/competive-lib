package jp.hirohiso.competive.util.math;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StaticMatrixRangeSumSolve {
    public static void main(String[] args) {
        var mrs1 = new MultiRangeSum(10, 9);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                mrs1.update(i * j, i, j);
            }
        }
        mrs1.build();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                var ans = mrs1.rangeSum(new int[]{i, j}, new int[]{i, j});
                System.out.print(ans + " ");
            }
            System.out.println();
        }

        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 9; j++) {
                var ans = mrs1.rangeSum(new int[]{i, j}, new int[]{0, 0});
                System.out.print(ans + " ");
            }
            System.out.println();
        }
    }


    public static class MultiRangeSum {
        int[] dims;
        int[] mul;

        long[] flattenArr;

        boolean isBuild = false;

        public MultiRangeSum(int... arr) {
            dims = new int[arr.length];
            for (int i = 0; i < dims.length; i++) {
                dims[i] = arr[i] + 1;
            }

            var base = 1;
            mul = new int[dims.length];
            for (int i = 0; i < dims.length; i++) {
                base *= dims[i];
                mul[i] = base;
            }
            flattenArr = new long[mul[mul.length - 1]];
        }

        public void update(long v, int... arr) {
            if (isBuild) {
                throw new IllegalStateException("after build");
            }
            var temp = Arrays.copyOf(arr, arr.length);
            for (int i = 0; i < temp.length; i++) {
                temp[i]++;
            }
            var idx = idx(temp);
            flattenArr[idx] = v;
        }

        public void build() {
            if (isBuild) {
                throw new IllegalStateException("after build");
            }
            isBuild = true;
            var size = mul.length;
            for (int i = 0; i < size; i++) {
                //i次元方向の増分
                var d = mul[i] / dims[i];
                for (int j = 0; j < flattenArr.length; j++) {
                    //d方向に加算。閾値は考慮
                    var nj = j + d;
                    if (nj / mul[i] == j / mul[i]) {
                        flattenArr[nj] += flattenArr[j];
                    }
                }
            }
        }

        //arr1からarr2までの範囲をの累積和を求める
        //arr[j] >= arr2[j]が成立していること
        public long rangeSum(int[] arr1, int[] arr2) {
            if (!isBuild) {
                throw new IllegalStateException("before build");
            }
            var temp1 = new int[arr1.length];
            var temp2 = new int[arr2.length];
            for (int i = 0; i < arr1.length; i++) {
                temp1[i] = arr1[i] + 1;
                temp2[i] = arr2[i];
            }

            var size = temp1.length;
            var max = 1 << size;

            var ans = 0L;
            for (int i = 0; i < max; i++) {
                var pos = (Integer.bitCount(i) % 2 == 0) ? 1 : -1;//偶数なら加算、奇数なら原産
                var arr = new int[temp1.length];
                for (int j = 0; j < size; j++) {
                    if ((i & (1 << j)) != 0) {
                        arr[j] = temp1[j];
                    } else {
                        arr[j] = temp2[j];
                    }
                }
                var idx = idx(arr);
                ans += (pos) * flattenArr[idx];
            }
            return ans;
        }

        private int idx(int... arr) {
            var idx = 0;
            for (int i = 0; i < arr.length; i++) {
                idx += arr[i] * (i != 0 ? mul[i - 1] : 1);
            }
            return idx;
        }
    }


    /**
     * 二次元累積和
     */
    public static class StaticMatrixRangeSum {
        private long[][] acc;

        public StaticMatrixRangeSum(int[][] array) {
            this(array.length, array[0].length, (i, j) -> (long) array[i][j]);
        }

        public StaticMatrixRangeSum(long[][] array) {
            this(array.length, array[0].length, (i, j) -> array[i][j]);
        }

        public StaticMatrixRangeSum(int n, int m, BiFunction<Integer, Integer, Long> function) {
            acc = new long[n + 1][m + 1];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    acc[i + 1][j + 1] = acc[i + 1][j] + acc[i][j + 1] - acc[i][j] + function.apply(i, j);
                }
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
            return (acc[x2 + 1][y2 + 1] + acc[x1][y1] - acc[x1][y2 + 1] - acc[x2 + 1][y1]);
        }
    }
}
