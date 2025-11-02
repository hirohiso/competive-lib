package jp.hirohiso.competive.util.tree;

import java.util.*;

public class ReRootingTreeMain {

    public static void main(String[] args) {

    }

    //頂点にモノイドVを持ち、辺のモノイドEを持つReRootingTree
    //
    //
    static class ReRootingTree<E, V> {

        //辺の情報を持つ隣接リスト
        record Edge(int u, int v) {
        }

        //辺のモノイドマージを行うインターフェース
        interface MergeEdgeFunction<E> {
            E merge(E e1, E e2);
        }

        //モノイドEの単位元を返すインターフェース
        interface GetEFunction<E> {
            E e();
        }

        //モノイドEの単位元を返すインターフェース
        interface ComputeEdgeFunction<E, V> {
            //頂点toの値から辺(from,to)のEを返す
            E putEdge(V v, int from, int to);
        }

        //モノイドVの単位元を返すインターフェース
        interface ComputeVertexFunction<E, V> {
            //頂点uとEの値から辺(from,to)のEを返す
            V putVertex(int u, E sum);
        }

        List<List<Edge>> adj;
        MergeEdgeFunction<E> mergeEdgeFunction;
        GetEFunction<E> getEFunction;
        ComputeEdgeFunction<E, V> computeEdgeFunction;
        ComputeVertexFunction<E, V> computeVertexFunction;


        //Edgeに付与したモノイドは再利用する
        HashMap<Edge, E> dp = new HashMap<>();

        int N;

        public ReRootingTree(int N,
                             MergeEdgeFunction<E> mergeEdgeFunction,
                             GetEFunction<E> getEFunction,
                             ComputeEdgeFunction<E, V> computeEdgeFunction,
                             ComputeVertexFunction<E, V> computeVertexFunction) {

            //隣接リストの初期化
            this.N = N;
            adj = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                adj.add(new ArrayList<>());
            }
            this.mergeEdgeFunction = mergeEdgeFunction;
            this.getEFunction = getEFunction;
            this.computeEdgeFunction = computeEdgeFunction;
            this.computeVertexFunction = computeVertexFunction;
        }

        //辺を追加する
        public void addEdge(int u, int v) {
            Edge e1 = new Edge(u, v);
            Edge e2 = new Edge(v, u);
            adj.get(u).add(e1);
            adj.get(v).add(e2);
        }

        public void solve(int start) {
            var ans = new ArrayList<V>(N);
            dfs(ans, start, -1);
        }

        private E dfs(List<V> ans, int now, int parent) {
            var list = adj.get(now).stream().filter(e -> e.v != parent).toList();

            //葉ノードの場合
            //辺モノイドを単位元として処理する
            if (list.isEmpty()) {
                var val = computeVertexFunction.putVertex(now, getEFunction.e());
                ans.add(now, val);
                var eVal = computeEdgeFunction.putEdge(val, parent, now);
                dp.put(new Edge(parent, now), eVal);
                return eVal;
            }

            var sumE = getEFunction.e();
            for (var e : list) {
                var childE = dfs(ans, e.v, now);
                sumE = mergeEdgeFunction.merge(sumE, childE);
            }
            var val = computeVertexFunction.putVertex(now, sumE);
            ans.add(now, val);
            var eVal = computeEdgeFunction.putEdge(val, parent, now);
            dp.put(new Edge(parent, now), eVal);
            return eVal;
        }

        public void reRooting() {
            var ans = new ArrayList<V>(N);
            for (int i = 0; i < N; i++) {
                ans.add(null);
            }
            dfs2(ans, 0, -1);
        }

        private void dfs2(List<V> ans, int now, int parent) {
            if(ans.get(now) == null ){
                //頂点nowを根としたときの答えが未計算なら計算する
                var list = adj.get(now);
                //親方向のEを計算
                E parentE = getEFunction.e();
                for (var e : list) {
                    var childE = dp.get(e);
                    parentE = mergeEdgeFunction.merge(parentE, childE);
                }
                var val = computeVertexFunction.putVertex(now, parentE);
                ans.add(now, val);
            }

            var sumLeft = getEFunction.e();

            var accRight = new ArrayList<E>();
            accRight.add(getEFunction.e());
            var list = adj.get(now);

            //右側からの累積和を計算
            for (int i = list.size() - 1; i >= 0; i--) {
                var e = list.get(i);
                var childE = dp.get(e);
                var merged = mergeEdgeFunction.merge(accRight.get(accRight.size() - 1), childE);
                accRight.add(merged);
            }
            for (int i = 0; i < list.size(); i++) {
                var e = list.get(i);
                if (e.v == parent) {
                    //親方向の辺の場合
                    continue;
                }
                //子方向の辺の場合
                var leftE = sumLeft;
                var rightE = accRight.get(accRight.size() - 1 - i);
                var withoutChildE = mergeEdgeFunction.merge(leftE, rightE);

                //親方向のEを更新
                var parentVal = computeVertexFunction.putVertex(now, withoutChildE);
                var parentE = computeEdgeFunction.putEdge(parentVal, e.v, now);
                dp.put(new Edge(now, e.v), parentE);

                //子ノードに再帰
                dfs2(ans, e.v, now);

                //左側の累積和を更新
                var childE = dp.get(e);
                sumLeft = mergeEdgeFunction.merge(sumLeft, childE);
            }
        }
    }
}
