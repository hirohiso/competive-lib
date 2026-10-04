package jp.hirohiso.competive.util.math;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 篩と素因数分解。
 * <p>prime(n) は 0～n の素数判定表（true が素数）、spf(n) は最小素因数表を返す。
 * 篩の時間は O(N log log N)、領域 O(N)。boolean 表での素因数分解は表を走査し、
 * 表が対象整数の平方根までの素数を網羅すること。SPF版は表の範囲内の正整数が対象で
 * O(log n)。1の素因数分解は空Map。0や負整数の分解は対象外。</p>
 */
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
        if (n >= 1) result[1] = false;
        for (int i = 2; i <= n / i; i++) {
            if (result[i] == false) {
                continue;
            }
            for (int j = 2; j <= n / i; j++) {
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
