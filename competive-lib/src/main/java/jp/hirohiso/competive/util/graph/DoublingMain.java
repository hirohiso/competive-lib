package jp.hirohiso.competive.util.graph;

import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class DoublingMain {

    public static void main(String[] args) {

    }


    // 2 ^ k先の頂点を示す関数f(u,k)
    // 2 ^ kまでの辺に付与されたモノイドの総和を関数g(u,k)で表す時
    //f(u, K + 1) := f(f(u,k),k)
    //g(u, k + 1) := g(u , k) + g(f(u,k),k) でダブリングによる事前計算を行い
    //各種 g(u,k)をlog kで回答する
    /**
     * 各頂点からの一意な遷移と通過値の集約を前処理する。
     * <p>N は正、頂点は [0,N)。set 後に build を呼び、range(u,gene) で
     * gene 回分を順序通りに集約する。gene=0 は単位元。
     * 対応する gene は {@code 0 <= gene < 2^baseSize}。段数は頂点数由来なので
     * 任意の巨大な遷移回数には対応しない。構築・領域 O(N log N)、照会 O(log N)。
     * func は結合的で e が単位元、非可換でもよい。</p>
     */
    static class Doubling<T> {
        int[][] parent;
        int baseSize;
        T[][] doublingValue;
        BinaryOperator<T> func;
        Supplier<T> e;

        @SuppressWarnings("unchecked")
        public Doubling(int size, BinaryOperator<T> func, Supplier<T> e) {
            baseSize = 1;
            while (1 << baseSize < size) {
                baseSize++;
            }

            parent = new int[size][baseSize];
            for (int i = 0; i < parent.length; i++) {
                parent[i][0] = i;//自分に遷移
            }
            doublingValue = (T[][]) new Object[size][baseSize];
            for (var arr : doublingValue) {
                arr[0] = e.get(); //単位元設定
            }
            this.e = e;
            this.func = func;
        }

        public void set(int u, int p, T value) {
            parent[u][0] = p;
            doublingValue[u][0] = value;
        }

        public void build() {
            for (int base = 1; base < baseSize; base++) {
                for (int i = 0; i < parent.length; i++) {
                    parent[i][base] = parent[parent[i][base - 1]][base - 1];

                    doublingValue[i][base] = func.apply(
                            doublingValue[i][base - 1],
                            doublingValue[parent[i][base - 1]][base - 1]
                    );
                }
            }
        }

        //gene先の親までの集計値
        public T range(int u, int gene) {
            var v = e.get();
            var now = u;
            for (int m = 0; m < baseSize; m++) {
                if ((gene & (1 << m)) != 0) {
                    v = func.apply(v, doublingValue[now][m]);
                    now = parent[now][m];
                }
            }
            return v;
        }
    }
}
