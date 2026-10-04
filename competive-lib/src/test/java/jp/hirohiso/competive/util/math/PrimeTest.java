package jp.hirohiso.competive.util.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class PrimeTest {
    static Map<Long, Integer> trial(int n) {
        var m = new TreeMap<Long, Integer>();
        for (long p = 2; p * p <= n; p++)
            while (n % p == 0) {
                m.merge(p, 1, Integer::sum);
                n /= p;
            }
        if (n > 1) m.put((long) n, 1);
        return m;
    }

    @Test
    void 篩の端点と平方数() {
        for (int n = 1; n <= 120; n++) {
            var primes = Prime.prime(n);
            for (int i = 0; i <= n; i++)
                assertEquals(
                        i >= 2 && trial(i).equals(Map.of((long) i, 1)),
                        primes[i],
                        "n=" + n + ", i=" + i);
        }
    }

    @Test
    void 素因数分解を試し割りと比較() {
        var primes = Prime.prime(1000);
        var spf = Prime.spf(1000);
        for (int n = 1; n <= 1000; n++) {
            assertEquals(trial(n), Prime.factorization(n, primes));
            assertEquals(trial(n), Prime.factorization(n, spf));
        }
        assertEquals(Map.of(1009L, 1), Prime.factorization(1009, Prime.prime(32)));
    }
}
