package jp.hirohiso.competive.util.string;

import java.util.Arrays;

public class ManacherMain {

    public static void main(String[] args) {
        var S = "abacaba";
        var m = manacher(S, "#");
        System.out.println(Arrays.toString(m));

        var Seven = "abaccaba";
        var m2 = manacher(Seven, "#");
        System.out.println(Arrays.toString(m2));
    }

    /**
     * 区切り文字を挿入した文字列上の回文半径を返す。
     * <p>S は非空、delimiter は S に含まれない1文字。
     * 変換後の文字列は区切り・文字・区切りの順で長さ 2N+1。
     * 返す半径は中心自身を含む長さで、元文字中心は添字 2i+1、文字間中心は 2i。
     * 時間・領域 O(N)。元文字列の添字とは異なることに注意。</p>
     */
    static int[] manacher(String S, String delimiter) {
        var temp = delimiter + String.join(delimiter, S.split("")) + delimiter;;
        var N = temp.length();
        var i = 0;
        var j = 0;
        var ans = new int[N];
        while (i < N) {
            while (i - j >= 0 && i + j < N && temp.charAt(i - j) == temp.charAt(i + j)) {
                j++;
            }
            ans[i] = j;
            var k = 1;
            while (i - k >= 0 && i + k < N && k + ans[i - k] < j) {
                ans[i + k] = ans[i - k];
                k++;
            }
            i += k;
            j -= k;
        }

        var result = new int[S.length()];
        for (i = 0; i < result.length; i++) {
            result[i] = ans[2 * i];
        }
        return ans;
    }
}
