package jp.hirohiso.competive.util.graph;

import java.util.*;
import java.util.function.*;

public class xxxFastSearchMain {

    public static void main(String[] args) {

    }
    //DFS抽象化
    static class Dfs<S> {

        //状態sから隣接状態s'の集合を取得する関数
        private final Function<S, List<S>> getNS;
        private final ToIntFunction<S> getHash;
        private final IntFunction<S> getState;

        //状態sに訪問した時に実施する関数
        Consumer<S> operate;
        //状態sから戻る時に実施する関数 第二引数に子の状態を格納する
        BiConsumer<S, List<S>> operateReturn;

        //状態sに訪問したこと示すハッシュ
        private final boolean[] visited;

        //状態sに訪問したときの深さ
        private final int[] level;
        //状態sの親s
        private final int[] parents;

        private final int size;

        public Dfs(int size,
                   ToIntFunction<S> getHash,
                   IntFunction<S> getState,
                   Function<S, List<S>> getNS
        ) {
            this.size = size;
            this.getHash = getHash;
            this.getState = getState;
            this.getNS = getNS;
            this.visited = new boolean[size];
            this.level = new int[size];
            Arrays.fill(level, -1);
            this.parents = new int[size];
            Arrays.fill(parents, -1);
        }

        public void solve(S s) {
            var hash = getHash.applyAsInt(s);
            this.level[hash] = 0;
            this.visited[hash] = true;
            dfs(s);
        }

        private void dfs(S s) {
            var hash = getHash.applyAsInt(s);
            if (operate != null) {
                operate.accept(s);
            }
            var children = new LinkedList<S>();

            //隣接状態を取得
            for (var ns : getNS.apply(s)) {
                var nsHash = getHash.applyAsInt(ns);
                if (!visited[nsHash]) {
                    visited[nsHash] = true;
                    children.add(ns);
                    var pl = level[hash];
                    level[nsHash] = pl + 1;
                    parents[nsHash] = hash;
                    dfs(ns);
                    //パス列挙の場合はfalseに戻す。親が更新されることに注意
                    //visited[nsHash] = false;
                }
            }
            //帰りがけに呼び出す関数
            if (operateReturn != null) {
                operateReturn.accept(s, children);
            }
        }
    }

    //BFS抽象化
    static class Bfs<S> {
        //状態sから隣接状態s'の集合を取得する関数
        Function<S, List<S>> getNS;
        ToIntFunction<S> getHash;
        IntFunction<S> getState;

        //状態sに訪問した時に実施する関数

        //状態sに訪問したこと示すハッシュ
        private final boolean[] visited;

        //状態sに訪問したときの深さ
        private final int[] level;
        int size = 0;

        //状態sの親s
        int[] parents;

        /**
         * 抽象化された幅優先探索
         *
         * @param size     状態数 Integer.MAX_VALUEを指定すると、状態数を気にせずに探索できる
         * @param getHash  状態sのハッシュ値を取得する関数
         * @param getState ハッシュ値から状態sを取得する関数
         * @param getNS    状態sから隣接状態s'の集合を取得する関数
         */
        public Bfs(int size,
                   ToIntFunction<S> getHash,
                   IntFunction<S> getState,
                   Function<S, List<S>> getNS) {
            this.getNS = getNS;
            this.visited = new boolean[size];
            this.level = new int[size];
            Arrays.fill(this.level, -1);
            this.parents = new int[size];
            Arrays.fill(this.parents, -1);
            this.size = size;
            this.getHash = getHash;
            this.getState = getState;
        }

        public void solve(S s) {
            bfs(s);
        }

        public int level(S s) {
            var hash = getHash.applyAsInt(s);
            return level[hash];
        }

        public S parent(S s) {
            var hash = getHash.applyAsInt(s);
            return getState.apply(parents[hash]);
        }

        private void bfs(S s) {
            var hash = getHash.applyAsInt(s);
            ;
            visited[hash] = true;
            level[hash] = 0;
            var stack = new LinkedList<S>();
            stack.add(s);
            while (!stack.isEmpty()) {
                var ps = stack.pollFirst();
                var nowHash = getHash.applyAsInt(ps);
                var pl = level[nowHash];
                //隣接状態を取得
                for (var ns : getNS.apply(ps)) {
                    var nsHash = getHash.applyAsInt(ns);
                    if (!visited[nsHash]) {
                        visited[nsHash] = true;
                        stack.addLast(ns);
                        level[nsHash] = pl + 1;
                        parents[nsHash] = nowHash;
                    }
                }
            }
        }
    }

}
