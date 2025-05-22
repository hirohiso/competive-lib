package jp.hirohiso.competive.util.math;

import java.util.ArrayList;
import java.util.LinkedList;


//精進に生えた汎用的なメソッドの置き場所
public class TempMain {
    public static void main(String[] args) {

    }

    //区別のないn個の要素を区別のあるk個のグループに分割する。割り当てない要素も許容する
    //arr: 探索列
    //level: 現在見ているidx
    //n:要素全体の数
    //s:まだ割り当てていない要素数
    //ret: 結果
    private void dfs(int[] arr, int level, int n, int s, LinkedList<ArrayList<Integer>> ret) {
        if (level >= n) {
            var list = new ArrayList<Integer>();
            for (int j : arr) {
                list.add(j);
            }
            ret.add(list);
        } else {
            for (int i = 0; i <= s; i++) {
                arr[level] = i;
                dfs(arr, level + 1, n, s - i, ret);
            }
        }
    }
}
