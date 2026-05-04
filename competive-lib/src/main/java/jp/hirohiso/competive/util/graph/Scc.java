package jp.hirohiso.competive.util.graph;

import java.util.*;
import java.util.stream.Collectors;

public class Scc {

    public static void main(String[] args) {
        SccSolver sccSolver = new SccSolver(11,100);
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

        private final int[] rank;
        private int count = 0;

        private int number = 0;
        private int[] componetns;

        private CsrBuilder csrBuilder;

        private Csr csr;
        private Csr reverseCsr;

        private class CsrBuilder {
            int[] from;
            int[] to;
            int idx = 0;
            int n = 0;
            int[] countEdgeFrom;
            int[] countEdgeTo;

            public CsrBuilder(int n, int e) {
                this.from = new int[e];
                this.to = new int[e];
                Arrays.fill(from, -1);
                Arrays.fill(to, -1);
                this.n = n;
                this.countEdgeFrom = new int[n];
                this.countEdgeTo = new int[n];
            }

            public void addEdge(int u, int v) {
                from[idx] = u;
                to[idx] = v;
                idx++;
                countEdgeFrom[u]++;
                countEdgeTo[v]++;
            }

            public Csr[] buildCsr() {
                var elistFrom = new int[idx];
                var elistTo = new int[idx];


                var fromAcc = new int[n + 1];
                var toAcc = new int[n + 1];
                for (int i = 0; i < n; i++) {
                    fromAcc[i + 1] = fromAcc[i] + countEdgeFrom[i];
                }
                for (int i = 0; i < n; i++) {
                    toAcc[i + 1] = toAcc[i] + countEdgeTo[i];
                }
                int[] startFrom = Arrays.copyOf(fromAcc, fromAcc.length);
                int[] startTo = Arrays.copyOf(toAcc, toAcc.length);

                for (int i = 0; i < from.length && from[i] != -1; i++) {
                    var u = from[i];
                    var v = to[i];

                    elistFrom[fromAcc[u]] = v;
                    elistTo[toAcc[v]] = u;
                    fromAcc[u]++;
                    toAcc[v]++;
                }
                var result = new Csr[2];
                result[0] = new Csr(elistFrom, startFrom);
                result[1] = new Csr(elistTo, startTo);
                return result;
            }
        }

        record Csr(int[] elist, int[] start) {
        }

        public SccSolver(int n, int e) {
            size = n;

            rank = new int[n];
            componetns = new int[n];
            csrBuilder = new CsrBuilder(n, e);
        }

        public void solve() {
            var csrs = this.csrBuilder.buildCsr();
            this.csr = csrs[0];
            this.reverseCsr = csrs[1];
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
         *
         * @return
         */
        public ArrayList<LinkedList<Integer>> getDag() {
            var max = Arrays.stream(componetns).max().getAsInt();
            var dag = new ArrayList<LinkedList<Integer>>(max);
            for (int i = 0; i < max; i++) {
                dag.add(new LinkedList<>());
            }
            for (int i = 0; i < componetns.length; i++) {
                for (int j = this.csr.start[i]; j < this.csr.start[i + 1]; j++) {
                    var v = this.csr.elist[j];
                    if (componetns[i] == componetns[v]) {
                        continue;
                    }
                    dag.get(componetns[i] - 1).add(componetns[v] - 1);
                }
            }
            return dag;
        }

        private void dfs(int now, boolean checked[]) {
            checked[now] = true;
            for (int i = this.csr.start[now]; i < this.csr.start[now + 1]; i++) {
                var v = this.csr.elist[i];
                if (checked[v]) {
                    continue;
                }
                dfs(v, checked);
            }
            rank[count++] = now;
        }

        private void rdfs(int now, boolean checked[]) {
            checked[now] = true;
            for (int i = this.reverseCsr.start[now]; i < this.reverseCsr.start[now + 1]; i++) {
                var v = this.reverseCsr.elist[i];
                if (checked[v]) {
                    continue;
                }
                rdfs(v, checked);
            }
            componetns[now] = number;
        }

        /**
         * @param a 0 <= a < size
         * @param b 0 <= b < size
         */
        public void addDirectEdge(int a, int b) {
            this.csrBuilder.addEdge(a, b);
        }
    }

}
