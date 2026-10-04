package jp.hirohiso.competive.util.graph;

import java.util.Arrays;

public class Warshall {
    public static void main(String[] args) {

    }


    /**
     * 全点対最短路を求める Floyd–Warshall 法。
     * <p>頂点は [0,N)。平行辺は最小重みを採用。負辺も可で、solve 後に
     * hasAnswer が false なら負閉路があり最短路は未定義。
     * 到達不能は Long.MAX_VALUE。有限距離の加算が long に収まること。
     * 構築 O(N²)、solve O(N³)、領域 O(N²)。answer は内部の距離行列を返す。</p>
     */
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
            dist[s][e] = Math.min(dist[s][e], cost);
            dist[e][s] = Math.min(dist[e][s], cost);
        }

        public void addDirectedEdge(int s, int e, long cost) {
            if (!checkNodeRange(s) || !checkNodeRange(e)) {
                throw new IllegalArgumentException();
            }
            dist[s][e] = Math.min(dist[s][e], cost);
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
