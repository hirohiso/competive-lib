package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class UnionFindTest {
    @Test
    void 連結性とサイズと集約値とポテンシャル() {
        int n = 20;
        var rng = new Random(91);
        int[] group = new int[n];
        Long[] weight = new Long[n];
        for (int i = 0; i < n; i++) {
            group[i] = i;
            weight[i] = (long) i * i - 100;
        }
        var uf = new UnionFind.DisjointSetUnion(n);
        var weighted = new UnionFind.WeightedDisjointSetUnion<Long>(n, weight, Long::sum);
        var potential = new UnionFind.PotentialDisjointSetUnion<Long>(n, Long::sum, x -> -x, 0L);
        for (int step = 0; step < 100; step++) {
            int a = rng.nextInt(n), b = rng.nextInt(n), old = group[b], next = group[a];
            uf.mergeSet(a, b);
            weighted.mergeSet(a, b);
            potential.mergeSet(a, b, weight[b] - weight[a]);
            for (int i = 0; i < n; i++) if (group[i] == old) group[i] = next;
            for (int i = 0; i < n; i++) {
                int size = 0;
                long sum = 0;
                for (int j = 0; j < n; j++) {
                    boolean same = group[i] == group[j];
                    assertEquals(same, uf.isSameSet(i, j));
                    assertEquals(same, weighted.isSameSet(i, j));
                    assertEquals(same, potential.isSameSet(i, j));
                    if (same) {
                        size++;
                        sum += weight[j];
                        assertEquals(weight[j] - weight[i], potential.diff(i, j));
                    }
                }
                assertEquals(size, uf.size(i));
                assertEquals(size, weighted.size(i));
                assertEquals(size, potential.size(i));
                assertEquals(sum, weighted.value(i));
            }
            Set<Set<Integer>> expected = new HashSet<>();
            for (int i = 0; i < n; i++) {
                Set<Integer> s = new HashSet<>();
                for (int j = 0; j < n; j++) if (group[i] == group[j]) s.add(j);
                expected.add(s);
            }
            for (var groups : List.of(uf.groups(), weighted.groups(), potential.groups())) {
                Set<Set<Integer>> actual = new HashSet<>();
                groups.forEach(g -> actual.add(new HashSet<>(g)));
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void 併合の戻り値は併合後の代表() {
        var uf = new UnionFind.DisjointSetUnion(5);
        var weighted =
                new UnionFind.WeightedDisjointSetUnion<Long>(
                        5, new Long[] {0L, 1L, 2L, 3L, 4L}, Long::sum);
        var potential = new UnionFind.PotentialDisjointSetUnion<Long>(5, Long::sum, x -> -x, 0L);
        for (int[] edge : new int[][] {{3, 4}, {3, 4}, {2, 3}, {1, 2}}) {
            int a = edge[0], b = edge[1];
            assertEquals(uf.mergeSet(a, b), uf.root(b));
            assertEquals(weighted.mergeSet(a, b), weighted.root(b));
            assertEquals(potential.mergeSet(a, b, (long) b - a), potential.root(b));
        }
    }
}
