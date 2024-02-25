package jp.hirohiso.competive.util.math;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Prime {

    public static void main(String[] args) {
        boolean[] primeTable = prime(10_000_000);
        var spf = spf(100_000_007);
        Map<Long, Integer> map = factorization(100_000_007, spf);
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
    //ルート n???
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

    //SPF利用　素因数分解

    //素因数分解　前処理N log log N
    public static int[] spf(int n) {
        int[] spf = new int[n + 1];
        Arrays.fill(spf, 0);
        for (int i = 0; i < spf.length; i++) {
            spf[i] = i;
        }
        for (int i = 2; i * i <= n; i++) {
            if(spf[i] == i){
                for (int j = i * i; j <= n; j+=i) {
                    if(spf[j] == j){
                        spf[j] = i;
                    }
                }
            }
        }
        return spf;
    }
    //素因数分解 log n
    public static Map<Long, Integer> factorization(int n,int[] spf) {
        Map<Long, Integer> map = new HashMap<>();
        while (n != 1){
            map.merge((long)spf[n], 1 , Integer::sum);
            n /= spf[n];
        }
        return map;
    }

}
