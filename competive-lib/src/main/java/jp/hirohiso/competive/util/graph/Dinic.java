package jp.hirohiso.competive.util.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Dinic {

    public static void main(String[] args) {
        DinicFlow df = new DinicFlow(5);
        df.addEdge(0, 1, 10);
        df.addEdge(0, 2, 2);
        df.addEdge(1, 2, 6);
        df.addEdge(1, 3, 6);
        df.addEdge(2, 4, 5);
        df.addEdge(3, 2, 3);
        df.addEdge(3, 4, 8);
        long answer = df.solve(0, 4);
        System.out.println(answer);

    }

    //Dinic法の実装
    //蟻本pp194参照
    public static class DinicFlow {
        //隣接リスト
        private List<Edge>[] edgeList;
        //sからの距離
        private long level[];

        //どこまで調べ終わったか
        private long iter[];

        public DinicFlow(int size) {
            this.edgeList = new ArrayList[size];
            this.level = new long[size];
            this.iter = new long[size];

            for (int i = 0; i < size; i++) {
                this.edgeList[i] = new ArrayList<Edge>();
            }
        }

        public void addEdge(int node1, int node2, int cost) {
            Edge e1 = Edge.of(node2, cost);
            Edge e2 = Edge.of(node1, 0);
            e1.setRevEdge(e2);
            e2.setRevEdge(e1);

            this.edgeList[node1].add(e1);
            this.edgeList[node2].add(e2);

        }

        public long solve(int source, int tink) {
            long flow = 0;
            while (true) {
                bfs(source);
                if (this.level[tink] < 0) {
                    return flow;
                }
                resetIter();
                long f;
                while ((f = dfs(source, tink, Long.MAX_VALUE)) > 0) {
                    flow += f;
                }
            }

        }

        private void resetIter() {
            for (int i = 0; i < this.iter.length; i++) {
                this.iter[i] = 0;
            }
        }

        private long dfs(int source, int tink, long maxValue) {
            if(source == tink) {
                return maxValue;
            }
            for(Edge e : this.edgeList[source]){
                if(e.cost >0 && this.level[source] < this.level[e.target]){
                    long d = dfs(e.target,tink,Math.min(maxValue, e.cost));
                    if(d > 0){
                        e.cost -= d;
                        e.revers.cost +=d;
                        return d;
                    }
                }
            }
            return 0;
        }

        private void bfs(int source) {
           resetLevel();
           Queue<Integer> queue = new LinkedList<>();
           this.level[source] = 0;
           queue.add(source);
           while(!queue.isEmpty()){
               int v = queue.poll();
               for(Edge e : this.edgeList[v]){
                   if(e.cost > 0 && level[e.target] < 0 ){
                       level[e.target] = level[v] + 1;
                       queue.add(e.target);
                   }
               }
           }

        }

        private void resetLevel() {
            for (int i = 0; i < this.level.length; i++) {
                this.level[i] = -1;
            }
        }

        private static class Edge {
            int target;
            Edge revers;
            long cost;

            private Edge(int node, long cost) {
                this.target = node;
                this.cost = cost;
            }

            public static Edge of(int node, long cost) {
                return new Edge(node, cost);
            }

            public void setRevEdge(Edge e) {
                this.revers = e;
            }
        }

    }
}
