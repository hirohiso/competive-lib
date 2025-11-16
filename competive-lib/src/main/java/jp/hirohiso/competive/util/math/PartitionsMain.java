package jp.hirohiso.competive.util.math;

import java.util.ArrayList;
import java.util.List;

public class PartitionsMain {

    public static void main(String[] args) {
        var ret = new ArrayList<List<Integer>>();
        var v = new ArrayList<Integer>();
        partition(10, 10, v, ret);
        System.out.println(ret);
    }


    private static void partition(int n, int max, ArrayList<Integer> v, ArrayList<List<Integer>> ret) {
        if (n == 0) {
            ret.add(v.stream().toList());
        }
        for (int i = Math.min(n, max); i > 0; i--) {
            v.add(i);
            partition(n - i, i, v, ret);
            v.remove(v.size() - 1);
        }
    }

    private static int toState(int[] arr, int[] cnt) {
        var ret = 1;
        for (int i = 0; i < cnt.length; i++) {
            ret = (ret - 1) * (cnt[i] + 1) + (arr[i] + 1);
        }
        return ret - 1;
    }

    private static int[] toArray(int state, int[] cnt) {
        var ret = new int[cnt.length];
        for (int i = cnt.length - 1; i >= 0; i--) {
            ret[i] = state % (cnt[i] + 1);
            state = state / (cnt[i] + 1);
        }
        return ret;
    }
}
