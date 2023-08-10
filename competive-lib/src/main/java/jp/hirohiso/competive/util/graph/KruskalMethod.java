package jp.hirohiso.competive.util.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class KruskalMethod {
    public static void main(String[] args) {
        KruskalMethod.Kruskal graph = new KruskalMethod.Kruskal(6);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 3, 7);
        graph.addEdge(2, 4, 1);
        graph.addEdge(1, 4, 3);
        graph.addEdge(3, 4, 1);
        graph.addEdge(3, 5, 1);
        graph.addEdge(4, 5, 6);
        graph.solve();
        System.out.println(graph.answer());
    }

    private static class Kruskal {
        private final int size;
        private final static Comparator<Edge> comp = Comparator.comparingInt(e -> e.cost);
        private final PriorityQueue<Edge> pq;
        private final List<Edge> treeEdge = new ArrayList<>();

        public Kruskal(int i) {
            this.size = i;
            pq = new PriorityQueue<>(comp);
        }

        public void addEdge(int i, int i1, int i2) {
            pq.add(new Edge(i, i1, i2));
        }

        public void solve() {
            UnionF union = new UnionF(size);
            Edge e;

            while ((e = pq.poll()) != null) {
                int node1 = e.node1;
                int node2 = e.node2;

                if(union.isSameSet(node1,node2)){
                    continue;
                }
                treeEdge.add(e);
                union.mergeSet(node1,node2);
            }

        }

        public long answer() {
            int result = 0;
            for(Edge e:this.treeEdge){
                result += e.cost;
            }
            return result;
        }

        private static class Edge {
            private final int node1;
            private final int node2;
            private final int cost;

            Edge(int n1, int n2, int cost) {
                this.node1 = n1;
                this.node2 = n2;
                this.cost = cost;
            }
        }

        public static class UnionF {
            int[] elements;
            int[] rank;

            public UnionF(int size) {
                this.elements = new int[size];
                this.rank = new int[size];
                for (int i = 0; i < elements.length; i++) {
                    elements[i] = i;
                    rank[i] = 0;
                }
            }

            public void mergeSet(int x, int y) {
                int i = root(x);

                int j = root(y);

                if (i == j) {
                    return;
                }

                if (rank[i] > rank[j]) {
                    elements[j] = i;

                } else {
                    elements[i] = j;
                    if (rank[i] == rank[j]) {
                        rank[i]++;
                    }
                }
            }

            public int root(int x) {
                int i = x;
                while (elements[i] != i) {
                    i = elements[i];
                }
                int root = i;
                //経路圧縮
                i = x;
                while (elements[i] != i) {
                    i = elements[i];
                    elements[i] = root;
                }
                return root;
            }

            public boolean isSameSet(int x, int y) {
                int i = root(x);
                int j = root(y);
                return i == j;
            }

            @Override
            public String toString() {
                return this.elements.toString();
            }
        }
    }
}
