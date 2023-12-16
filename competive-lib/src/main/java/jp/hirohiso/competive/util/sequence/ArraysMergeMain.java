package jp.hirohiso.competive.util.sequence;

import java.util.Arrays;
import java.util.function.BinaryOperator;

public class ArraysMergeMain {
    public static void main(String[] args) {
        var arr1 = new int[]{1, 2, 3, 4, 5};
        var arr2 = new int[]{1, 1, 1, 1};
        System.out.println(Arrays.toString(merge(arr1, arr2, 0, Integer::sum)));
        System.out.println(Arrays.toString(merge(arr1, arr2, 3, Integer::sum)));

        var grid1 = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        var grid2 = new int[][]{
                {1, 1},
                {1, 1}
        };

        {
            var m1 = merge(grid1, grid2, 0, 0, Integer::sum);
            for (int i = 0; i < m1.length; i++) {
                System.out.println(Arrays.toString(m1[i]));
            }
        }
        {
            var m1 = merge(grid1, grid2, 2, 2, Integer::sum);
            for (int i = 0; i < m1.length; i++) {
                System.out.println(Arrays.toString(m1[i]));
            }
        }
    }

    /**
     * baseのstart位置からlayerをopで作用させつつマージした結果を返す。
     *
     * @param base
     * @param layer
     * @param start
     * @param op
     * @return
     */
    private static int[] merge(int[] base, int[] layer, int start, BinaryOperator<Integer> op) {
        var result = new int[Math.max(start + layer.length, base.length)];
        for (int i = 0; i < base.length; i++) {
            result[i] = base[i];
        }
        for (int i = 0; i < layer.length; i++) {
            result[start + i] = op.apply(result[start + i], layer[i]);
        }
        return result;
    }

    private static int[][] merge(int[][] base, int[][] layer, int x, int y, BinaryOperator<Integer> op) {
        var bh = base.length;
        var bw = base[0].length;
        var lh = layer.length;
        var lw = layer[0].length;

        var rh = Math.max(x + lh, bh);
        var rw = Math.max(y + lw, bw);
        var result = new int[rh][rw];

        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                result[i][j] = base[i][j];
            }
        }
        for (int i = 0; i < lh; i++) {
            for (int j = 0; j < lw; j++) {
                result[x + i][y + j] = op.apply(result[x + i][y + j], layer[i][j]);
            }
        }
        return result;
    }
}
