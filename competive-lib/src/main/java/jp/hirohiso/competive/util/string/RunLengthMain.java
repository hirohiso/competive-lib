package jp.hirohiso.competive.util.string;

import java.util.ArrayList;
import java.util.LinkedList;

public class RunLengthMain {
    public static void main(String[] args) {
        System.out.println(runLengthEncoding("aaaBBcccc"));
    }
    public static ArrayList<CharIntPair> runLengthEncoding(String str) {
        var result = new ArrayList<CharIntPair>();
        char c = 0;
        int num = 0;
        for (int i = 0; i < str.length(); i++) {
            if (c != str.charAt(i)) {
                if (i > 0) {
                    result.add(new CharIntPair(c, num));
                }
                c = str.charAt(i);
                num = 1;
            } else {
                num++;
            }
        }
        result.add(new CharIntPair(c, num));
        return result;
    }

    record CharIntPair(char a, int b) {
    }
}
