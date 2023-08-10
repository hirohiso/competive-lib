package jp.hirohiso.competive.util.graph;

import java.util.Arrays;

public class Warshall {
    public static void main(String[] args) {

    }


    public static class WarshallFloyd {
        private long[][] dist;
        private long INF = Long.MAX_VALUE;

        public WarshallFloyd(int n) {
            dist = new long[n][n];
            for (int i = 0; i < dist.length; i++) {
                Arrays.fill(dist[i], INF);
                for (int j = 0; j < dist.length; j++) {
                    if (i == j) {
                        dist[i][j] = 0;
                    } else {
                        dist[i][j] = INF;
                    }
                }
            }
        }

        public void addUndirectedEdge(int s, int e, long cost) {
            if (!checkNodeRange(s) || !checkNodeRange(e)) {
                throw new IllegalArgumentException();
            }
            dist[s][e] = cost;
            dist[e][s] = cost;
        }

        public void addDirectedEdge(int s, int e, long cost) {
            if (!checkNodeRange(s) || !checkNodeRange(e)) {
                throw new IllegalArgumentException();
            }
            dist[s][e] = cost;
        }

        public void solve() {
            for (int k = 0; k < dist.length; k++) {
                for (int i = 0; i < dist.length; i++) {
                    for (int j = 0; j < dist.length; j++) {
                        if (dist[i][k] == INF || dist[k][j] == INF) {
                            continue;
                        }
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }

        public long[][] answer(){
            return dist;
        }

        public boolean hasAnswer(){
            for (int i = 0; i < dist.length; i++) {
                if (dist[i][i] < 0) {
                    return false;
                }
            }
            return true;
        }

        private boolean checkNodeRange(int n) {
            return n < 0 || n >= dist.length ? false : true;
        }
    }
}
