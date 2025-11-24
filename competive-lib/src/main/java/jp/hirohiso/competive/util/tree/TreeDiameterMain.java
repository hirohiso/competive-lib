package jp.hirohiso.competive.util.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class TreeDiameterMain {
    public static void main(String[] args) {
        var tree = new TreeDiameter(10);
        tree.edge(6, 5, 1);
        tree.edge(2, 1, 1);
        tree.edge(1, 3, 1);
        tree.edge(3, 4, 1);

        tree.edge(7, 8, 1);
        tree.edge(0, 7, 1);
        tree.edge(0, 5, 1);
        tree.edge(0, 1, 1);
        tree.edge(8, 9, 1);
        System.out.println(tree.diameter());
    }

    private static class TreeDiameter {
        ArrayList<LinkedList<InnerPair>> list;
        boolean[] visited;//内部利用　
        int[] parent;//内部利用　

        public TreeDiameter(int n) {
            this.list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(new LinkedList<>());
            }
            this.visited = new boolean[n];
            this.parent = new int[n];
            Arrays.fill(parent, -1);
        }

        public void edge(int n1, int n2, long d) {
            this.list.get(n1).add(new InnerPair(n2, d));
            this.list.get(n2).add(new InnerPair(n1, d));
        }

        private InnerPair dfs(int n, long l) {
            visited[n] = true;
            var p = new InnerPair(n, l);
            var nextList = this.list.get(n);
            for (var next : nextList) {
                if (visited[next.n]) {
                    continue;
                }
                visited[next.n] = true;
                parent[next.n] = n;
                var q = dfs(next.n, next.d + l);
                if (p.d < q.d) {
                    p = q;
                }
            }
            return p;
        }

        public ResultTuple diameter() {
            this.visited = new boolean[visited.length];
            var p = dfs(0, 0);
            this.visited = new boolean[visited.length];
            var q = dfs(p.n, 0);

            var dc = q.d / 2;
            var center = q.n;
            while (dc > 0) {
                center = this.parent[center];
                dc--;
            }

            return new ResultTuple(p.n, q.n, q.d, center);
        }

        record InnerPair(int n, long d) {
        }

        record ResultTuple(int n1, int n2, long d, int center) {
        }
    }

}
