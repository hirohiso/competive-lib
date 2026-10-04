package jp.hirohiso.competive.util.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class FlowTest {
    record Edge(int u, int v, int cap, int cost) {}

    @Test
    void 最大流と全カット最小値の一致() {
        var rng = new Random(292);
        for (int trial = 0; trial < 100; trial++) {
            int n = 2 + rng.nextInt(6);
            var edges = new ArrayList<Edge>();
            var d = new Dinic.DinicFlow(n);
            for (int i = 0; i < 20; i++) {
                var e = new Edge(rng.nextInt(n), rng.nextInt(n), rng.nextInt(5), 0);
                edges.add(e);
                d.addEdge(e.u, e.v, e.cap);
            }
            long min = Long.MAX_VALUE;
            for (int mask = 1; mask < (1 << n); mask += 2)
                if ((mask & (1 << (n - 1))) == 0) {
                    long cut = 0;
                    for (var e : edges)
                        if ((mask & (1 << e.u)) != 0 && (mask & (1 << e.v)) == 0) cut += e.cap;
                    min = Math.min(min, cut);
                }
            assertEquals(min, d.solve(0, n - 1), "trial=" + trial + ", edges=" + edges);
        }
        var d = new Dinic.DinicFlow(2);
        d.addEdge(0, 1, 2_000_000_000);
        d.addEdge(0, 1, 2_000_000_000);
        assertEquals(4_000_000_000L, d.solve(0, 1));
    }

    static void enumerate(List<Edge> edges, int i, int[] balance, long cost, long[] best) {
        if (i == edges.size()) {
            for (int j = 1; j < balance.length - 1; j++) if (balance[j] != 0) return;
            int flow = -balance[0];
            if (flow >= 0 && flow < best.length && balance[balance.length - 1] == flow)
                best[flow] = Math.min(best[flow], cost);
            return;
        }
        var e = edges.get(i);
        for (int f = 0; f <= e.cap; f++) {
            balance[e.u] -= f;
            balance[e.v] += f;
            enumerate(edges, i + 1, balance, cost + (long) f * e.cost, best);
            balance[e.u] += f;
            balance[e.v] -= f;
        }
    }

    @Test
    void 最小費用流を容量の全列挙と比較() {
        var rng = new Random(293);
        for (int trial = 0; trial < 60; trial++) {
            int n = 4;
            var edges = new ArrayList<Edge>();
            for (int i = 0; i < 6; i++) {
                int u = rng.nextInt(n), v = rng.nextInt(n);
                if (u == v) v = (v + 1) % n;
                edges.add(new Edge(u, v, rng.nextInt(3), rng.nextInt(6)));
            }
            long[] best = new long[13];
            Arrays.fill(best, Long.MAX_VALUE);
            enumerate(edges, 0, new int[n], 0, best);
            for (int limit = 0; limit <= 5; limit++) {
                var f = new MinCostFlowMain.MinCostFlow(n, edges.size());
                for (var e : edges) f.addEdge(e.u, e.v, e.cap, e.cost);
                int flow = limit;
                while (best[flow] == Long.MAX_VALUE) flow--;
                var actual = f.flow(0, n - 1, limit);
                assertEquals(flow, actual.flow(), "trial=" + trial + ", edges=" + edges);
                assertEquals(
                        best[flow],
                        actual.cost(),
                        "trial=" + trial + ", limit=" + limit + ", edges=" + edges);
            }
        }
    }

    @Test
    void 費用曲線の同一傾き統合と流し直し() {
        var f = new MinCostFlowMain.MinCostFlow(4);
        f.addEdge(0, 1, 1, 0);
        f.addEdge(0, 2, 1, 0);
        f.addEdge(1, 2, 1, 0);
        f.addEdge(1, 3, 1, 1);
        f.addEdge(2, 3, 1, 1);
        assertEquals(
                List.of(
                        new MinCostFlowMain.MinCostFlow.CostAndFlow(0, 0),
                        new MinCostFlowMain.MinCostFlow.CostAndFlow(2, 2)),
                f.flowSlope(0, 3));
        var single = new MinCostFlowMain.MinCostFlow(2);
        single.addEdge(0, 1, 3, 2);
        assertEquals(new MinCostFlowMain.MinCostFlow.CostAndFlow(6, 3), single.flow(0, 1));
    }

    @Test
    void 逆辺を通ってマッチングを組み替える() {
        var d = new Dinic.DinicFlow(6);
        int[][] edges = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 3}, {3, 5}, {4, 5}};
        for (int[] e : edges) d.addEdge(e[0], e[1], 1);
        assertEquals(2, d.solve(0, 5));
        assertEquals(0, d.solve(0, 5));
        var f = new MinCostFlowMain.MinCostFlow(6, 8);
        for (int[] e : edges)
            f.addEdge(e[0], e[1], 1, (e[0] == 1 && e[1] == 4) || (e[0] == 2 && e[1] == 3) ? 1 : 0);
        f.addEdge(2, 4, 1, 100);
        assertEquals(new MinCostFlowMain.MinCostFlow.CostAndFlow(2, 2), f.flow(0, 5));
    }
}
