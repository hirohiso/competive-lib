package jp.hirohiso.competive.util.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstraLight {

    public static void main(String[] args) {
        Dijkstra graph = new Dijkstra(4);
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
        private List<Edge>[] edgeList;
        private int[] parent;

        //private edge[];
        public Dijkstra(int size) {
            this.size = size;
            this.disitance = new long[size];
            this.edgeList = new ArrayList[size];
            for (int i = 0; i < size; i++) {
                this.disitance[i] = Long.MAX_VALUE;
                this.edgeList[i] = new ArrayList<Edge>();
            }
            this.parent = new int[size];
            for (int i = 0; i < size; i++) {
                this.parent[i] = -1;
            }
        }

        public long[] getDistance() {
            return this.disitance;
        }
        public int[] getParent() {
            return this.parent;
        }

        public void solve(int root) {
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
                for (Edge e : this.edgeList[n]) {
                    int node = e.otherNode;
                    long cost = e.cost;
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
            Edge e = Edge.of(node2, cost);
            this.edgeList[node1].add(e);
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

        private static class Edge {
            int otherNode;
            long cost;

            private Edge(int node, long cost) {
                this.otherNode = node;
                this.cost = cost;
            }

            public static Edge of(int node, long cost) {
                return new Edge(node, cost);
            }
        }

    }

}
