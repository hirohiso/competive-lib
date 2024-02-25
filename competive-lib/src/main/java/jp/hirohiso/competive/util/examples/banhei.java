package jp.hirohiso.competive.util.examples;

import java.util.Arrays;

public class banhei {

    public static void main(String[] args) {

    }

    /**
     * 周りを番兵で囲むメソッド(int
     *
     * @param target
     * @param fill
     * @return
     */
    private static int[][] wrap(int[][] target, int fill) {
        var result = new int[target.length + 2][target[0].length + 2];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], fill);
            if (i == 0 || i == result.length - 1) {
                continue;
            }
            for (int j = 0; j < result[i].length; j++) {
                if (j == 0 || j == result[i].length - 1) {
                    continue;
                }
                result[i][j] = target[i - 1][j - 1];
            }
        }
        return result;
    }

    private static char[][] wrap(char[][] target, char fill) {
        var result = new char[target.length + 2][target[0].length + 2];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], fill);
            if (i == 0 || i == result.length - 1) {
                continue;
            }
            for (int j = 0; j < result[i].length; j++) {
                if (j == 0 || j == result[i].length - 1) {
                    continue;
                }
                result[i][j] = target[i - 1][j - 1];
            }
        }
        return result;
    }
}
