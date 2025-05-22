package jp.hirohiso.competive.util.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class NamoriGraphMain {

    public static class NamoriGraph{
        private final int n;
        private final ArrayList<LinkedList<Integer>> graph;

        public int[] edges;
        public final ArrayList<HashSet<Integer>> trees;

        public NamoriGraph(int n) {
            this.n = n;
            this.graph = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                graph.add(new LinkedList<>());
            }
            this.edges = new int[n];
            this.trees = new ArrayList<>();
        }

        public void addEdge(int s, int e) {
            graph.get(s).add(e);
            graph.get(e).add(s);
            edges[s]++;
            edges[e]++;
        }

        public List<Integer> getAdjacent(int s) {
            return graph.get(s);
        }
        public void solve() {
            //DFS
            {
                var q = new LinkedList<Integer>();
                for (var i = 0; i < n; i++) {
                    if (edges[i] == 1) {
                        q.add(i);
                    }
                }
                while (!q.isEmpty()) {
                    var s = q.poll();
                    for (var e : graph.get(s)) {
                        edges[e]--;
                        if (edges[e] == 1) {
                            q.add(e);
                        }
                    }
                }
            }
            //サイクルに含まれる頂点集合を求める
            var hashset = new HashSet<Integer>();
            for (int i = 0; i < n; i++) {
                //２つの辺が出ている頂点はサイクルに含まれる
                if (edges[i] == 2) {
                    hashset.add(i);
                }
            }
            //サイクルから出る頂点集合を求める
            for(var n : hashset){
                var tree = new HashSet<Integer>();
                tree.add(n);
                var q = new LinkedList<Integer>();
                q.add(n);
                while(!q.isEmpty()){
                    var s = q.poll();
                    for (var e : graph.get(s)) {
                        if (!hashset.contains(e) && !tree.contains(e)) {
                            tree.add(e);
                            q.add(e);
                        }
                    }
                }
                trees.add(tree);
            }
        }
    }

}
