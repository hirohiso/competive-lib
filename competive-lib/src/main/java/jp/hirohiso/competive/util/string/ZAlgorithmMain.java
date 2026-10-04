package jp.hirohiso.competive.util.string;

import java.util.Arrays;

public class ZAlgorithmMain {
    public static void main(String[] args) {
        var s = "aaabaaaab";
        System.out.println(Arrays.toString(zAlgorithm(s)));
    }

    /**
     * 各接尾辞と文字列全体の最長共通接頭辞長を返す。
     * <p>非空文字列が対象。戻り値の長さは N、z[0]=N。UTF-16 の char 単位で比較する。
     * 時間・領域 O(N)。</p>
     */
    static int[] zAlgorithm(String s) {
        var z = new int[s.length()];
        z[0] = s.length();
        var i = 1;
        var j = 0;
        while (i < s.length()) {
            while (i + j < s.length() && s.charAt(j) == s.charAt(i + j)) {
                j++;
            }
            z[i] = j;
            if (j == 0) {
                i++;
                continue;
            }
            var k = 1;
            while (i + k < s.length() && k + z[k] < j) {
                z[i + k] = z[k];
                k++;
            }
            i += k;
            j -= k;
        }
        return z;
    }
}
