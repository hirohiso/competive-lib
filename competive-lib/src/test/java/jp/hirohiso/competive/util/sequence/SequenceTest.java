package jp.hirohiso.competive.util.sequence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class SequenceTest {
    @Test
    void LISの長さと復元を二乗時間DPと比較() {
        var rng = new Random(541);
        for (int trial = 0; trial < 200; trial++) {
            int n = 1 + rng.nextInt(25);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rng.nextInt(15) - 7;
            if (trial == 0) a = new int[] {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
            int[] dp = new int[a.length];
            int best = 0;
            for (int i = 0; i < a.length; i++) {
                dp[i] = 1;
                for (int j = 0; j < i; j++) if (a[j] < a[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
                best = Math.max(best, dp[i]);
            }
            Integer[] boxed = Arrays.stream(a).boxed().toArray(Integer[]::new);
            for (var result :
                    List.of(
                            Sequence.LongestIncreasingSubsequence.lis(a),
                            new Sequence.GenericsLIS<>(
                                            boxed,
                                            Comparator.nullsLast(
                                                    Comparator.<Integer>naturalOrder()))
                                    .solve())) {
                assertEquals(best, result.size(), Arrays.toString(a));
                int pos = 0;
                Integer prev = null;
                for (int x : result) {
                    if (prev != null) assertTrue(prev < x);
                    while (pos < a.length && a[pos] != x) pos++;
                    assertTrue(pos < a.length);
                    pos++;
                    prev = x;
                }
            }
        }
    }

    @Test
    void 座標圧縮は大小関係と重複を保存する() {
        var rng = new Random(542);
        for (int trial = 0; trial < 100; trial++) {
            long[] a = new long[rng.nextInt(30)];
            for (int i = 0; i < a.length; i++) a[i] = rng.nextInt(15) - 7;
            if (a.length >= 2) {
                a[0] = Long.MIN_VALUE;
                a[1] = Long.MAX_VALUE;
            }
            long[] saved = a.clone(),
                    sorted = Arrays.stream(a).distinct().sorted().toArray(),
                    result = ZaatsuMain.arrayComp(a);
            for (int i = 0; i < a.length; i++)
                assertEquals(Arrays.binarySearch(sorted, a[i]), result[i]);
            assertArrayEquals(saved, a);
        }
    }
}
