package jp.hirohiso.competive.util.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class MatrixTest {
    static long[][] product(long[][] a, long[][] b, long mod) {
        long[][] c = new long[a.length][b[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b[0].length; j++)
                for (int k = 0; k < b.length; k++) c[i][j] = (c[i][j] + a[i][k] * b[k][j]) % mod;
        return c;
    }

    @Test
    void 行列累乗を逐次乗算と比較() {
        var rng = new Random(642);
        for (int n = 1; n <= 4; n++)
            for (int trial = 0; trial < 20; trial++) {
                long[][] a = new long[n][n], expected = new long[n][n];
                for (int i = 0; i < n; i++) {
                    expected[i][i] = 1;
                    for (int j = 0; j < n; j++) a[i][j] = rng.nextInt(10);
                }
                for (int power = 0; power <= 12; power++) {
                    assertTrue(
                            Arrays.deepEquals(expected, MatrixLib.matrixPow(a, power, 101)),
                            "n=" + n + ", power=" + power);
                    expected = product(expected, a, 101);
                }
            }
    }

    @Test
    void 長方形行列と加算と要素変換() {
        var a =
                new MatrixLib.Matrix.Builder(2, 3)
                        .setRowValue(0, new long[] {1, 2, 3})
                        .setRowValue(1, new long[] {4, 5, 6})
                        .build();
        var b = new MatrixLib.Matrix(new long[][] {{1, 2}, {3, 4}, {5, 6}});
        // 数値を読み出すAPIがないため、公開の文字列表現から演算結果を確認する。
        assertEquals("Matrix{values=[22, 28][49, 64]}", a.multiply(b).toString());
        assertEquals("Matrix{values=[2, 4, 6][8, 10, 12]}", a.add(a).toString());
        assertEquals("Matrix{values=[-1, -2, -3][-4, -5, -6]}", a.apply(x -> -x).toString());
    }

    @Test
    void Builderの一点設定はチェーン可能() {
        var b = new MatrixLib.Matrix.Builder(1, 1);
        assertSame(b, b.setValue(0, 0, 42));
        assertEquals("Matrix{values=[42]}", b.build().toString());
    }
}
