package jp.hirohiso.competive.util.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

class ArithmeticTest {
    @Test
    void gcdと拡張互除法とlcm() {
        for (long a = 1; a <= 60; a++)
            for (long b = 1; b <= 60; b++) {
                long g = BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValue();
                assertEquals(g, Gcd.MathUtil.gcd(a, b));
                assertEquals(g, Gcd.MathUtil.gcd(-a, b));
                assertEquals(a / g * b, Gcd.MathUtil.lcm(a, b));
                var p = Gcd.MathUtil.getExtGcdPara();
                assertEquals(g, Gcd.MathUtil.extgcd(a, b, p));
                assertEquals(g, a * p.x + b * p.y);
                long x = p.x, y = p.y;
                assertSame(p, p.swap());
                assertEquals(y, p.x);
                assertEquals(x, p.y);
            }
        assertEquals(5, Gcd.MathUtil.gcd(0, -5));
        assertEquals(5, Gcd.MathUtil.gcd(5, 0));
    }

    @Test
    void floorSumと冪剰余を逐次計算() {
        for (int n = 0; n < 15; n++)
            for (int m = 1; m < 12; m++)
                for (int a = 0; a < 12; a++)
                    for (int b = 0; b < 12; b++) {
                        long sum = 0;
                        for (int i = 0; i < n; i++) sum += (a * i + b) / m;
                        assertEquals(sum, Gcd.MathUtil.floor_sum(n, m, a, b));
                    }
        for (int a = 0; a < 10; a++)
            for (int n = 0; n < 15; n++)
                for (int m = 2; m < 12; m++)
                    assertEquals(
                            BigInteger.valueOf(a)
                                    .modPow(BigInteger.valueOf(n), BigInteger.valueOf(m))
                                    .longValue(),
                            Gcd.MathUtil.powmod(a, n, m));
    }

    @Test
    void 互いに素な法のCRT() {
        for (int a = 0; a < 5; a++)
            for (int b = 0; b < 7; b++)
                for (int c = 0; c < 11; c++) {
                    long value =
                            Gcd.MathUtil.chineseRemainderTheorem(
                                    new long[] {a, b, c}, new long[] {5, 7, 11});
                    assertTrue(value >= 0 && value < 385);
                    assertEquals(a, value % 5);
                    assertEquals(b, value % 7);
                    assertEquals(c, value % 11);
                }
    }

    @Test
    void 組合せをPascalの三角形と比較() {
        for (int mod : new int[] {101, 998244353}) {
            int n = 70;
            var c = new Combination.CombinationHelper(n + 1, mod);
            long[][] dp = new long[n + 1][n + 1];
            dp[0][0] = 1;
            for (int i = 1; i <= n; i++) {
                dp[i][0] = 1;
                for (int j = 1; j <= i; j++) dp[i][j] = (dp[i - 1][j - 1] + dp[i - 1][j]) % mod;
            }
            for (int i = 0; i <= n; i++)
                for (int j = 0; j <= n; j++) assertEquals(dp[i][j], c.cal(i, j));
            assertEquals(0, c.cal(-1, 0));
            assertEquals(0, c.cal(2, -1));
        }
    }

    @Test
    void 拡張互除法はintを超える係数を返す() {
        var p = Gcd.MathUtil.getExtGcdPara();
        long a = 10_000_000_007L, b = 3;
        assertEquals(1, Gcd.MathUtil.extgcd(a, b, p));
        assertEquals(1, a * p.x + b * p.y);
    }
}
