package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class LcaTest {
    @Test
    void 全頂点対を親の逐次探索と比較() {
        var rng = new Random(814);
        for (int n : new int[] {1, 2, 4, 8, 17, 35})
            for (int mode = 0; mode < 3; mode++) {
                var builder = new DoblingTreeMain().new TreeBuilder(n);
                int[] parent = new int[n], depth = new int[n];
                parent[0] = -1;
                for (int i = 1; i < n; i++) {
                    parent[i] = mode == 0 ? i - 1 : mode == 1 ? 0 : rng.nextInt(i);
                    depth[i] = depth[parent[i]] + 1;
                    builder.addEdge(i, parent[i]);
                }
                var tree = builder.rootTree(0);
                for (int u = 0; u < n; u++) {
                    int a = u;
                    for (int k = 0; k <= depth[u] + 1; k++) {
                        assertEquals(a, tree.ancestor(u, k));
                        if (a >= 0) a = parent[a];
                    }
                    for (int v = 0; v < n; v++) {
                        int x = u, y = v;
                        while (depth[x] > depth[y]) x = parent[x];
                        while (depth[y] > depth[x]) y = parent[y];
                        while (x != y) {
                            x = parent[x];
                            y = parent[y];
                        }
                        assertEquals(x, tree.lca(u, v), "n=" + n + ", u=" + u + ", v=" + v);
                    }
                }
            }
    }
}
