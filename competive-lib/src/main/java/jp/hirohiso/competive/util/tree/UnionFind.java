package jp.hirohiso.competive.util.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UnionFind {

    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ

    }

    //ALC準拠
    public static class DisjointSetUnion {
        int size;
        int[] parentsOrSize;

        public DisjointSetUnion(int size) {
            this.size = size;
            this.parentsOrSize = new int[size];
            Arrays.fill(parentsOrSize, -1);
        }

        public int mergeSet(int x, int y) {
            int i = root(x);
            int j = root(y);

            if (i == j) {
                return i;
            }
            //iのサイズが大きくなるように入れ替える
            if (-parentsOrSize[i] < -parentsOrSize[j]) {
                var temp = i;
                i = j;
                j = temp;
            }
            //iを代表としてxをマージする
            parentsOrSize[i] += parentsOrSize[j];
            parentsOrSize[j] = i;
            return i;
        }

        public int root(int x) {
            if (parentsOrSize[x] < 0) {
                return x;
            }
            parentsOrSize[x] = root(parentsOrSize[x]);
            return parentsOrSize[x];
        }

        public boolean isSameSet(int x, int y) {
            return root(x) == root(y);
        }

        public int size(int x) {
            return -parentsOrSize[root(x)];
        }

        public List<List<Integer>> groups() {
            var map = IntStream.range(0, size).boxed().collect(Collectors.groupingBy(i -> root(i)));
            return map.values().stream().toList();
        }

        @Override
        public String toString() {
            return this.parentsOrSize.toString();
        }

    }


    //重みつきUF
    public static class PotentialDisjointSetUnion {
        int size;
        int[] parentsOrSize;

        long[] diffWeight;

        public PotentialDisjointSetUnion(int size) {
            this.size = size;
            this.parentsOrSize = new int[size];
            this.diffWeight = new long[size];
            Arrays.fill(parentsOrSize, -1);
        }

        public int mergeSet(int x, int y, long w) {
            w += weight(x);
            w -= weight(y);
            int i = root(x);
            int j = root(y);

            if (i == j) {
                return i;
            }
            //iのサイズが大きくなるように入れ替える
            if (-parentsOrSize[i] < -parentsOrSize[j]) {
                var temp = i;
                i = j;
                j = temp;
                w = -w;
            }
            //iを代表としてxをマージする
            parentsOrSize[i] += parentsOrSize[j];
            parentsOrSize[j] = i;
            diffWeight[j] = w;
            return i;
        }

        public int root(int x) {
            if (parentsOrSize[x] < 0) {
                return x;
            }
            int root = root(parentsOrSize[x]);
            diffWeight[x] += diffWeight[parentsOrSize[x]];
            parentsOrSize[x] = root;
            return parentsOrSize[x];
        }

        public boolean isSameSet(int x, int y) {
            return root(x) == root(y);
        }

        public long diff(int x, int y) {
            return weight(y) - weight(x);
        }

        private long weight(int x) {
            root(x);
            return diffWeight[x];
        }

        public int size(int x) {
            return -parentsOrSize[root(x)];
        }

        public List<List<Integer>> groups() {
            var map = IntStream.range(0, size).boxed().collect(Collectors.groupingBy(i -> root(i)));
            return map.values().stream().toList();
        }

        @Override
        public String toString() {
            return this.parentsOrSize.toString();
        }

    }


    //可換モノイド　UF
    public static class WeightedDisjointSetUnion<T> {
        int size;
        int[] parentsOrSize;

        List<T> value;

        BinaryOperator<T> ope;

        public WeightedDisjointSetUnion(int size, T[] init, BinaryOperator<T> ope) {
            this.size = size;
            this.parentsOrSize = new int[size];
            this.ope = ope;
            Arrays.fill(parentsOrSize, -1);

            value = new ArrayList<>();
            for (int i = 0; i < init.length; i++) {
                value.add(init[i]);
            }
        }

        public int mergeSet(int x, int y) {
            int i = root(x);
            int j = root(y);

            if (i == j) {
                return i;
            }
            //iのサイズが大きくなるように入れ替える
            if (-parentsOrSize[i] < -parentsOrSize[j]) {
                var temp = i;
                i = j;
                j = temp;
            }
            //iを代表としてxをマージする
            parentsOrSize[i] += parentsOrSize[j];
            parentsOrSize[j] = i;

            //モノイド演算
            var m1 = value.get(i);
            var m2 = value.get(j);
            value.set(i, ope.apply(m1, m2));
            return i;
        }

        //連結成分要素に演算opeを作用した結果を取得する
        public T value(int x) {
            return value.get(root(x));
        }

        public int root(int x) {
            if (parentsOrSize[x] < 0) {
                return x;
            }
            parentsOrSize[x] = root(parentsOrSize[x]);
            return parentsOrSize[x];
        }

        public boolean isSameSet(int x, int y) {
            return root(x) == root(y);
        }

        public int size(int x) {
            return -parentsOrSize[root(x)];
        }

        public List<List<Integer>> groups() {
            var map = IntStream.range(0, size).boxed().collect(Collectors.groupingBy(i -> root(i)));
            return map.values().stream().toList();
        }

        @Override
        public String toString() {
            return this.parentsOrSize.toString();
        }

    }

    public static class UnionF {
        int[] elements;
        int[] rank;

        public UnionF(int size) {
            this.elements = new int[size];
            this.rank = new int[size];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = i;
                rank[i] = 0;
            }
        }

        public void mergeSet(int x, int y) {
            int i = root(x);

            int j = root(y);

            if (i == j) {
                return;
            }

            if (rank[i] > rank[j]) {
                elements[j] = i;

            } else {
                elements[i] = j;
                if (rank[i] == rank[j]) {
                    rank[i]++;
                }
            }
        }

        public int root(int x) {
            int i = x;
            while (elements[i] != i) {
                i = elements[i];
            }
            int root = i;
            //経路圧縮
            i = x;
            while (elements[i] != i) {
                i = elements[i];
                elements[i] = root;
            }
            return root;
        }

        public boolean isSameSet(int x, int y) {
            int i = root(x);

            int j = root(y);

            return i == j;
        }

        @Override
        public String toString() {
            return this.elements.toString();
        }

    }

    //俺自身がUnion-Findなんだ、のオブジェクト
    public static class UnionFindNode {
        //親への参照
        UnionFindNode parent;

        //自分のランク
        int rank;

        public UnionFindNode() {
            this.parent = this;
            this.rank = 0;
        }

        public UnionFindNode root() {
            if (this.parent == this) {
                return this;
            }

            UnionFindNode root = this.parent.root();
            //経路圧縮
            this.parent = root;
            return root;
        }

        public boolean isSameSet(UnionFindNode target) {
            UnionFindNode a = this.root();
            UnionFindNode b = target.root();
            return a == b;
        }

        public void union(UnionFindNode target) {
            UnionFindNode a = this.root();
            UnionFindNode b = target.root();
            if (a == b) {
                return;
            }
            if (a.rank < b.rank) {
                a.parent = b;
            } else {
                b.parent = a;
                if (a.rank == b.rank) {
                    a.rank++;
                }
            }
            return;
        }
    }

}
