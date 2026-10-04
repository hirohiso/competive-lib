package jp.hirohiso.competive.util.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class ShortestPathTest {
    record Edge(int u, int v, long w) {}

    @Test
    void 非負グラフを独立した緩和法と比較() {
        var rng = new Random(290);
        for (int trial = 0; trial < 100; trial++) {
            int n = 1 + rng.nextInt(8), m = rng.nextInt(25);
            var edges = new ArrayList<Edge>();
            var wf = new Warshall.WarshallFloyd(n);
            for (int i = 0; i < m; i++) {
                var e = new Edge(rng.nextInt(n), rng.nextInt(n), rng.nextInt(10) * 1_000_000_000L);
                edges.add(e);
                wf.addDirectedEdge(e.u, e.v, e.w);
            }
            wf.solve();
            assertTrue(wf.hasAnswer());
            for (int s = 0; s < n; s++) {
                var d = new DijkstraLight.Dijkstra(n, m);
                for (var e : edges) d.addEdge(e.u, e.v, e.w);
                d.solve(s);
                long[] expected = new long[n];
                Arrays.fill(expected, Long.MAX_VALUE);
                expected[s] = 0;
                for (int k = 1; k < n; k++)
                    for (var e : edges)
                        if (expected[e.u] != Long.MAX_VALUE)
                            expected[e.v] = Math.min(expected[e.v], expected[e.u] + e.w);
                assertArrayEquals(
                        expected,
                        d.getDistance(),
                        "trial=" + trial + ", source=" + s + ", edges=" + edges);
                assertArrayEquals(
                        expected,
                        wf.answer()[s],
                        "trial=" + trial + ", source=" + s + ", edges=" + edges);
                int[] parent = d.getParent();
                for (int v = 0; v < n; v++)
                    if (v != s && expected[v] != Long.MAX_VALUE) {
                        final int x = v;
                        assertTrue(
                                edges.stream()
                                        .anyMatch(
                                                e ->
                                                        e.u == parent[x]
                                                                && e.v == x
                                                                && expected[e.u] + e.w
                                                                        == expected[x]));
                    }
            }
        }
    }

    @Test
    void 負辺と負閉路と無向辺() {
        var w = new Warshall.WarshallFloyd(4);
        w.addDirectedEdge(0, 1, -2);
        w.addDirectedEdge(1, 2, 1);
        w.solve();
        assertEquals(-1, w.answer()[0][2]);
        assertTrue(w.hasAnswer());
        assertEquals(Long.MAX_VALUE, w.answer()[3][0]);
        w.addDirectedEdge(2, 0, 0);
        w.solve();
        assertFalse(w.hasAnswer());
        var u = new Warshall.WarshallFloyd(2);
        u.addUndirectedEdge(0, 1, 3);
        u.addUndirectedEdge(0, 1, 8);
        u.solve();
        assertEquals(3, u.answer()[0][1]);
        assertEquals(3, u.answer()[1][0]);
    }
}
