package jp.hirohiso.competive.util.graph;

import java.util.*;

public class PrimMethod {
    public static void main(String[] args) {
        PrimMethod.Prim graph = new Prim(6);
        graph.addEdge(0 ,1, 1);
        graph.addEdge(0 ,2, 3);
        graph.addEdge(1 ,2, 1);
        graph.addEdge(1 ,3, 7);
        graph.addEdge(2 ,4, 1);
        graph.addEdge(1 ,4, 3);
        graph.addEdge(3 ,4, 1);
        graph.addEdge(3 ,5, 1);
        graph.addEdge(4 ,5, 6);
        graph.solve();
        System.out.println(graph.answer());
    }

    private static class Prim {
        private final int size;
        ArrayList<LinkedList<Edge>> adjList;
        private final List<Edge> treeEdge = new ArrayList<>();
        private final Comparator<Edge> comp = Comparator.comparingLong(e -> e.cost);

        public Prim(int i) {
            this.size = i;
            this.adjList = new ArrayList<>();
            for (int j = 0; j < this.size; j++) {
                this.adjList.add(new LinkedList<>());
            }
        }

        //i,i1は0 から size-1まで
        public void addEdge(int i, int i1, long i2) {
            var e = new Edge(i, i1, i2);
            this.adjList.get(i).add(e);
            this.adjList.get(i1).add(e);
        }

        public void solve() {
            //探索済ノード
            this.treeEdge.clear();
            var visited = new boolean[size];
            Arrays.fill(visited, false);
            PriorityQueue<Edge> pq = new PriorityQueue<>(comp);

            //頂点0を探索済にする
            visited[0] = true;
            //頂点0から出ている辺を全て加える
            var list = this.adjList.get(0);
            pq.addAll(list);

            Edge e;
            while ((e = pq.poll()) != null) {
                if (visited[e.node1] && visited[e.node2]) {
                    //両方探索済ならスキップ
                    continue;
                }
                //未探索の点を得る
                //両方未探索ケースはない
                this.treeEdge.add(e);
                var n = visited[e.node1] ? e.node2 : e.node1;
                //頂点nから出ている辺を全て加える
                var list2 = this.adjList.get(n);
                pq.addAll(list2);
                visited[n] = true;
            }
        }

        public long answer(){
            var  result = 0L;
            for(Edge e:this.treeEdge){
                result += e.cost;
            }
            return result;
        }

        private static class Edge {
            private final int node1;
            private final int node2;
            private final long cost;

            Edge(int n1, int n2, long cost) {
                this.node1 = n1;
                this.node2 = n2;
                this.cost = cost;
            }
        }
    }
}
