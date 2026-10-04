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
    /**
     * モノイドのキュー集約（SWAG）。
     * <p>push で末尾追加、pop で先頭削除、cal で先頭から末尾への集約。
     * 演算は結合的で e は単位元。非可換でもよい。空キューの cal は e、pop は非空時のみ。
     * 要素数 N に対して領域 O(N)、push/pop は償却 O(1)、cal は O(1)。
     * 演算は引数を破壊しないこと。</p>
     */
    static class SlidingWindowAggregation<T> {

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
                    var sum = back.isEmpty() ? p.n : ope.apply(p.n, back.peekLast().sum);
                    back.addLast(new SlidingAggregationPair(p.n, sum));
                }
            }
            var x = back.pollLast();
            return x.n;
        }

        public T cal() {
            var f = front.peekLast();
            var b = back.peekLast();

            return ope.apply(
                    b != null ? b.sum : e.get(),
                    f != null ? f.sum : e.get()
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
