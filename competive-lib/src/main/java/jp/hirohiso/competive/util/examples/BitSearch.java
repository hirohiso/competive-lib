package jp.hirohiso.competive.util.examples;

public class BitSearch {
    public static void main(String[] args) {
        var bit = 0;
        var M = 10;
        while (bit < (1 << M)) {
            for (int i = 0; i < M; i++) {
                if (((bit >> i) & 1) == 1) {
                    //bitが立っている時
                } else {
                    //bitが立っていない時
                }
            }
        }
    }
}
