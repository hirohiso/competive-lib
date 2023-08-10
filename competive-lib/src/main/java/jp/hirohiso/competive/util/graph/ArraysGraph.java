package jp.hirohiso.competive.util.graph;

public class ArraysGraph {

    /**
     * 配列で構築したグラフ
     */
    public static class ArrayGraph{

        private final int size;

        private final boolean[][] edge;

        public ArrayGraph(int n){
            size = n;
            edge = new boolean[n][n];
        }

        /**
         *
         * @param a 0 <= a < size
         * @param b 0 <= b < size
         */
        public void  addEdge(int a,int b){
            edge[a][b] = true;
            edge[b][a] = true;
        }
    }
}
