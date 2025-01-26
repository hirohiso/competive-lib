package jp.hirohiso.competive.util.graph;

import java.util.*;

public class BinaryGraphMain {
    public static void main(String[] args) {

    }

    //black or white

    private Map<Boolean, List<Integer>> check(List<Integer> group, ArrayList<LinkedList<Integer>> list) {
        var map = new HashMap<Integer, Boolean>();
        var queue = new LinkedList<Integer>();
        queue.add(group.get(0));
        map.put(group.get(0), true);
        while (!queue.isEmpty()) {
            var p = queue.pollFirst();
            var b = map.get(p);
            for (var i : list.get(p)) {
                if (map.containsKey(i)) {
                    if (map.get(i) == b) {
                        //訪問済みで今のノードと同じ色
                        return null;
                    }
                } else {
                    map.put(i, !b);
                    queue.addLast(i);
                }
            }
        }
        //白と黒の数を数える
        var black = new LinkedList<Integer>();
        var white = new LinkedList<Integer>();
        for (var entity : map.entrySet()) {
            if (entity.getValue()) {
                black.add(entity.getKey());
            } else {
                white.add(entity.getKey());
            }
        }
        var ret = new HashMap<Boolean, List<Integer>>();
        ret.put(true, black);
        ret.put(false, black);
        return ret;
    }
}
