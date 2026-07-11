package jp.hirohiso.competive.util.graph;

import java.util.Arrays;
import java.util.List;

public class MinMaxFlowMain {
    public static void main(String[] args) {
        var mmf = new MinMaxFlow(4);

        mmf.addEdge(0, 1, 1, 3);
        mmf.addEdge(0, 2, 1, 1);
        mmf.addEdge(1, 3, 1, 1);
        mmf.addEdge(1, 2, 1, 2);
        mmf.addEdge(2, 3, 1, 3);


        var ret = mmf.flowSlope(0, 3);

        for (int i = 0; i < 5; i++) {
            System.out.println(mmf.getEdge(i));
        }

    }

    //todo: MinMaxFlowの実装
    public static class MinMaxFlow {
        // 実装はここに記述
        // 例: 最小費用流量を求めるアルゴリズム
        //https://github.com/NASU41/AtCoderLibraryForJava/tree/master/MinCostFlow

        int n;
        int m;

        CsrBuilder builder;
        Csr csr;

        public MinMaxFlow(int size) {
            this(size, Math.min(size * (size - 1) / 2, 10 * size));
        }

        public MinMaxFlow(int vSize, int eSize) {
            n = vSize;
            m = eSize;
            builder = new CsrBuilder(n, m);
        }

        public int addEdge(int from, int to, long cost, long capacity) {
            return builder.addEdge(from, to, cost, capacity);
        }

        public CostAndFlow flow(int s, int t) {
            return null;
        }

        public CostAndFlow flow(int s, int t, long flowLimit) {
            return null;
        }

        public List<CostAndFlow> flowSlope(int s, int t) {
            csr = builder.buildCsr();
            return null;
        }

        public List<CostAndFlow> flowSlope(int s, int t, long flowLimit) {
            return null;
        }

        //build 後、辺 ID i の情報はこう復元します。Step 2 のテストで assert しておくと後段のデバッグが楽になります。
        //
        //- 順辺の位置: e = edgeIdx[i]、逆辺の位置: rev[e]
        //- 流れた量: flow = csrCap[rev[e]](逆辺の cap は増えた分 = 流量)
        //- 元の容量: csrCap[e] + csrCap[rev[e]]
        public Edge getEdge(int i) {
            var e = csr.edgeIdx[i];
            var re = csr.rev[e];
            var flow = csr.capacity[re];
            var cap = csr.capacity[e] + csr.capacity[re];
            var cost = csr.cost[e];
            return new Edge(csr.elist[re], csr.elist[e], cap, flow, cost);
        }


        private static class CsrBuilder {
            int[] from;
            int[] to;
            long[] cost;
            long[] capacity;
            int idx = 0;
            int n = 0;
            int[] degree;

            public CsrBuilder(int n, int e) {
                this.from = new int[e];
                this.to = new int[e];
                this.cost = new long[e];
                this.capacity = new long[e];
                Arrays.fill(from, -1);
                Arrays.fill(to, -1);
                this.n = n;
                this.degree = new int[n];
            }

            public int addEdge(int u, int v, long c, long cap) {
                from[idx] = u;
                to[idx] = v;
                cost[idx] = c;
                capacity[idx] = cap;
                degree[u]++;
                degree[v]++;//逆辺も必要なため加算
                return idx++;
            }

            public Csr buildCsr() {
                var elist = new int[2 * idx];
                var costList = new long[2 * idx];
                var capacityList = new long[2 * idx];
                var rev = new int[2 * idx];
                var edgeIdx = new int[idx];

                var start = new int[n + 1];
                for (int i = 0; i < n; i++) {
                    start[i + 1] = start[i] + degree[i];
                }
                int[] counter = Arrays.copyOf(start, start.length);

                for (int i = 0; i < idx; i++) {
                    var u = from[i];
                    var v = to[i];
                    var c = cost[i];
                    var cap = capacity[i];

                    var fwdPos = counter[u]++;
                    var revPos = counter[v]++;
                    elist[fwdPos] = v;
                    costList[fwdPos] = c;
                    capacityList[fwdPos] = cap;
                    rev[fwdPos] = revPos;

                    elist[revPos] = u;
                    costList[revPos] = -c;
                    capacityList[revPos] = 0;
                    rev[revPos] = fwdPos;
                    edgeIdx[i] = fwdPos;
                }
                return new Csr(elist, costList, capacityList, start, rev, edgeIdx);
            }
        }

        record Csr(int[] elist, long[] cost, long[] capacity, int[] start, int[] rev, int[] edgeIdx) {
        }

        public record CostAndFlow(long cost, long flow) {

        }

        public record Edge(int from, int to, long cap, long flow, long cost) {
        }
    }
}
