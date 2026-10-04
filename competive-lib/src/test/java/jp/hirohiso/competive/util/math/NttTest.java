package jp.hirohiso.competive.util.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class NttTest {
    @Test
    void 畳み込みを二重ループと比較() {
        var rng = new Random(641);
        var ntt = new NttLib.NumberTheoreticTransform();
        long mod = 998244353;
        for (int trial = 0; trial < 120; trial++) {
            int n = 1 + rng.nextInt(20), m = 1 + rng.nextInt(20);
            long[] a = new long[n], b = new long[m], expected = new long[n + m - 1];
            for (int i = 0; i < n; i++) a[i] = rng.nextInt((int) mod);
            for (int i = 0; i < m; i++) b[i] = rng.nextInt((int) mod);
            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++) expected[i + j] = (expected[i + j] + a[i] * b[j]) % mod;
            assertArrayEquals(
                    expected,
                    ntt.convolution(a, b),
                    "a=" + Arrays.toString(a) + ", b=" + Arrays.toString(b));
        }
        assertArrayEquals(new long[] {6}, ntt.convolution(new long[] {2}, new long[] {3}));
    }

    @Test
    void 変換そのものを離散フーリエ和と比較() {
        long mod = 998244353;
        var ntt = new NttLib.NumberTheoreticTransform();
        for (int n : new int[] {1, 2, 4, 8, 16}) {
            var a = new NttLib.NttRing[n];
            for (int i = 0; i < n; i++) a[i] = NttLib.NttRing.from(i * i + 2);
            long root =
                    java.math.BigInteger.valueOf(3)
                            .modPow(
                                    java.math.BigInteger.valueOf((mod - 1) / n),
                                    java.math.BigInteger.valueOf(mod))
                            .longValue();
            var transformed = ntt.fft(a);
            for (int k = 0; k < n; k++) {
                long expected = 0;
                for (int j = 0; j < n; j++) {
                    long power =
                            java.math.BigInteger.valueOf(root)
                                    .modPow(
                                            java.math.BigInteger.valueOf((long) j * k),
                                            java.math.BigInteger.valueOf(mod))
                                    .longValue();
                    expected = (expected + a[j].toLong() * power) % mod;
                }
                assertEquals(expected, transformed[k].toLong());
            }
            var restored = ntt.ifft(transformed);
            for (int i = 0; i < n; i++) assertEquals(a[i].toLong(), restored[i].toLong());
        }
    }
}
