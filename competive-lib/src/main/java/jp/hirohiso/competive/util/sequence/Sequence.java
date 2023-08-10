package jp.hirohiso.competive.util.sequence;

import java.util.Arrays;

public class Sequence {

    //LSI 最長部分増加列
    public static int lsi(int[] array){
        int N = array.length;//数値の個数

        int[] dp = new int[N];
        Arrays.fill(dp,Integer.MAX_VALUE);
        dp[0] = array[0];
        for (int i = 1; i < N; i++) {
            int a = array[i];//n番目の数
            if(dp[i-1] < a){
                dp[i] = a;
                continue;
            }
            int point = (-(Arrays.binarySearch(dp,a)))-1;//挿入ポイント
            dp[point] = a;
        }
        return (-Arrays.binarySearch(dp,Integer.MAX_VALUE-1));
    }
}
