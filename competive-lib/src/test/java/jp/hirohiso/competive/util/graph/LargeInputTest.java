package jp.hirohiso.competive.util.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("stress")
class LargeInputTest {
    @Test
    void 十万頂点の疎グラフ() {
        int n = 100_000;
        var graph = new DijkstraLight.Dijkstra(n, n - 1);
        for (int i = 1; i < n; i++) graph.addEdge(i - 1, i, 1_000_000_000L);
        graph.solve(0);
        assertEquals((n - 1) * 1_000_000_000L, graph.getDistance()[n - 1]);
        for (int i = 1; i < n; i++) assertEquals(i - 1, graph.getParent()[i]);
    }
}
