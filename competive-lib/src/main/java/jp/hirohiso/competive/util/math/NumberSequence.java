package jp.hirohiso.competive.util.math;

public class NumberSequence {
}

/**
 * 　等差数列
 *
 * @param a0 初項
 * @param d  　公差
 */
record ArithmeticSequence(long a0, long d) {
    //n項目までの和
    public long sum(int n) {
        return (n * (2 * a0 + (n - 1) * d)) / 2;
    }

    //第n項
    public long get(int n) {
        return a0 + (n - 1) * d;
    }
}
