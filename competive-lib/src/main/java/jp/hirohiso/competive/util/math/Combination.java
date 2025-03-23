package jp.hirohiso.competive.util.math;

public class Combination {

    public static void main(String[] args) {
        var com = new CombinationHelper(1000000, 1000000007);


        System.out.println(com.cal(8, 3));
    }

    static class CombinationHelper {
        final int MAX;
        final int MOD;
        final long[] fac;
        final long[] finv;
        final long[] inv;

        public CombinationHelper(int max, int mod) {
            MOD = mod;
            MAX = max;

            fac = new long[MAX];
            finv = new long[MAX];
            inv = new long[MAX];

            fac[0] = fac[1] = 1;
            finv[0] = finv[1] = 1;
            inv[1] = 1;
            for (int i = 2; i < MAX; i++) {
                fac[i] = fac[i - 1] * i % MOD;
                inv[i] = MOD - inv[MOD % i] * (MOD / i) % MOD;
                finv[i] = finv[i - 1] * inv[i] % MOD;
            }
        }

        // 二項係数計算
        public long cal(int n, int k) {
            if (n < k)
                return 0;
            if (n < 0 || k < 0)
                return 0;
            return (fac[n] * (finv[k] * finv[n - k] % MOD) % MOD);
        }
    }

}
