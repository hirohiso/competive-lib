package jp.hirohiso.competive.util.examples;

import java.util.HashMap;

public class SwitchLRUD {
    public static void main(String[] args) {
        var map = new HashMap<Character, Pair>();
        map.put('L', new Pair(0, -1));
        map.put('R', new Pair(0, 1));
        map.put('U', new Pair(-1, 0));
        map.put('D', new Pair(1, 0));
    }
    record Pair(long a, long b) {
    }
}
