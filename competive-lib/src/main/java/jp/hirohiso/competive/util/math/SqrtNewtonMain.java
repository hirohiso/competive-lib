package jp.hirohiso.competive.util.math;

public class SqrtNewtonMain {

    public static void main(String[] args) {
        for (long i = 0; i < 100000000; i++) {
            var x = isqrt(i * i);
            if (x != i) {
                System.out.println("error: " + i + " " + x);
            }
        }
    }

    private static long isqrt(long n) {
        var x = n;
        var y = (x + 1) / 2;
        while (y < x) {
            x = y;
            y = (n / y + y) / 2;
        }
        return x;
    }
}
