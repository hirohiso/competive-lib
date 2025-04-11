package jp.hirohiso.competive.util.graph;

import java.util.*;
import java.util.stream.Collectors;

public class Scc {

    public static void main(String[] args) {
        SccSolver sccSolver = new SccSolver(11);
        sccSolver.addDirectEdge(0, 1);
        sccSolver.addDirectEdge(1, 2);
        sccSolver.addDirectEdge(1, 3);
        sccSolver.addDirectEdge(2, 0);
        sccSolver.addDirectEdge(2, 4);
        sccSolver.addDirectEdge(3, 3);
        sccSolver.addDirectEdge(3, 5);
        sccSolver.addDirectEdge(4, 5);
        sccSolver.addDirectEdge(5, 7);
        sccSolver.addDirectEdge(7, 6);
        sccSolver.addDirectEdge(6, 5);
        sccSolver.addDirectEdge(6, 8);
        sccSolver.addDirectEdge(8, 6);
        sccSolver.addDirectEdge(9, 10);
        sccSolver.addDirectEdge(10, 9);
        sccSolver.solve();
        var scc = sccSolver.getResultScc();
        System.out.println(scc);

        var dag = sccSolver.getDag();
        System.out.println(dag);
    }


    public static class SccSolver {
        private final int size;
        private final List<Integer>[] edges;
        private final List<Integer>[] reverseEdge;

        private final int[] rank;
        private int count = 0;

        private int number = 0;
        private int[] componetns;

        public SccSolver(int n) {
            size = n;
            edges = new List[n];
            reverseEdge = new List[n];
            for (int i = 0; i < n; i++) {
                edges[i] = new LinkedList<>();
                reverseEdge[i] = new LinkedList<>();
            }
            rank = new int[n];
            componetns = new int[n];
        }

        public void solve() {
            boolean[] checked = new boolean[size];
            for (int i = 0; i < checked.length; i++) {
                if (checked[i]) {
                    continue;
                }
                dfs(i, checked);
            }

            //2回目
            boolean[] checked2 = new boolean[size];

            for (int i = rank.length - 1; i >= 0; i--) {
                int k = rank[i];
                if (checked2[k]) {
                    continue;
                }
                number++;
                rdfs(k, checked2);
            }
        }

        public List<List<Integer>> getResultScc() {
            //トポロジカルソート
            var max = Arrays.stream(componetns).max().getAsInt();
            var scc = new ArrayList<List<Integer>>(max);
            for (int i = 0; i < max; i++) {
                scc.add(new LinkedList<>());
            }
            for (int i = 0; i < componetns.length; i++) {
                scc.get(componetns[i] - 1).add(i);
            }
            return scc;
        }

        /**
         * 強連結成分をDAGに変換する
         * @return
         */
        public ArrayList<LinkedList<Integer>> getDag(){
            var max = Arrays.stream(componetns).max().getAsInt();
            var dag = new ArrayList<LinkedList<Integer>>(max);
            for (int i = 0; i < max; i++) {
                dag.add(new LinkedList<>());
            }
            for (int i = 0; i < componetns.length; i++) {
                for (int j : edges[i]) {
                    if (componetns[i] == componetns[j]) {
                        continue;
                    }
                    dag.get(componetns[i] - 1).add(componetns[j] - 1);
                }
            }
            return dag;
        }

        private void dfs(int now, boolean checked[]) {
            checked[now] = true;
            for (int i : edges[now]) {
                if (checked[i]) {
                    continue;
                }
                dfs(i, checked);
            }
            rank[count++] = now;
        }

        private void rdfs(int now, boolean checked[]) {
            checked[now] = true;
            for (int i : reverseEdge[now]) {
                if (checked[i]) {
                    continue;
                }
                rdfs(i, checked);
            }
            componetns[now] = number;
        }

        /**
         * @param a 0 <= a < size
         * @param b 0 <= b < size
         */
        public void addDirectEdge(int a, int b) {
            edges[a].add(b);
            reverseEdge[b].add(a);
        }

    }

}
