package jp.hirohiso.competive.util.graph;

import java.util.*;
import java.util.stream.Collectors;

public class Scc {

    public static void main(String[] args) {
        SccSolver sccSolver = new SccSolver(5);
        sccSolver.addDirectEdge(0, 1);
        sccSolver.addDirectEdge(1, 0);
        sccSolver.addDirectEdge(1, 2);
        sccSolver.addDirectEdge(2, 4);
        sccSolver.addDirectEdge(4, 3);
        sccSolver.addDirectEdge(3, 2);
        sccSolver.solve();
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
            System.out.println(Arrays.toString(componetns));
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
