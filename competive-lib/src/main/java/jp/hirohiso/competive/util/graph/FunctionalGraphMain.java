package jp.hirohiso.competive.util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionalGraphMain {
    public static void main(String[] args) {
        var to = new int[]{2, 3, 5, 3, 6, 7, 3, 4, 4, 6, 12, 11}; //
        Arrays.setAll(to, i -> to[i] - 1); // 0-indexed
        FunctionalGraph fg = new FunctionalGraph(to);

        System.out.println(fg.jump(0, 4)); // => 6
        System.out.println(Arrays.toString(fg.getCycleInfo(0))); // => [3, 3]
        System.out.println(fg.getDepth(0)); // => 3
        System.out.println(fg.getRoot(0));  // => 2
        System.out.println(fg.allCycles()); // => [[2, 3, 4]]
    }

    /**
     * FunctionalGraph
     */
    public static class FunctionalGraph {
        private final int[] to;
        private final int[][] jumpTable;
        private final int[] depth;
        private final int[] loopLength;
        private final int[] loopStart;
        private final boolean[] visited;
        private final int n;
        private static final int MAX_LOG = 60;

        public FunctionalGraph(int[] to) {
            this.n = to.length;
            this.to = to;
            this.jumpTable = new int[MAX_LOG][n];
            this.depth = new int[n];
            this.loopLength = new int[n];
            this.loopStart = new int[n];
            this.visited = new boolean[n];

            // 倍化テーブル構築
            buildDoubling();

            // 各ノードについてDFSでループ検出・深さ計算
            Arrays.fill(depth, -1);
            for (int i = 0; i < n; i++) {
                if (depth[i] == -1) {
                    dfs(i, new ArrayList<>());
                }
            }
        }

        private void buildDoubling() {
            for (int i = 0; i < n; i++) {
                jumpTable[0][i] = to[i];
            }
            for (int k = 1; k < MAX_LOG; k++) {
                for (int i = 0; i < n; i++) {
                    jumpTable[k][i] = jumpTable[k - 1][jumpTable[k - 1][i]];
                }
            }
        }

        /**
         * 指定されたノードから到達可能なループの長さと深さを取得する
         */
        public int[] getCycleInfo(int v) {
            return new int[]{depth[v], loopLength[v]};
        }

        /**
         * 指定されたノードから到達可能なループの根を取得する
         */
        public int getRoot(int v) {
            return loopStart[v];
        }

        /**
         * 指定されたノードからループまでの深さを取得する
         */
        public int getDepth(int v) {
            return depth[v];
        }

        /**
         * サイクルを全て取得する
         */
        public ArrayList<ArrayList<Integer>> allCycles() {
            boolean[] seen = new boolean[n];
            var res = new ArrayList<ArrayList<Integer>>();
            for (int i = 0; i < n; i++) {
                int root = loopStart[i];
                if (!seen[root]) {
                    var cycle = new ArrayList<Integer>();
                    int cur = root;
                    do {
                        cycle.add(cur);
                        seen[cur] = true;
                        cur = to[cur];
                    } while (cur != root);
                    res.add(cycle);
                }
            }
            return res;
        }

        /**
         * 指定されたノードからk回ジャンプした先のノードを取得する
         */
        public int jump(int v, long k) {
            for (int i = 0; i < MAX_LOG; i++) {
                if (((k >> i) & 1) == 1) {
                    v = jumpTable[i][v];
                }
            }
            return v;
        }


        private void dfs(int v, List<Integer> path) {
            int cur = v;
            while (!visited[cur]) {
                visited[cur] = true;
                path.add(cur);
                cur = to[cur];
            }

            int loopStartIdx = path.indexOf(cur);
            if (loopStartIdx != -1) {
                // 新たにループが見つかった場合
                int cycleLen = path.size() - loopStartIdx;
                for (int i = loopStartIdx; i < path.size(); i++) {
                    int u = path.get(i);
                    loopLength[u] = cycleLen;
                    depth[u] = 0;
                    loopStart[u] = cur;
                }
            } else {
                //すでに訪問済みのノードに到達した場合
                loopStartIdx = path.size() - 1;
                int u = path.get(loopStartIdx);
                int next = to[u];
                depth[u] = depth[next] + 1;
                loopLength[u] = loopLength[next];
                loopStart[u] = depth[cur] == 0 ? cur : loopStart[next];
            }

            for (int i = loopStartIdx - 1; i >= 0; i--) {
                int u = path.get(i);
                int next = to[u];
                depth[u] = depth[next] + 1;
                loopLength[u] = loopLength[next];
                loopStart[u] = loopStart[next];
            }
        }
    }
}
