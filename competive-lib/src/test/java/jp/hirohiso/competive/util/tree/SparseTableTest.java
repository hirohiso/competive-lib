package jp.hirohiso.competive.util.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class SparseTableTest {
    @Test
    void 全区間の最小値() {
        var rng = new Random(651);
        for (int n : new int[] {1, 2, 3, 7, 8, 9, 32}) {
            Integer[] a = new Integer[n];
            Arrays.setAll(a, i -> rng.nextInt(200) - 100);
            var table = new SparseTableMain.SparseTable<Integer>(a, Math::min);
            for (int l = 0; l < n; l++) {
                int min = Integer.MAX_VALUE;
                for (int r = l + 1; r <= n; r++) {
                    min = Math.min(min, a[r - 1]);
                    assertEquals(min, table.query(l, r));
                }
            }
        }
    }
}
