package jp.hirohiso.competive.util.graph;

import java.util.*;

public class MinMaxFlowMain {
    public static void main(String[] args) {
        var mmf = new MinMaxFlow(4);

        mmf.addEdge(0, 1, 1, 3);
        mmf.addEdge(0, 2, 1, 1);
        mmf.addEdge(1, 3, 1, 1);
        mmf.addEdge(1, 2, 1, 2);
        mmf.addEdge(2, 3, 1, 3);


        var ret = mmf.flowSlope(0, 3);
        System.err.println(ret);

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

        long INF = Long.MAX_VALUE;

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
            return flow(s, t, INF);
        }

        public CostAndFlow flow(int s, int t, long flowLimit) {
            return flowSlope(s, t, flowLimit).getLast();
        }

        public List<CostAndFlow> flowSlope(int s, int t) {
            return flowSlope(s, t, INF);
        }

        public List<CostAndFlow> flowSlope(int s, int t, long flowLimit) {
            csr = builder.buildCsr();

            var dual = new long[n];
            var dist = new long[n];
            var visited = new boolean[n];
            var prevE = new int[n];

            var flow = 0L;
            var cost = 0L;
            var preCost = -1L;

            var result = new LinkedList<CostAndFlow>();
            result.add(new CostAndFlow(cost, flow));

            var count = 0;
            while (flow < flowLimit) {
                //dualの処理を呼び出す
                if (!dualRef(s, t, dual, dist, prevE, visited)) {
                    break;
                }
                var c = flowLimit - flow;
                for (int i = t; i != s; i = csr.elist[csr.rev[prevE[i]]]) {
                    c = Math.min(c, csr.capacity[prevE[i]]);
                }
                for (int i = t; i != s; i = csr.elist[csr.rev[prevE[i]]]) {
                    var e = prevE[i];
                    var re = csr.rev[e];
                    csr.capacity[e] -= c;
                    csr.capacity[re] += c;
                }


                var d = -dual[s];
                flow += c;
                cost += c * d;
                if (cost == preCost) {
                    result.removeLast();
                }
                result.add(new CostAndFlow(cost, flow));
                preCost = cost;
                if (10 < ++count) {
                    break;
                }
            }


            return result;
        }

        record State(long dis, int v) {
        }

        ;

        private boolean dualRef(int s, int t, long[] dual, long[] dist, int[] prevE, boolean[] vis) {
            Arrays.fill(dist, INF);
            Arrays.fill(prevE, -1);
            Arrays.fill(vis, false);

            var pq = new PriorityQueue<State>(
                    Comparator.comparingLong(State::dis)
            );
            dist[s] = 0;
            pq.add(new State(0L, s));
            while (!pq.isEmpty()) {
                var v = pq.poll().v;
                if (vis[v]) {
                    continue;
                }
                vis[v] = true;
                if (v == t) {
                    break;
                }
                for (int i = csr.start[v]; i < csr.start[v + 1]; i++) {
                    var cost = csr.cost[i];
                    var next = csr.elist[i];
                    var cap = csr.capacity[i];
                    if (vis[next] || cap == 0) {
                        continue;
                    }

                    var nc = cost - dual[next] + dual[v];
                    if (dist[next] - dist[v] > nc) {
                        dist[next] = dist[v] + nc;
                        prevE[next] = i;
                        pq.add(new State(dist[next], next));
                    }
                }
            }
            if (!vis[t]) {
                return false;
            }

            for (int i = 0; i < n; i++) {
                if (!vis[i]) {
                    continue;
                }
                dual[i] -= dist[t] - dist[i];
            }
            return true;
        }

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
