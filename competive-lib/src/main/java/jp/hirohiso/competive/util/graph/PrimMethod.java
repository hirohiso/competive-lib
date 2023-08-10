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
        Map<Integer, List<Edge>> edgeListMap;
        private List<Edge> treeEdge = new ArrayList<>();
        private final Comparator<Edge> comp = Comparator.comparingInt(e -> e.cost);

        public Prim(int i) {
            this.size = i;
            this.edgeListMap = new TreeMap<>();
            for (int j = 0; j < this.size; j++) {
                this.edgeListMap.put(j, new ArrayList<>());
            }
        }

        //i,i1は0 から size-1まで
        public void addEdge(int i, int i1, int i2) {
            Edge e = new Edge(i, i1, i2);
            this.edgeListMap.get(i).add(e);
            this.edgeListMap.get(i1).add(e);
        }

        public void solve() {
            //探索済ノード
            boolean[] visited = new boolean[size];
            Arrays.fill(visited, false);

            PriorityQueue<Edge> pq = new PriorityQueue<>(comp);

            //頂点0を探索済にする
            visited[0] = true;
            //頂点0から出ている辺を全て加える
            List<Edge> list = this.edgeListMap.get(0);
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
                int n = visited[e.node1] ? e.node2 : e.node1;
                //頂点nから出ている辺を全て加える
                List<Edge> list2 = this.edgeListMap.get(n);
                pq.addAll(list2);
                visited[n] = true;
            }
        }

        public int answer(){
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
    }
}
