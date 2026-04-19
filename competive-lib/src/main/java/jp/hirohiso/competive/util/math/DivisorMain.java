package jp.hirohiso.competive.util.math;

import java.util.ArrayList;
import java.util.Comparator;

public class DivisorMain {
    public static void main(String[] args) {

    }

    //約数列挙
    private ArrayList<Integer> divisor(int n) {
        var ret = new ArrayList<Integer>();
        for (int i = 1; i < n + 1; i++) {
            if (i * i > n) {
                break;
            }
            if (n % i != 0) {
                continue;
            }
            ret.add(i);
            if (n / i != i) {
                ret.add(n / i);
            }
        }
        ret.sort(Comparator.comparingInt(v-> v));
        return ret;
    }
}
