package jp.hirohiso.competive.util.math;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class SortedPrefixMain {
    static void main() {
        {
            //使用例：文字列キー + 和モノイド
            var entries = List.of(
                    new GroupRangeQuery.Entry<>("cat", 4L),
                    new GroupRangeQuery.Entry<>("apple", 10L),
                    new GroupRangeQuery.Entry<>("dog", 2L),
                    new GroupRangeQuery.Entry<>("apple", 3L)
            );

            var rq = new GroupRangeQuery<String, Long>(
                    entries,
                    Comparator.naturalOrder(),
                    0L,
                    Long::sum,
                    x -> -x
            );

            System.out.println(rq.query("apple", "cat")); // 17
            System.out.println(rq.query("cat", "dog"));   // 6
            System.out.println(rq.query("bee", "cow"));   // 4

        }

        {

        }

    }
    public static class GroupRangeQuery<K, V> {
        public record Entry<K, V>(K key, V value) {}

        private final List<K> keys;
        private final List<V> prefix;
        private final Comparator<? super K> cmp;
        private final BinaryOperator<V> op;
        private final UnaryOperator<V> inverse;

        public GroupRangeQuery(
                List<Entry<K, V>> entries,
                Comparator<? super K> cmp,
                V identity,
                BinaryOperator<V> op,
                UnaryOperator<V> inverse
        ) {
            this.cmp = cmp;
            this.op = op;
            this.inverse = inverse;

            var sorted = new ArrayList<>(entries);
            sorted.sort((a, b) ->
                    cmp.compare(a.key(), b.key()));

            keys = new ArrayList<>(sorted.size());
            prefix = new ArrayList<>(sorted.size() + 1);

            V acc = identity;
            prefix.add(acc);

            for (var e : sorted) {
                keys.add(e.key());
                acc = op.apply(acc, e.value());
                prefix.add(acc);
            }
        }

        private int lowerBound(K key) {
            int l = 0, r = keys.size();
            while (l < r) {
                int m = (l + r) >>> 1;
                if (cmp.compare(keys.get(m), key) < 0)
                    l = m + 1;
                else
                    r = m;
            }
            return l;
        }

        private int upperBound(K key) {
            int l = 0, r = keys.size();
            while (l < r) {
                int m = (l + r) >>> 1;
                if (cmp.compare(keys.get(m), key) <= 0)
                    l = m + 1;
                else
                    r = m;
            }
            return l;
        }

        // a <= key <= b の集約
        public V query(K a, K b) {
            int l = lowerBound(a);
            int r = upperBound(b);

            if (l >= r) {
                return prefix.get(0);
            }

            return op.apply(
                    inverse.apply(prefix.get(l)),
                    prefix.get(r)
            );
        }
    }

}
