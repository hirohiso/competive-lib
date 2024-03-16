package jp.hirohiso.competive.util.sequence;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Sequence {

    public static void main(String[] args) {
        var arr = new int[]{3, 2, 4, 1, 3, 5, 2, 100};
        var list = LongestIncreasingSubsequence.lis(arr);
        System.out.println(list.size());
        for (Integer i : list){
            System.out.print(i + " ");
        }
        System.out.println();
    }


    public static class LongestIncreasingSubsequence {
        //LIS 最長部分増加列
        public static List<Integer> lis(int[] array) {
            int N = array.length;//数値の個数

            var dp = new int[N + 1];
            var dpi = new int[N + 1];//dpが示す要素のインデックス
            var table = new int[N];//自分より小さな要素。先頭の場合は-1
            Arrays.fill(table, -1);
            Arrays.fill(dp, Integer.MAX_VALUE);
            Arrays.fill(dpi, -1);
            dp[0] = array[0];
            dpi[0] = 0;
            var n = 1; //現在のdpの末尾
            for (int i = 1; i < N; i++) {
                var a = array[i];//n番目の数
                if (dp[n - 1] < a) {
                    dp[n] = a;
                    dpi[n] = i;
                    table[i] = dpi[n - 1];
                    n++;
                    continue;
                }
                var point = lb(dp, a);//挿入ポイント
                dp[point] = a;
                dpi[point] = i;
                if (point == 0) {
                    table[i] = -1;
                } else {
                    table[i] = dpi[point - 1];
                }
            }
            var tail = dpi[lb(dp, Integer.MAX_VALUE) - 1];
            var list = new LinkedList<Integer>();
            while (tail != -1) {
                list.addFirst(array[tail]);
                tail = table[tail];
            }
            return list;
        }

        public static final int lb(final int[] arr, final long value) {
            int low = 0;
            int high = arr.length;
            int mid;
            while (low < high) {
                mid = ((high - low) >>> 1) + low;    //(low + high) / 2 (オーバーフロー対策)
                if (arr[mid] < value) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            return low;
        }
    }
}
