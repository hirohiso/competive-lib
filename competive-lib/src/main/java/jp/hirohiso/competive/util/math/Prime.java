package jp.hirohiso.competive.util.math;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Prime {

    public static void main(String[] args) {
        boolean[] primeTable = prime(10_000_000);
        Map<Long, Integer> map = factorization(100_000_007, primeTable);
        map.forEach((k, i) -> {
            System.out.println(k + "^" + i);
        });
    }


    //Nまでの素数を判定する
    public static boolean[] prime(int n) {
        boolean[] result = new boolean[n + 1];
        Arrays.fill(result, true);
        result[0] = false;
        result[1] = false;
        for (int i = 2; i < Math.sqrt(n); i++) {
            if (result[i] == false) {
                continue;
            }
            for (int j = 2; j * i < n; j++) {
                result[j * i] = false;
            }
        }
        return result;
    }

    //素因数分解
    public static Map<Long, Integer> factorization(long n, boolean[] prime) {
        Map<Long, Integer> map = new TreeMap<>();
        long temp = n;
        for (int i = 2; i < prime.length; i++) {
            if (!prime[i]) {
                continue;
            }
            int count = 0;
            while (temp % i == 0) {
                count++;
                temp = temp / i;
            }
            if (count != 0) {
                map.put((long) i, count);
            }
        }
        if (temp != 1) {
            map.put(temp, 1);
        }
        return map;
    }

}
