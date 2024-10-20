package jp.hirohiso.competive.util.range;

import java.util.*;
import java.util.function.BinaryOperator;

public class PriorityRangeSumMain {
    public static void main(String[] args) {
        var prs = new PriorityRangeSum(3);

        var prs2 = new PriorityRangeSumT<Long>(3,
                Comparator.naturalOrder(),//並び順
                (x, y) -> x + y, //加算
                (x, y) -> x - y,//減算
                0l//単位元
        );

        var rnd = new Random();
        var list = new LinkedList<Integer>();
        for (int i = 0; i < 10; i++) {
            var v = rnd.nextInt(10);
            list.add(v);
            Collections.sort(list);
            prs.push(v);
            prs2.push((long) v);
            System.out.println("list:" + list);
            System.out.println("sum:" + prs.sum());
            System.out.println("sum2:" + prs2.sum());
        }

        for (var n : list) {
            prs.pop(n);
            prs2.pop((long) n);
            System.out.println(prs.sum());
            System.out.println(prs2.sum());
        }
    }

    //区間内の昇順top Kのみをsumする
    private static class PriorityRangeSum {

        private PriorityQueue<Long> inK;//K以内の要素集合
        private PriorityQueue<Long> outK;//K超過の要素集合
        private PriorityQueue<Long> del_inK;//inK内の遅延削除する要素集合;
        private PriorityQueue<Long> del_outK;//outK内の遅延削除する要素集合;

        private int sum = 0;
        private int K;

        public PriorityRangeSum(int K) {
            this.inK = new PriorityQueue<>(Comparator.reverseOrder());
            this.outK = new PriorityQueue<>();
            this.del_inK = new PriorityQueue<>(Comparator.reverseOrder());
            this.del_outK = new PriorityQueue<>();
            this.K = K;
        }

        public void push(long x) {
            inK.add(x);
            sum += x;
            if (sizeIn() > K) {
                var n = peekIn();
                sum -= n;
                inK.poll();
                outK.add(n);
            }
        }

        public void pop(long x) {
            if (x <= peekIn()) {
                del_inK.add(x);
                sum -= x;
            } else {
                del_outK.add(x);
            }

            if (sizeIn() < K && sizeOut() != 0) {
                var n = peekOut();
                sum += n;
                outK.poll();
                inK.add(n);
            }
        }

        public long size() {
            return this.sizeIn();
        }

        private int sizeIn() {
            return this.inK.size() - this.del_inK.size();
        }

        private int sizeOut() {
            return this.outK.size() - this.del_outK.size();
        }

        private long peekIn() {
            while (del_inK.size() != 0 && del_inK.peek() == inK.peek()) {
                del_inK.poll();
                inK.poll();
            }
            return inK.peek();
        }

        private long peekOut() {
            while (del_outK.size() != 0 && del_outK.peek() == outK.peek()) {
                del_outK.poll();
                outK.poll();
            }
            return outK.peek();
        }

        public long sum() {
            return this.sum;
        }
    }


    //ジェネリクスversion
    private static class PriorityRangeSumT<T> {

        private PriorityQueue<T> inK;//K以内の要素集合
        private PriorityQueue<T> outK;//K超過の要素集合
        private PriorityQueue<T> del_inK;//inK内の遅延削除する要素集合;
        private PriorityQueue<T> del_outK;//outK内の遅延削除する要素集合;

        private T sum;
        private int K;

        private BinaryOperator<T> add;
        private BinaryOperator<T> minus;
        private Comparator<T> comparator;

        public PriorityRangeSumT(int K, Comparator<T> comparator, BinaryOperator<T> ope1, BinaryOperator<T> ope2, T zero) {
            this.inK = new PriorityQueue<>(comparator.reversed());
            this.outK = new PriorityQueue<>(comparator);
            this.del_inK = new PriorityQueue<>(comparator.reversed());
            this.del_outK = new PriorityQueue<>(comparator);
            this.K = K;
            this.add = ope1;
            this.minus = ope2;
            this.sum = zero;
            this.comparator = comparator;
        }

        public void push(T x) {
            inK.add(x);
            sum = add.apply(sum, x);
            if (sizeIn() > K) {
                var n = peekIn();
                sum = minus.apply(sum, n);
                inK.poll();
                outK.add(n);
            }
        }

        public void pop(T x) {
            if (this.comparator.compare(x, peekIn()) <= 0) {
                del_inK.add(x);
                sum = minus.apply(sum, x);
                ;
            } else {
                del_outK.add(x);
            }

            if (sizeIn() < K && sizeOut() != 0) {
                var n = peekOut();
                sum = add.apply(sum, n);
                outK.poll();
                inK.add(n);
            }
        }

        public long size() {
            return this.sizeIn();
        }

        private int sizeIn() {
            return this.inK.size() - this.del_inK.size();
        }

        private int sizeOut() {
            return this.outK.size() - this.del_outK.size();
        }

        private T peekIn() {
            while (del_inK.size() != 0 && del_inK.peek() == inK.peek()) {
                del_inK.poll();
                inK.poll();
            }
            return inK.peek();
        }

        private T peekOut() {
            while (del_outK.size() != 0 && del_outK.peek() == outK.peek()) {
                del_outK.poll();
                outK.poll();
            }
            return outK.peek();
        }

        public T sum() {
            return this.sum;
        }
    }

}
