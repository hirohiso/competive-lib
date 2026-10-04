package jp.hirohiso.competive.util.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class SccTest {
    @Test
    void 相互到達性と縮約グラフ() {
        var rng = new Random(291);
        for (int trial = 0; trial < 100; trial++) {
            int n = 1 + rng.nextInt(9), m = rng.nextInt(30);
            var solver = new Scc.SccSolver(n, m);
            boolean[][] edge = new boolean[n][n], reach = new boolean[n][n];
            for (int i = 0; i < n; i++) reach[i][i] = true;
            for (int i = 0; i < m; i++) {
                int u = rng.nextInt(n), v = rng.nextInt(n);
                edge[u][v] = reach[u][v] = true;
                solver.addDirectEdge(u, v);
            }
            for (int k = 0; k < n; k++)
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++) reach[i][j] |= reach[i][k] && reach[k][j];
            solver.solve();
            var groups = solver.getResultScc();
            int[] comp = new int[n];
            Arrays.fill(comp, -1);
            for (int c = 0; c < groups.size(); c++)
                for (int v : groups.get(c)) {
                    assertEquals(-1, comp[v]);
                    comp[v] = c;
                }
            for (int c : comp) assertTrue(c >= 0);
            var dag = solver.getDag();
            assertEquals(groups.size(), dag.size());
            Set<String> expected = new HashSet<>(), actual = new HashSet<>();
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) {
                    assertEquals(reach[i][j] && reach[j][i], comp[i] == comp[j]);
                    if (edge[i][j] && comp[i] != comp[j]) {
                        assertTrue(comp[i] < comp[j]);
                        expected.add(comp[i] + ":" + comp[j]);
                    }
                }
            for (int i = 0; i < dag.size(); i++) for (int j : dag.get(i)) actual.add(i + ":" + j);
            assertEquals(expected, actual);
        }
    }
}
