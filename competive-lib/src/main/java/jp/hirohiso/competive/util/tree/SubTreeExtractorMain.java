package jp.hirohiso.competive.util.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.IntPredicate;

public class SubTreeExtractorMain {
    public static void main(String[] args) {

    }


    //葉を共有し、葉以外を共有しないいくつかの部分木集合を抽出する
    //葉となる条件は全ての部分木において共通であり、
    //部分木の境界は葉のみであることが前提
    public static class SubTreeExtractor {
        private final ArrayList<LinkedList<Integer>> lists;
        private final IntPredicate isVertex;
        private final IntPredicate isLeaf;

        public SubTreeExtractor(ArrayList<LinkedList<Integer>> lists, IntPredicate isVertex, IntPredicate isLeaf) {
            this.lists = lists;
            this.isVertex = isVertex;
            this.isLeaf = isLeaf;
        }

        //木を分割してfuncに渡す
        //リーフは互いの木で共有する可能性があるが、その他の頂点は共有されない
        private LinkedList<HashSet<Integer>> extractTrees() {
            var n = lists.size();
            var used = new boolean[n];

            var ret = new LinkedList<HashSet<Integer>>();
            LOOP:
            for (int i = 0; i < n; i++) {
                if (!isVertex.test(i) || used[i]) {
                    continue LOOP;
                }

                if (isLeaf.test(i)) {
                    //いくつか木の端点として共有される可能性がある
                    // 隣接点に頂点があった場合はスキップ(別な機会に探索されるため
                    //それ以外の場合は葉のみの木として続行
                    for (var v : lists.get(i)) {
                        if (isVertex.test(v)) {
                            continue LOOP;
                        }
                    }
                }
                //探索開始
                var set = extract(i, used);
                ret.addLast(set);
            }
            return ret;
        }

        record BfsPair(int u, int dis) {
        }


        private HashSet<Integer> extract(int root, boolean[] used) {
            var q = new LinkedList<BfsPair>();
            q.addLast(new BfsPair(root, -1));
            var ret = new HashSet<Integer>();
            while (!q.isEmpty()) {
                var bfsPair = q.pollFirst();
                ret.add(bfsPair.u);

                if (isLeaf.test(bfsPair.u)) {
                    //葉ノードの場合は探索終了
                    continue;
                }
                used[bfsPair.u] = true;
                for (var v : lists.get(bfsPair.u)) {
                    if (bfsPair.dis == v) {
                        continue;
                    }
                    if (!isVertex.test(v)) {
                        continue;
                    }
                    q.addLast(new BfsPair(v, bfsPair.u));
                }
            }
            return ret;
        }

    }
}
