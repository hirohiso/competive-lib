package jp.hirohiso.competive.util.math;

import java.util.Arrays;

public class Polygon2DMain {
    public static void main(String[] args) {

    }
    //多角形を表すクラス
    //頂点を反時計回りに保持して、与えられた頂点の面積を高速に求める
    //参考：https://imagingsolution.net/math/calc_n_point_area/
    class Polygon2D {
        LongPair[] points;
        int N;//頂点数
        long[] acc;//外積の累積和
        long sum; //多角形の面積

        //反時計回りで与えられる頂点
        public Polygon2D(LongPair[] points) {
            this.points = points;
            this.N = points.length;

            var cp = new long[N];
            for (int i = 0; i < N; i++) {
                var p = points[i % N];
                var q = points[(i + 1) % N];
                var s = (p.a * q.b - p.b * q.a); //内部では2倍の面積で保持数
                cp[i] = s;
            }
            sum = Arrays.stream(cp).sum();
            acc = new long[N + 1];
            acc[0] = 0L;
            for (int i = 1; i < N; i++) {
                acc[i] = acc[i - 1] + cp[i - 1];
            }
        }

        //頂点lから反時計回りに頂点rまで加えた時の多角形の面積。2倍にして返却
        // 0 <= l,r < N
        public long area(int l, int r) {
            //rからlへの外積
            var p = points[r];
            var q = points[l];
            var s = (p.a * q.b - p.b * q.a);

            if (l <= r) {
                return acc[r] - acc[l] + s;
            } else {
                return (sum - (acc[l] - acc[r]) + s);
            }
        }
    }
    record LongPair(long a, long b) {
    }
}
