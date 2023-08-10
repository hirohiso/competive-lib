package jp.hirohiso.competive.util.graph;

public class MetaGraph {

    public static void main(String[] args) {

    }

    public static class Graph<T>{
        private Object[] metaData;
        private final int graphSize;
        private int edge[][];

        public Graph(int size){
            this.metaData = new Object[size];
            this.graphSize = size;
        }

        public void associateMetaData(int index, T data){
            setMetaData(index,data);
        }

        private void setMetaData(int index,T data){
            this.metaData[index] = data;
        }
        private T getMetaData(int index){
            return (T)this.metaData[index];
        }
    }
}
