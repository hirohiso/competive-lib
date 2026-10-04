package jp.hirohiso.competive.util.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class KruskalTest {
    @Test
    void 全域木全列挙と比較() {
        var rng = new Random(294);
        for (int trial = 0; trial < 80; trial++) {
            int n = 2 + rng.nextInt(4), m = n + 3;
            int[][] edges = new int[m][3];
            for (int i = 0; i < m; i++)
                edges[i] =
                        new int[] {
                            i < n - 1 ? i : rng.nextInt(n),
                            i < n - 1 ? i + 1 : rng.nextInt(n),
                            rng.nextInt(21) - 10
                        };
            var k = new KruskalMethod.Kruskal(n);
            for (var e : edges) k.addEdge(e[0], e[1], e[2]);
            k.solve();
            long best = Long.MAX_VALUE;
            for (int mask = 0; mask < (1 << m); mask++)
                if (Integer.bitCount(mask) == n - 1) {
                    boolean[] seen = new boolean[n];
                    seen[0] = true;
                    long cost = 0;
                    for (int i = 0; i < m; i++) if ((mask & (1 << i)) != 0) cost += edges[i][2];
                    for (int pass = 0; pass < n; pass++)
                        for (int i = 0; i < m; i++)
                            if ((mask & (1 << i)) != 0) {
                                int[] e = edges[i];
                                if (seen[e[0]] || seen[e[1]]) seen[e[0]] = seen[e[1]] = true;
                            }
                    boolean all = true;
                    for (boolean v : seen) all &= v;
                    if (all) best = Math.min(best, cost);
                }
            assertEquals(
                    best, k.answer(), "trial=" + trial + ", edges=" + Arrays.deepToString(edges));
        }
    }

    @Test
    void 非連結グラフと大きな総和() {
        var k = new KruskalMethod.Kruskal(5);
        k.addEdge(0, 1, 2_000_000_000);
        k.addEdge(2, 3, 2_000_000_000);
        k.solve();
        assertEquals(4_000_000_000L, k.answer());
    }
}
