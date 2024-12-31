package jp.hirohiso.competive.util.range;

import java.util.Comparator;
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
        rangeSet.remove(new Range( 10 ,15));
        System.out.println(rangeSet);
        rangeSet.remove(new Range( 20 ,21));
        rangeSet.remove(new Range( 23 ,27));
        rangeSet.remove(new Range( 30 ,35));
        System.out.println(rangeSet);
        rangeSet.remove(new Range( 13 ,18));
        System.out.println(rangeSet);
        rangeSet.remove(new Range( 25 ,28));
        System.out.println(rangeSet);
        rangeSet.remove(new Range( 20 ,35));
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

    public void remove(Range range) {
        var pre = set.floor(new Range(range.l(), range.l()));
        var next = set.higher(new Range(range.r(), range.r()));

        var sub = set.subSet(pre, true, next, false);
        var list = sub.stream().toList();
        for (var ran : list) {
            var maxl = Math.max(ran.l(), range.l());
            var minr = Math.min(ran.r(), range.r());
            if (maxl < minr) {
                set.remove(ran);
                if (ran.l() < range.l() && range.r() < ran.r()) {
                    set.add(new Range(ran.l(), range.l()));
                    set.add(new Range(range.r(), ran.r()));
                } else if (ran.l() < range.l()) {
                    set.add(new Range(ran.l(), range.l()));
                } else if (range.r() < ran.r()) {
                    set.add(new Range(range.r(), ran.r()));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "RangeSet{" +
                "set=" + set +
                '}';
    }
}