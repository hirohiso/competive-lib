package jp.hirohiso.competive.util.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class DoblingTreeMain {
    public static void main(String[] args) {

    }


    private void cal(int size, int root) {
        var lists = new ArrayList<LinkedList<Integer>>();
        for (int i = 0; i < size; i++) {
            lists.add(new LinkedList<>());
        }
        //隣接リストを構築する

        //各頂点ごとに深さと親を計算
        //初期化
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

        //使いかた 頂点1から5個上の祖先を取得
        var u = 1;
        var k = 5;
        if (!(k > dis[u])) {
            var now = u;
            var idx = 0;
            while ((k >> idx) > 0) {
                if (((k >> idx) & 1) == 1) {
                    now = parent[idx][now];
                }
                if (now == -1) {
                    break;
                }
                idx++;
            }
        }
    }
}
