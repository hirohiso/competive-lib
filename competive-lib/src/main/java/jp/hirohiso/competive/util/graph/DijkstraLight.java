package jp.hirohiso.competive.util.graph;

import java.util.*;

public class DijkstraLight {

    public static void main(String[] args) {
        Dijkstra graph = new Dijkstra(4,7);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 8);
        graph.solve(0);
        long[] distance = graph.getDistance();
        for (int i = 0; i < distance.length; i++) {
            System.out.println(i + ":" + distance[i]);
        }

    }

    //有向グラフ版お手軽ダイクストラ
    //O(E + V log V)
    public static class Dijkstra {
        private int size = 0;
        private long[] disitance;
        //隣接リスト
        private int[] parent;
        private CsrBuilder csrBuilder;

        public Dijkstra(int size, int esize) {
            this.size = size;
            this.disitance = new long[size];
            this.parent = new int[size];
            for (int i = 0; i < size; i++) {
                this.disitance[i] = Long.MAX_VALUE;
            }
            for (int i = 0; i < size; i++) {
                this.parent[i] = -1;
            }
            this.csrBuilder = new CsrBuilder(size, esize);
        }

        public long[] getDistance() {
            return this.disitance;
        }

        public int[] getParent() {
            return this.parent;
        }

        public void solve(int root) {
            var csr = csrBuilder.buildCsr();
            updateDistance(root, 0);
            Comparator<DistansNodeSet> comp = (DistansNodeSet e1, DistansNodeSet e2) -> e1.distans < e2.distans ? -1
                    : e1.distans > e2.distans ? 1 : 0;
            PriorityQueue<DistansNodeSet> pq = new PriorityQueue<>(comp);
            pq.add(DistansNodeSet.of(0, root));
            while (!pq.isEmpty()) {
                DistansNodeSet pair = pq.poll();
                int n = pair.getNode();

                if (this.disitance[n] < pair.getDistans()) {
                    continue;
                }
                for (int i = csr.start[n]; i <csr.start[n + 1]; i++) {
                    int node = csr.elist[i];
                    long cost = csr.cost[i];
                    long newCost = this.disitance[n] + cost;

                    if (this.disitance[node] > newCost) {
                        updateDistance(node, newCost);
                        pq.add(DistansNodeSet.of(newCost, node));
                        parent[node] = n;
                    }
                }
            }
        }

        public void addEdge(int node1, int node2, long cost) {
            csrBuilder.addEdge(node1, node2, cost);
        }

        private void updateDistance(int node, long cost) {
            this.disitance[node] = cost;
        }

        private static class DistansNodeSet {
            private long distans;
            private int node;

            private DistansNodeSet(long d, int n) {
                distans = d;
                node = n;
            }

            public static DistansNodeSet of(long d, int n) {
                return new DistansNodeSet(d, n);
            }

            public long getDistans() {
                return distans;
            }

            public int getNode() {
                return node;
            }
        }

        private class CsrBuilder {
            int[] from;
            int[] to;
            long[] cost;
            int idx = 0;
            int n = 0;
            int[] countEdgeFrom;

            public CsrBuilder(int n, int e) {
                this.from = new int[e];
                this.to = new int[e];
                this.cost = new long[e];
                Arrays.fill(from, -1);
                Arrays.fill(to, -1);
                this.n = n;
                this.countEdgeFrom = new int[n];
            }

            public void addEdge(int u, int v, long c) {
                from[idx] = u;
                to[idx] = v;
                cost[idx] = c;
                idx++;
                countEdgeFrom[u]++;
            }

            public Csr buildCsr() {
                var elist = new int[idx];
                var costList = new long[idx];

                var fromAcc = new int[n + 1];
                for (int i = 0; i < n; i++) {
                    fromAcc[i + 1] = fromAcc[i] + countEdgeFrom[i];
                }
                int[] startFrom = Arrays.copyOf(fromAcc, fromAcc.length);

                for (int i = 0; i < idx; i++) {
                    var u = from[i];
                    var v = to[i];
                    var c = cost[i];

                    elist[fromAcc[u]] = v;
                    costList[fromAcc[u]] = c;
                    fromAcc[u]++;
                }
                return new Csr(elist, costList, startFrom);
            }
        }

        record Csr(int[] elist, long[] cost, int[] start) {
        }

    }

    //ダイクストラ　半環
    // min max -> パス上のコストのmaxのmin
}
