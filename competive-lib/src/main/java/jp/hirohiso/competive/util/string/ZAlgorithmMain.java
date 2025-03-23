package jp.hirohiso.competive.util.string;

import java.util.Arrays;

public class ZAlgorithmMain {
    public static void main(String[] args) {
        var s = "aaabaaaab";
        System.out.println(Arrays.toString(zAlgorithm(s)));
    }

    private static int[] zAlgorithm(String s) {
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
