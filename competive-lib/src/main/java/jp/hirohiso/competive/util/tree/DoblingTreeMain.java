package jp.hirohiso.competive.util.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class DoblingTreeMain {
    public static void main(String[] args) {

    }

    class TreeBuilder {
        int N;
        ArrayList<LinkedList<Integer>> lists;

        public TreeBuilder(int n) {
            N = n;
            lists = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                lists.add(new LinkedList<>());
            }
        }

        public void addEdge(int v1, int v2) {
            lists.get(v1).add(v2);
            lists.get(v2).add(v1);
        }

        public RootTree rootTree(int root) {
            return new RootTree(N, root, lists);
        }

        //根付き木
        //各頂点の親
        //根からの深さ
        class RootTree {
            int size;
            int root;
            int[][] parents;
            int[] depth;
            ArrayList<LinkedList<Integer>> lists;

            public RootTree(int size, int root, ArrayList<LinkedList<Integer>> lists) {
                this.size = size;
                this.root = root;
                this.lists = lists;
                init();
            }

            private void init() {
                var K = 1;
                while ((1 << K) < size) {
                    K++;
                }
                var parent = new int[K][size];
                for (int i = 0; i < K; i++) {
                    Arrays.fill(parent[i], -1);
                }
                var q = new LinkedList<Integer>();
                var dis = new int[size];
                Arrays.fill(dis, Integer.MAX_VALUE);
                q.add(root);
                dis[root] = 0;
                while (!q.isEmpty()) {
                    var now = q.pollFirst();
                    var list = lists.get(now);
                    for (var next : list) {
                        if (dis[next] == Integer.MAX_VALUE) {
                            q.add(next);
                            dis[next] = dis[now] + 1;
                            parent[0][next] = now;
                        }
                    }
                }
                //ダブリング
                for (int i = 1; i < K; i++) {
                    for (int j = 0; j < size; j++) {
                        if (parent[i - 1][j] == -1) {
                            parent[i][j] = -1;
                        } else {
                            parent[i][j] = parent[i - 1][parent[i - 1][j]];
                        }
                    }
                }
                this.parents = parent;
                this.depth = dis;
            }


            //vからg世代上の先祖を取得する
            //存在しない場合は-1を返却する
            public int ancestor(int v, int g) {
                if (!(g > depth[v])) {
                    var now = v;
                    var idx = 0;
                    while ((g >> idx) > 0) {
                        if (((g >> idx) & 1) == 1) {
                            now = parents[idx][now];
                        }
                        if (now == -1) {
                            break;
                        }
                        idx++;
                    }
                    return now;
                }
                return -1;
            }

            //最小共通祖先
            public int lca(int u, int v) {
                if (depth[u] < depth[v]) {
                    var t = u;
                    u = v;
                    v = t;// u の方が深いとする
                }
                int K = parents.length;
                // LCA までの距離を同じにする
                for (int k = 0; k < K; k++) {
                    if (((depth[u] - depth[v]) >> k & 1) == 1) {
                        u = parents[k][u];
                    }
                }
                // 二分探索で LCA を求める
                if (u == v) {
                    return u;
                }
                for (int k = K - 1; k >= 0; k--) {
                    if (parents[k][u] != parents[k][v]) {
                        u = parents[k][u];
                        v = parents[k][v];
                    }
                }
                return parents[0][u];
            }
        }
    }
}
