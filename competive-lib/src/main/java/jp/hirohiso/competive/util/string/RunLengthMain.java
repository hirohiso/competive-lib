package jp.hirohiso.competive.util.string;

import java.util.LinkedList;

public class RunLengthMain {
    public static void main(String[] args) {
        System.out.println(runLengthEncoding("aaaBBcccc"));
    }
    public static LinkedList<TPair<Character, Integer>> runLengthEncoding(String str) {
        var result = new LinkedList<TPair<Character, Integer>>();
        char c = 0;
        int num = 0;
        for (int i = 0; i < str.length(); i++) {
            if (c != str.charAt(i)) {
                if (i > 0) {
                    result.addLast(new TPair<>(c, num));
                }
                c = str.charAt(i);
                num = 1;
            } else {
                num++;
            }
        }
        result.addLast(new TPair<>(c, num));
        return result;
    }

    record TPair<S, T>(S a, T b) {
    }
}
