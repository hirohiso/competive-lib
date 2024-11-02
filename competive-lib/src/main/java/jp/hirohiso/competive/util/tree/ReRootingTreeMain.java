package jp.hirohiso.competive.util.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class ReRootingTreeMain {

    public static void main(String[] args) {

    }

    public static class ReRootingTree<T> {
        // 木のサイズ
        private final int n;
        // 隣接リスト形式での木の表現
        private final List<List<Edge>> tree;
        // 各ノードのDP値
        private final List<T> dp;
        // 各エッジのDP値
        private final List<List<T>> dpEdge;
        // 単位元（再帰的計算に使用）
        private final T identity;
        // 二項演算
        private final BinaryOperator<T> merge;
        // 子ノードから親ノードへの変換関数
        private final Function<T, T> addRoot;

        public ReRootingTree(int n, T identity, BinaryOperator<T> merge, Function<T, T> addRoot) {
            this.n = n;
            this.tree = new ArrayList<>(n);
            this.dp = new ArrayList<>(n);
            this.dpEdge = new ArrayList<>(n);
            this.identity = identity;
            this.merge = merge;
            this.addRoot = addRoot;

            for (int i = 0; i < n; i++) {
                tree.add(new ArrayList<>());
                dp.add(identity);
                dpEdge.add(new ArrayList<>());
            }
        }

        public void addEdge(int u, int v) {
            tree.get(u).add(new Edge(u, v));
            tree.get(v).add(new Edge(v, u));
        }

        public List<T> solve() {
            dfs(0, -1); // 任意のノードを根としてDFSを実行
            reroot(0, -1, identity); // 根を動的に再計算
            return dp;
        }

        private T dfs(int node, int parent) {
            T accum = identity;
            for (Edge edge : tree.get(node)) {
                if (edge.to == parent) continue;
                T childDp = dfs(edge.to, node);
                dpEdge.get(node).add(childDp);
                accum = merge.apply(accum, childDp);
            }
            dp.set(node, addRoot.apply(accum));
            return dp.get(node);
        }

        private void reroot(int node, int parent, T parentContribution) {
            int size = tree.get(node).size();
            List<T> leftAccum = new ArrayList<>(size + 1);
            List<T> rightAccum = new ArrayList<>(size + 1);

            leftAccum.add(identity);
            rightAccum.add(identity);

            for (int i = 0; i < size; i++) {
                T childDp = dpEdge.get(node).get(i);
                leftAccum.add(merge.apply(leftAccum.get(i), childDp));
            }

            for (int i = size - 1; i >= 0; i--) {
                T childDp = dpEdge.get(node).get(i);
                rightAccum.add(merge.apply(rightAccum.get(size - 1 - i), childDp));
            }

            for (int i = 0; i < size; i++) {
                Edge edge = tree.get(node).get(i);
                if (edge.to == parent) continue;

                T withoutCurrentChild = merge.apply(leftAccum.get(i), rightAccum.get(size - 1 - i));
                reroot(edge.to, node, addRoot.apply(merge.apply(parentContribution, withoutCurrentChild)));
            }

            dp.set(node, addRoot.apply(merge.apply(parentContribution, leftAccum.get(size))));
        }

        private static class Edge {
            int from, to;
            Edge(int from, int to) {
                this.from = from;
                this.to = to;
            }
        }
    }

}
