package jp.hirohiso.competive.util.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DoublingTest {
    @Test
    void 逐次遷移と非可換集約を比較() {
        for (int n : new int[] {1, 2, 3, 8, 9}) {
            var d = new DoublingMain.Doubling<String>(n, String::concat, () -> "");
            for (int i = 0; i < n; i++) d.set(i, (i + 1) % n, "" + (char) ('a' + i));
            d.build();
            for (int i = 0; i < n; i++)
                for (int k = 0; k < n; k++) {
                    String expected = "";
                    for (int j = 0; j < k; j++) expected += (char) ('a' + (i + j) % n);
                    assertEquals(expected, d.range(i, k));
                }
        }
    }

    @Test
    void 未設定の遷移は自分に戻り単位元を集約する() {
        var d = new DoublingMain.Doubling<Long>(5, Long::sum, () -> 0L);
        d.set(3, 3, 2L);
        d.set(0, 0, 10L);
        d.build();
        assertEquals(0L, d.range(1, 7));
        assertEquals(14L, d.range(3, 7));
    }
}
