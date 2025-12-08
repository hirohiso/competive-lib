package jp.hirohiso.competive.util.range;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class RangeSetMain {
    public static void main(String[] args) {
        var rangeSet = new RangeSet();
        rangeSet.insert(new Range(1, 2));
        System.out.println(rangeSet);
        System.out.println(rangeSet.get(0));
        System.out.println(rangeSet.get(1));
        System.out.println(rangeSet.get(2));
        rangeSet.insert(new Range(3, 4));
        System.out.println(rangeSet);
        rangeSet.insert(new Range(4, 7));
        System.out.println(rangeSet);
        rangeSet.insert(new Range(10, 20));
        rangeSet.insert(new Range(21, 25));
        rangeSet.insert(new Range(8, 9));
        System.out.println(rangeSet);
        rangeSet.insert(new Range(9, 100));
        System.out.println(rangeSet);
        rangeSet.remove(new Range(10, 15));
        System.out.println(rangeSet);
        rangeSet.remove(new Range(20, 21));
        rangeSet.remove(new Range(23, 27));
        rangeSet.remove(new Range(30, 35));
        System.out.println(rangeSet);
        rangeSet.remove(new Range(13, 18));
        System.out.println(rangeSet);
        rangeSet.remove(new Range(25, 28));
        System.out.println(rangeSet);
        System.out.println(rangeSet.remove(new Range(20, 35)));
        System.out.println(rangeSet);
    }
}

//区間をSETで管理するやつ
//半開区間[l,r)
record Range(long l, long r) {
}

class RangeSet {
    private TreeSet<Range> set;

    public RangeSet() {
        set = new TreeSet<>(Comparator.comparingLong(r -> r.l()));
        //番兵を入れる
        set.add(new Range(Long.MIN_VALUE, Long.MIN_VALUE));
        set.add(new Range(Long.MAX_VALUE, Long.MAX_VALUE));
    }
    public RangeSet and(RangeSet other) {
        var ret = new RangeSet();
        // 番兵を無視するために、次の要素からイテレータを開始する
        var itA = this.set.iterator();
        var itB = other.set.iterator();

        // 番兵をスキップ
        if (itA.hasNext()) itA.next();
        if (itB.hasNext()) itB.next();

        if (!itA.hasNext() || !itB.hasNext()) {
            return ret; // どちらかが空なら共通区間なし
        }

        var currentA = itA.next();
        var currentB = itB.next();

        // どちらかのイテレータが終端に達するまで繰り返す
        while (true) {
            // 1. 共通区間の開始点を計算
            long start = Math.max(currentA.l(), currentB.l());

            // 2. 共通区間の終了点を計算
            long end = Math.min(currentA.r(), currentB.r());

            // 3. 共通区間が存在するかチェック
            // 区間が [l, r) または [l, r] のどちらを意味するかで条件は変わるが、
            // 提示されたコードは終了点が小さい方を採用しているため、
            // end > start または end >= start を使う (ここでは end > start を仮定)
            if (start < end) {
                ret.insert(new Range(start, end));
            }

            // 4. 次に進める区間を決定
            // 終了点が小さい方の区間を次に進める
            if (currentA.r() <= currentB.r()) {
                if (!itA.hasNext()) break;
                currentA = itA.next();
            } else {
                if (!itB.hasNext()) break;
                currentB = itB.next();
            }
        }

        return ret;
    }

    //xを含むRangeを返却する
    //存在しない場合はnull
    public Range get(long x) {
        var r = new Range(x, x);
        var pre = set.floor(r);
        if (pre.l() <= x && x < pre.r()) {
            return pre;
        } else {
            return null;
        }
    }

    public Range getLeft(long x) {
        var r = new Range(x, x);
        var pre = set.headSet(r, true).descendingSet();
        for (var range : pre) {
            if (range.r() <= x) {
                return range;
            }
        }
        return null;
    }

    public Range getRight(long x) {
        var r = new Range(x, x);
        var pre = set.tailSet(r, false);
        for (var range : pre) {
            if (range.l() > x) {
                return range;
            }
        }
        return null;
    }


    //rangeを追加する
    public void insert(Range range) {
        var pre = set.floor(range);
        var next = set.higher(range);
        if (pre.r() < range.l() && range.r() < next.l()) {
            set.add(range);
            return;
        }

        if (range.l() <= pre.r()) {
            set.remove(pre);
            range = new Range(pre.l(), range.r());
        }
        while (range.r() >= next.l()) {
            set.remove(next);
            range = new Range(range.l(), Math.max(range.r(), next.r()));
            next = set.higher(range);
        }
        set.add(range);
    }

    public Set<Range> remove(Range range) {
        var pre = set.floor(new Range(range.l(), range.l()));
        var next = set.higher(new Range(range.r(), range.r()));

        var sub = set.subSet(pre, true, next, false);
        var list = sub.stream().toList();

        var ret = new HashSet<Range>();
        for (var ran : list) {
            var maxl = Math.max(ran.l(), range.l());
            var minr = Math.min(ran.r(), range.r());
            if (maxl < minr) {
                set.remove(ran);
                if (ran.l() < range.l() && range.r() < ran.r()) {
                    set.add(new Range(ran.l(), range.l()));
                    set.add(new Range(range.r(), ran.r()));
                    ret.add(new Range(range.l(), range.r()));
                } else if (ran.l() < range.l()) {
                    set.add(new Range(ran.l(), range.l()));
                    ret.add(new Range(range.l(), ran.r()));
                } else if (range.r() < ran.r()) {
                    set.add(new Range(range.r(), ran.r()));
                    ret.add(new Range(ran.l(), range.r()));
                } else {
                    ret.add(new Range(ran.l(), ran.r()));
                }
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return "RangeSet{" + "set=" + set + '}';
    }
}

