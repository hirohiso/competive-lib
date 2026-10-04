package jp.hirohiso.competive.util.string;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

class RollingHashTest {
    @Test
    void 全窓のハッシュを独立した多項式計算と比較() {
        var hash = new RollingHashMain.RollingHash();
        for (String s : StringAlgorithmsTest.samples())
            for (int len = 1; len <= s.length(); len++) {
                var it = hash.iterator(s, len).iterator();
                for (int l = 0; l + len <= s.length(); l++) {
                    assertTrue(it.hasNext());
                    long expected = 0;
                    for (int i = l; i < l + len; i++)
                        expected = (expected * 3491 + s.charAt(i) - 'a') % 999999937;
                    assertEquals(expected, hash.get(s.substring(l, l + len)));
                    assertEquals(expected, it.next(), s + ", l=" + l + ", len=" + len);
                }
                assertFalse(it.hasNext());
            }
    }

    static RollingHashMain.ShiftHash of(String s) {
        var h = RollingHashMain.ShiftHash.E();
        for (char c : s.toCharArray()) h = h.applyR(RollingHashMain.ShiftHash.of(c));
        return h;
    }

    @Test
    void 連結と左右の除去と乗算() {
        for (String s : StringAlgorithmsTest.samples())
            for (int i = 0; i <= s.length(); i++) {
                var a = of(s.substring(0, i));
                var b = of(s.substring(i));
                var whole = of(s);
                assertEquals(whole, a.applyR(b));
                assertEquals(whole, b.applyL(a));
                assertEquals(a, whole.rightInv(b));
                assertEquals(b, whole.leftInv(a));
            }
        var rng = new Random(442);
        long m = RollingHashMain.ShiftHash.m;
        for (int i = 0; i < 100; i++) {
            long a = rng.nextLong(m), b = rng.nextLong(m);
            long expected =
                    BigInteger.valueOf(a)
                            .multiply(BigInteger.valueOf(b))
                            .mod(BigInteger.valueOf(m))
                            .longValue();
            assertEquals(expected, RollingHashMain.ShiftHash.mul(a, b, m));
        }
    }

    @Test
    void 長さ付きハッシュも独立した多項式と一致する() {
        for (String text : StringAlgorithmsTest.samples()) {
            long expected = 0;
            for (char c : text.toCharArray()) expected = (expected * 37 + c) % 999_999_937;
            assertEquals(expected, of(text).v());
            assertEquals(text.length(), of(text).size());
        }
    }
}
