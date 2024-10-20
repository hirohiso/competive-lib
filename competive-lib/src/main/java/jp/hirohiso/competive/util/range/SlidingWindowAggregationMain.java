package jp.hirohiso.competive.util.range;

import java.util.LinkedList;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class SlidingWindowAggregationMain {
    public static void main(String[] args) {
        var swag = new SlidingWindowAggregation<Long>(
                (x, y) -> Math.min(x, y),
                () -> Long.MAX_VALUE
        );

        swag.push(5l);
        swag.push(2l);
        swag.push(3l);
        swag.push(8l);
        System.out.println(swag.cal());
        swag.pop();
        System.out.println(swag.cal());
        swag.push(4l);
        System.out.println(swag.cal());
        swag.pop();
        System.out.println(swag.cal());
        swag.pop();
        System.out.println(swag.cal());
    }


    //モノイドTを載せたSWAG
    private static class SlidingWindowAggregation<T> {

        LinkedList<SlidingAggregationPair> front;
        LinkedList<SlidingAggregationPair> back;

        BinaryOperator<T> ope;
        Supplier<T> e;

        //ope : 加算
        //e : 単位元
        public SlidingWindowAggregation(BinaryOperator<T> ope, Supplier<T> e) {
            this.front = new LinkedList<>();
            this.back = new LinkedList<>();
            this.ope = ope;
            this.e = e;
        }

        //要素の追加
        public void push(T x) {
            pushList(x, this.front);
        }

        private void pushList(T x, LinkedList<SlidingAggregationPair> list) {
            if (list.isEmpty()) {
                var sum = ope.apply(e.get(), x);
                var pair = new SlidingAggregationPair(x, sum);
                list.addLast(pair);
            } else {
                var sum = ope.apply(list.getLast().sum, x);
                var pair = new SlidingAggregationPair(x, sum);
                list.addLast(pair);
            }
        }

        //要素の削除
        public T pop() {
            if (back.isEmpty()) {
                while (!front.isEmpty()) {
                    var p = front.pollLast();
                    pushList(p.n, back);
                }
            }
            var x = back.pollLast();
            return x.n;
        }

        public T cal() {
            var f = front.peekLast();
            var b = back.peekLast();

            return ope.apply(
                    f != null ? f.sum : e.get(),
                    b != null ? b.sum : e.get()
            );
        }

        private class SlidingAggregationPair {
            T n;
            T sum;

            public SlidingAggregationPair(T n, T sum) {
                this.n = n;
                this.sum = sum;
            }
        }
    }
}
