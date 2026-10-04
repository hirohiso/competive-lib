package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("stress")
class LargeInputTest {
    @Test
    void 二十万要素の更新と探索() {
        int n = 200_000;
        var tree = new SegmentationTree.SegmentTree<Long>(n, Long::sum, () -> 0L);
        var uf = new UnionFind.DisjointSetUnion(n);
        for (int i = 0; i < n; i++) {
            tree.update(i, 1L);
            uf.mergeSet(0, i);
        }
        assertEquals((long) n, tree.getRange(0, n));
        assertEquals(n, uf.size(n - 1));
        assertEquals(n / 2, tree.maxRight(0, x -> x <= n / 2));
        assertEquals(n / 2, tree.minLeft(n, x -> x <= n / 2));
    }
}
