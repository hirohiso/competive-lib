package jp.hirohiso.competive.util.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FlowEdgeTest {
    @Test
    void 元の辺順で容量費用と流量を返す() {
        var f = new MinCostFlowMain.MinCostFlow(3, 4);
        assertEquals(0, f.addEdge(1, 2, 3, 5));
        assertEquals(1, f.addEdge(0, 1, 2, 4));
        assertEquals(2, f.addEdge(0, 2, 1, 20));
        assertEquals(new MinCostFlowMain.MinCostFlow.CostAndFlow(38, 3), f.flow(0, 2));
        assertEquals(new MinCostFlowMain.MinCostFlow.Edge(1, 2, 3, 2, 5), f.getEdge(0));
        assertEquals(new MinCostFlowMain.MinCostFlow.Edge(0, 1, 2, 2, 4), f.getEdge(1));
        assertEquals(new MinCostFlowMain.MinCostFlow.Edge(0, 2, 1, 1, 20), f.getEdge(2));
    }

    @Test
    void 平行辺による多数の距離更新でも最小費用を選ぶ() {
        var f = new MinCostFlowMain.MinCostFlow(3, 51);
        for (int cost = 50; cost >= 1; cost--) f.addEdge(0, 1, 1, cost);
        f.addEdge(1, 2, 50, 0);
        assertEquals(new MinCostFlowMain.MinCostFlow.CostAndFlow(1275, 50), f.flow(0, 2));
        for (int i = 0; i < 50; i++) assertEquals(1, f.getEdge(i).flow());
    }

    @Test
    void 多数の異なる頂点から安い経路を選ぶ() {
        var rng = new java.util.Random(645);
        for (int trial = 0; trial < 15; trial++) {
            int n = 20;
            int[] costs = new int[n];
            for (int i = 0; i < n; i++) costs[i] = rng.nextInt(100);
            int[] sorted = costs.clone();
            java.util.Arrays.sort(sorted);
            for (int limit = 1; limit <= n; limit++) {
                var f = new MinCostFlowMain.MinCostFlow(n + 2, 2 * n);
                for (int i = 0; i < n; i++) {
                    f.addEdge(0, i + 1, 1, costs[i]);
                    f.addEdge(i + 1, n + 1, 1, 0);
                }
                long expected = 0;
                for (int i = 0; i < limit; i++) expected += sorted[i];
                assertEquals(
                        new MinCostFlowMain.MinCostFlow.CostAndFlow(expected, limit),
                        f.flow(0, n + 1, limit),
                        "trial="
                                + trial
                                + ", limit="
                                + limit
                                + ", costs="
                                + java.util.Arrays.toString(costs));
            }
        }
    }
}
