package jp.hirohiso.competive.util.range;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class PriorityRangeSumTest {
    @Test
    void K変更と重複削除をソートと比較() {
        var rng = new Random(342);
        var a = new ArrayList<Long>();
        int k = 0;
        var p = new PriorityRangeSumMain.PriorityRangeSum(k);
        for (int step = 0; step < 600; step++) {
            if (step % 7 == 0) {
                k = rng.nextInt(12);
                p.updateK(k);
            } else if (a.isEmpty() || rng.nextBoolean()) {
                long x = (rng.nextInt(9) - 4) * 1_000_000_000L;
                a.add(x);
                p.push(x);
            } else {
                int i = rng.nextInt(a.size());
                p.pop(a.remove(i));
            }
            a.sort(null);
            assertEquals(
                    a.stream().limit(k).mapToLong(Long::longValue).sum(),
                    p.sum(),
                    "step=" + step + ", k=" + k + ", a=" + a);
            assertEquals(a.size(), p.size());
        }
        for (long x : a) p.pop(x);
        assertEquals(0, p.sum());
        assertEquals(0, p.size());
    }

    @Test
    void ジェネリクス版の遅延削除は値で比較する() {
        var rng = new Random(343);
        var a = new ArrayList<Long>();
        int k = 3;
        var p =
                new PriorityRangeSumMain.PriorityRangeSumT<Long>(
                        k, Comparator.naturalOrder(), Long::sum, (x, y) -> x - y, 0L);
        for (int step = 0; step < 200; step++) {
            if (a.size() < k || rng.nextBoolean()) {
                long x = 1000 + rng.nextInt(6);
                a.add(x);
                p.push(x);
            } else {
                int i = rng.nextInt(a.size());
                long value = a.remove(i);
                p.pop(Long.valueOf(value));
            }
            a.sort(null);
            assertEquals(
                    a.stream().limit(k).mapToLong(Long::longValue).sum(),
                    p.sum(),
                    "step=" + step + ", a=" + a);
            assertEquals(Math.min(k, a.size()), p.size());
        }
    }
}
