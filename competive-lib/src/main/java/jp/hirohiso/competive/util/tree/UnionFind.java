package jp.hirohiso.competive.util.tree;

public class UnionFind {

    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ

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

        public boolean isSameSet(UnionFindNode target){
            UnionFindNode a = this.root();
            UnionFindNode b = target.root();
            return a == b;
        }

        public void union(UnionFindNode target){
            UnionFindNode a = this.root();
            UnionFindNode b = target.root();
            if(a == b){
                return;
            }
            if(a.rank < b.rank){
                a.parent = b;
            }else{
                b.parent = a;
                if(a.rank == b.rank){
                    a.rank++;
                }
            }
            return;
        }
    }

}
