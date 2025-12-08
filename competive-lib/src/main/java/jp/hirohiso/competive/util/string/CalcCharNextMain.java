package jp.hirohiso.competive.util.string;

import java.util.Arrays;

public class CalcCharNextMain {
    public static void main(String[] args) {
        var test = "nyanpasu";
        var d =calcCharNext(test.toCharArray());
    }

    private static int[][] calcCharNext(char[] str) {
        var CHAR_SIZE = 26;
        var BASE = 'a';
        var ret = new int[str.length + 1][CHAR_SIZE];
        Arrays.fill(ret[str.length], str.length);
        for (int i = str.length - 1; i >= 0; i--) {
            for (int j = 0; j < CHAR_SIZE; j++) {
                ret[i][j] = ret[i + 1][j];
            }
            ret[i][str[i] - BASE] = i;
        }

        return ret;
    }
}
