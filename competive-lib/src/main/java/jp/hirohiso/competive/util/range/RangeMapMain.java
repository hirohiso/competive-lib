package jp.hirohiso.competive.util.range;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class RangeMapMain {
    public static void main(String[] args) {
        RangeMap<Integer> rm = new RangeMap<>(Integer::sum, () -> 0);

        rm.insert(new Range(1, 5), 2);
        rm.insert(new Range(3, 7), 10);
        rm.insert(new Range(6, 8), 3);

        System.out.println(rm);
        // => {[-9223372036854775808,-9223372036854775808)=0, [1,3)=2, [3,5)=12, [5,6)=10, [6,8)=13, [9223372036854775807,9223372036854775807)=0}

        System.out.println(rm.get(4)); // RangeVal([3,5)=12)
        System.out.println(rm.getLeft(3)); // RangeVal([1,3)=2)
        System.out.println(rm.getRight(5)); // RangeVal([6,8)=13)

        var removed = rm.remove(new Range(4, 7));
        System.out.println("removed: " + removed);
        System.out.println(rm);
    }

    // 半開区間 [l, r)
    record Range(long l, long r) {
        @Override
        public String toString() {
            return "[" + l + "," + r + ")";
        }
    }

    // 値付きRange
    record RangeVal<T>(Range range, T value) {
        @Override
        public String toString() {
            return range + "=" + value;
        }
    }

    static class RangeMap<T> {
        private final TreeMap<Range, T> map;
        private final BinaryOperator<T> op; // モノイド演算
        private final Supplier<T> e;        // 単位元（番兵用など）

        public RangeMap(BinaryOperator<T> op, Supplier<T> e) {
            this.map = new TreeMap<>((a, b) -> {
                if (a.l() != b.l()) return Long.compare(a.l(), b.l());
                return Long.compare(a.r(), b.r());
            });
            this.op = op;
            this.e  = e;
            // 番兵（長さ0）
            map.put(new Range(Long.MIN_VALUE, Long.MIN_VALUE), e.get());
            map.put(new Range(Long.MAX_VALUE, Long.MAX_VALUE), e.get());
        }

        /** x を含む区間（なければ null） */
        public RangeVal<T> get(long x) {
            var ent = map.floorEntry(new Range(x, x));
            if (ent != null && ent.getKey().l() <= x && x < ent.getKey().r()) {
                return new RangeVal<>(ent.getKey(), ent.getValue());
            }
            return null; // RangeSet の get と同じ挙動
        }

        /** x より左の隣接（常に非 null：番兵が返る可能性あり） */
        public RangeVal<T> getLeft(long x) {
            var ent = map.floorEntry(new Range(x, x));
            while (ent != null && ent.getKey().r() > x) {
                ent = map.lowerEntry(ent.getKey());
            }
            return new RangeVal<>(ent.getKey(), ent.getValue()); // 番兵を含む
        }

        /** x より右の隣接（常に非 null：番兵が返る可能性あり） */
        public RangeVal<T> getRight(long x) {
            // l > x を保証したいので (x, Long.MAX_VALUE) より大きいキーを取得
            var ent = map.higherEntry(new Range(x, Long.MAX_VALUE));
            return new RangeVal<>(ent.getKey(), ent.getValue()); // 番兵を含む
        }

        /** 区間挿入：重複部は op を適用、空白部は v をそのまま埋める */
        public void insert(Range range, T v) {
            long l = range.l(), r = range.r();
            if (l >= r) return;

            // 既存区間を境界 l, r で「切る」
            splitAt(l);
            splitAt(r);

            // [l, r) を左から走査：既存なら op、空白ならそのまま挿入
            long pos = l;
            while (pos < r) {
                var ent = map.ceilingEntry(new Range(pos, pos));
                // 空白（次の区間の開始 > pos）を v で埋める
                if (ent == null || ent.getKey().l() > pos) {
                    long end = (ent == null ? r : Math.min(r, ent.getKey().l()));
                    map.put(new Range(pos, end), v);
                    pos = end;
                } else {
                    // ent.getKey().l() == pos を満たす（事前分割済みなので必ず一致）
                    var key = ent.getKey();
                    long end = Math.min(r, key.r());
                    T newVal = op.apply(ent.getValue(), v);
                    if (!Objects.equals(newVal, ent.getValue())) {
                        map.put(key, newVal); // キーはそのまま値を上書き
                    }
                    pos = end;
                }
            }

            // 断片化を軽減：同値の隣接区間を併合
            coalesceRange(l, r);
        }

        /** 区間削除：削除された部分（区間＋値）を返す */
        public Set<RangeVal<T>> remove(Range range) {
            long l = range.l(), r = range.r();
            var removed = new LinkedHashSet<RangeVal<T>>();
            if (l >= r) return removed;

            splitAt(l);
            splitAt(r);

            // [l, r) に完全に入る区間を順に削除
            for (;;) {
                var ent = map.ceilingEntry(new Range(l, l));
                if (ent == null || ent.getKey().l() >= r) break;
                removed.add(new RangeVal<>(ent.getKey(), ent.getValue()));
                map.remove(ent.getKey());
            }

            // 端で同値が隣接していれば併合
            coalesceRange(l, r);
            return removed;
        }

        // --- helpers ------------------------------------------------------------

        /** 点 x を内部に含む区間を左右に分割する（番兵や境界は無視） */
        private void splitAt(long x) {
            var ent = map.floorEntry(new Range(x, x));
            if (ent == null) return;
            var rg = ent.getKey();
            if (rg.l() < x && x < rg.r()) {
                T val = ent.getValue();
                map.remove(rg);
                map.put(new Range(rg.l(), x), val);
                map.put(new Range(x, rg.r()), val);
            }
        }

        /** [l, r) 近傍で、同じ値の隣接区間を可能な限り併合 */
        private void coalesceRange(long l, long r) {
            var ent = map.floorEntry(new Range(l, l));
            if (ent == null) ent = map.firstEntry();

            while (ent != null) {
                var cur = ent;
                var nxt = map.higherEntry(cur.getKey());
                if (nxt == null) break;

                // どちらも長さ>0（番兵でない）かつ r が連結、値が等しければ併合
                var a = cur.getKey();
                var b = nxt.getKey();
                if (a.l() < a.r() && b.l() < b.r() &&
                        a.r() == b.l() &&
                        Objects.equals(cur.getValue(), nxt.getValue())) {
                    T v = cur.getValue();
                    map.remove(a);
                    map.remove(b);
                    var merged = new Range(a.l(), b.r());
                    map.put(merged, v);
                    // ent は merged の floor を取り直す
                    ent = map.floorEntry(merged);
                } else {
                    if (b.l() >= r) break;
                    ent = nxt;
                }
            }
        }

        @Override
        public String toString() {
            return map.toString();
        }
    }
}




