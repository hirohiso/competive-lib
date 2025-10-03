package jp.hirohiso.competive.util.graph;

import java.util.*;
import java.util.stream.IntStream;

public class ManhattanMstMain {
    public static void main(String[] args) {

    }


    record ManhattanEdge(int u, int v, long w) {
    }

    record ManhattanPoint(int x, int y) {
    }

    //https://github.com/hitonanode/cplib-cpp/blob/master/graph/manhattan_mst.hpp
    static class ManhattanMst {
        //マンハッタン最小全域木の候補となる辺を全て求める
        public static List<ManhattanEdge> solve(ManhattanPoint[] points) {
            var n = points.length;
            var edges = new ArrayList<ManhattanEdge>();
            var idx = new Integer[n];
            Arrays.setAll(idx, i -> i);

            var xs = new long[n];
            var ys = new long[n];
            for (int i = 0; i < n; i++) {
                xs[i] = points[i].x();
                ys[i] = points[i].y();
            }

            for (int s = 0; s < 2; s++) {
                for (int t = 0; t < 2; t++) {
                    // x+y でソート
                    var finalXs = xs;
                    var finalYs = ys;
                    Arrays.sort(idx, Comparator.comparingLong(i -> finalXs[i] + finalYs[i]));

                    var sweep = new TreeMap<Long, Integer>();
                    for (int i : idx) {
                        var it = sweep.ceilingEntry(-ys[i]);
                        while (it != null) {
                            var j = it.getValue();
                            if (xs[i] - xs[j] < ys[i] - ys[j]) {
                                break;
                            }
                            var w = Math.abs(xs[i] - xs[j]) + Math.abs(ys[i] - ys[j]);
                            edges.add(new ManhattanEdge(i, j, w));
                            sweep.remove(it.getKey());
                            it = sweep.ceilingEntry(-ys[i]);
                        }
                        sweep.put(-ys[i], i);
                    }

                    // swap xs, ys
                    var tmp = xs;
                    xs = ys;
                    ys = tmp;
                }
                // negate xs
                for (int i = 0; i < n; i++) {
                    xs[i] = -xs[i];
                }
            }
            // 辺を重み順にソート
            edges.sort(Comparator.comparingLong(ManhattanEdge::w));
            return edges;
        }
    }
}
