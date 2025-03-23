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

    private static int[] manacher(String S, String delimiter) {
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
