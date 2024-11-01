package jp.hirohiso.competive.util.tree;

import java.util.ArrayList;
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
        boolean[] visited;

        public TreeDiameter(int n) {
            this.list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(new LinkedList<>());
            }
            this.visited = new boolean[n];
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
            return new ResultTuple(p.n, q.n, q.d);
        }

        record InnerPair(int n, long d) {
        }

        record ResultTuple(int n1, int n2, long d) {
        }
    }
}
